package com.pokemongame.util;

import com.pokemongame.entity.Player;
import com.pokemongame.item.Item;
import com.pokemongame.pokemon.Move;
import com.pokemongame.pokemon.Pokemon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {

    public static int playerMoney = 0;
    public static int lastWildPokeId = -1;

    // --- VARIABEL KUNCI UNTUK SISTEM MULTI-SLOT ---
    public static int currentSaveSlot = 1;

    // ==========================================================
    // --- FITUR SAVE/LOAD 4 SLOT UNTUK PAUSE MENU ---
    // ==========================================================
    public static void saveGameToSlot(Player player, int slotNumber) {
        currentSaveSlot = slotNumber; // Ubah target database ke slot yang dipilih
        System.out.println("Menyimpan ke Slot " + slotNumber + "...");

        saveGame(player); // Panggil fungsi save utama

        // Bikin file .txt untuk nampilin info singkat di Pause Menu
        try {
            int partySize = loadPlayerParty().size();
            String previewText = "$" + playerMoney + " | " + partySize + " Pkmn";

            java.io.File file = new java.io.File("slot_" + slotNumber + "_info.txt");
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(previewText);
            writer.close();

            System.out.println("Preview Slot " + slotNumber + " berhasil di-update!");
        } catch (Exception e) {
            System.out.println("Gagal save info preview slot " + slotNumber);
        }
    }

    public static void loadGameFromSlot(Player player, int slotNumber) {
        currentSaveSlot = slotNumber; // Ubah target database ke slot yang dipilih
        System.out.println("Meload dari Slot " + slotNumber + "...");

        loadGame(player); // Panggil fungsi load utama
    }

    public static String getSlotPreviewInfo(int slotNumber) {
        try {
            java.io.File file = new java.io.File("slot_" + slotNumber + "_info.txt");
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                String info = "";
                if (scanner.hasNextLine()) {
                    info = scanner.nextLine();
                }
                scanner.close();
                return info;
            }
        } catch (Exception e) {
            return "ERROR";
        }
        return "EMPTY";
    }

    // ==========================================================
    // --- FUNGSI DATABASE UTAMA (UDAH DI-UPGRADE BUAT MULTI-SLOT) ---
    // ==========================================================
    public static void loadGame(Player player) {
        String sql = "SELECT world_x, world_y, money FROM player_save WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentSaveSlot);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    player.worldX = rs.getInt("world_x");
                    player.worldY = rs.getInt("world_y");
                    playerMoney = rs.getInt("money");
                    System.out.println("SUKSES LOAD SLOT " + currentSaveSlot + ": Duit $" + playerMoney);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat save game dari database!");
            e.printStackTrace();
        }
    }

    public static void saveGame(Player player) {
        if (player == null) {
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                return;
            }

            // 1. Cek apakah data di slot ini udah ada atau masih kosong?
            String checkSql = "SELECT id FROM player_save WHERE id = ?";
            boolean slotExists = false;
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, currentSaveSlot);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        slotExists = true;
                    }
                }
            }

            // 2. Eksekusi Overwrite (Timpa) ATAU Insert (Bikin Baru)
            if (slotExists) {
                // OVERWRITE: Timpa data save yang udah ada
                String updateSql = "UPDATE player_save SET world_x = ?, world_y = ?, money = ? WHERE id = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, player.worldX);
                    psUpdate.setInt(2, player.worldY);
                    psUpdate.setInt(3, playerMoney);
                    psUpdate.setInt(4, currentSaveSlot);
                    psUpdate.executeUpdate();
                    System.out.println("OVERWRITE SUKSES: Data di Slot " + currentSaveSlot + " berhasil ditimpa!");
                }
            } else {
                // INSERT: Isi slot yang tadinya bener-bener kosong
                String insertSql = "INSERT INTO player_save (id, world_x, world_y, money) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, currentSaveSlot);
                    psInsert.setInt(2, player.worldX);
                    psInsert.setInt(3, player.worldY);
                    psInsert.setInt(4, playerMoney);
                    psInsert.executeUpdate();
                    System.out.println("INSERT SUKSES: Data baru berhasil dibuat di Slot " + currentSaveSlot + "!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Pokemon loadPokemonById(int id, int level) {
        Pokemon p = null;
        String sql = "SELECT * FROM pokemon_base WHERE poke_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                return null;
            }
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String t1 = rs.getString("type1");
                    String t2 = rs.getString("type2");

                    int bHp = rs.getInt("base_hp");
                    int bAtk = rs.getInt("base_atk");
                    int bDef = rs.getInt("base_def");
                    int bSpAtk = rs.getInt("base_sp_atk");
                    int bSpDef = rs.getInt("base_sp_def");
                    int bSpd = rs.getInt("base_spd");

                    int finalHp = bHp + (level * 2) + 10;
                    int finalAtk = bAtk + (int) (level * 1.2);
                    int finalDef = bDef + (int) (level * 1.2);
                    int finalSpAtk = bSpAtk + (int) (level * 1.2);
                    int finalSpDef = bSpDef + (int) (level * 1.2);
                    int finalSpd = bSpd + (int) (level * 1.2);

                    p = new Pokemon(id, name, t1, level, finalHp, finalAtk, finalDef, finalSpAtk, finalSpDef, finalSpd);
                    if (t2 != null) {
                        p.setType2(t2);
                    }

                    loadMovesForPokemon(p, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static void loadMovesForPokemon(Pokemon p, Connection conn) throws SQLException {
        String sql = "SELECT mb.move_id FROM pokemon_moves pm "
                + "JOIN moves_base mb ON pm.move_id = mb.move_id "
                + "WHERE pm.poke_id = ? ORDER BY mb.power DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPokeId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Move m = getMoveById(rs.getInt("move_id"), conn);
                    if (m != null) {
                        p.addMove(m);
                    }
                }
            }
        }

        if (p.getMoves().isEmpty()) {
            Move tackle = getMoveByName("Tackle", conn);
            if (tackle != null) {
                p.addMove(tackle);
            }

            String sqlTipe = "SELECT move_id FROM moves_base WHERE type = ? AND name != 'Tackle' LIMIT 1";
            try (PreparedStatement psType = conn.prepareStatement(sqlTipe)) {
                psType.setString(1, p.getType1());
                try (ResultSet rsType = psType.executeQuery()) {
                    if (rsType.next()) {
                        Move elementalMove = getMoveById(rsType.getInt("move_id"), conn);
                        if (elementalMove != null) {
                            p.addMove(elementalMove);
                        }
                    }
                }
            }

            if (p.getType2() != null) {
                try (PreparedStatement psType2 = conn.prepareStatement(sqlTipe)) {
                    psType2.setString(1, p.getType2());
                    try (ResultSet rsType2 = psType2.executeQuery()) {
                        if (rsType2.next()) {
                            Move elementalMove2 = getMoveById(rsType2.getInt("move_id"), conn);
                            if (elementalMove2 != null) {
                                p.addMove(elementalMove2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static int getRandomPokemonId() {
        int randomId = 495;
        String sql = "SELECT poke_id FROM pokemon_base WHERE poke_id != ? ORDER BY RAND() LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lastWildPokeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    randomId = rs.getInt("poke_id");
                    lastWildPokeId = randomId;
                }
            }
        } catch (SQLException e) {
            System.err.println("EROR: Gagal mengambil ID Pokemon acak dari database!");
            e.printStackTrace();
        }
        return randomId;
    }

    public static void savePokemonStatus(Pokemon p) {
        String sql = "UPDATE player_pokemon SET level = ?, current_hp = ?, exp = ? WHERE player_id = ? AND poke_id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) {
                return;
            }
            ps.setInt(1, p.getLevel());
            ps.setInt(2, p.getCurrentHp());
            ps.setInt(3, p.getExp());
            ps.setInt(4, currentSaveSlot);
            ps.setInt(5, p.getPokeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Pokemon> loadPlayerParty() {
        List<Pokemon> party = new ArrayList<>();
        String sql = "SELECT poke_id, level, current_hp, exp, move1_id, move2_id, move3_id, move4_id FROM player_pokemon WHERE player_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                return party;
            }
            ps.setInt(1, currentSaveSlot);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pokemon p = loadPokemonById(rs.getInt("poke_id"), rs.getInt("level"));
                    if (p != null) {
                        p.setCurrentHp(rs.getInt("current_hp"));
                        p.setExp(rs.getInt("exp"));
                        p.getMoves().clear();

                        int[] moveIds = {
                            rs.getInt("move1_id"), rs.getInt("move2_id"),
                            rs.getInt("move3_id"), rs.getInt("move4_id")
                        };

                        for (int moveId : moveIds) {
                            if (moveId > 0) {
                                Move m = getMoveById(moveId, conn);
                                if (m != null) {
                                    p.getMoves().add(m);
                                }
                            }
                        }

                        if (p.getMoves().isEmpty()) {
                            loadMovesForPokemon(p, conn);
                        }

                        party.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return party;
    }

    public static List<Item> loadPlayerItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.name, i.effect_value, pi.quantity "
                + "FROM player_inventory pi "
                + "JOIN items i ON pi.item_id = i.item_id "
                + "WHERE pi.player_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                return items;
            }
            ps.setInt(1, currentSaveSlot);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(
                            rs.getString("name"),
                            rs.getInt("effect_value"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat item dari tabel player_inventory!");
            e.printStackTrace();
        }

        if (items.isEmpty()) {
            items.add(new Item("Potion", 20, 5));
        }

        return items;
    }

    public static boolean catchPokemon(int pokeId, int level, int currentHp) {
        String sql = "INSERT INTO player_pokemon (player_id, poke_id, level, current_hp, exp) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, currentSaveSlot);
                ps.setInt(2, pokeId);
                ps.setInt(3, level);
                ps.setInt(4, currentHp);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Move getMoveById(int moveId, Connection conn) {
        String sql = "SELECT name, power, accuracy, type, category FROM moves_base WHERE move_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, moveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String catStr = rs.getString("category");
                    Move.Category cat = Move.Category.valueOf(catStr);

                    return new Move(
                            rs.getString("name"),
                            rs.getInt("power"),
                            rs.getInt("accuracy"),
                            rs.getString("type"),
                            cat
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Move getMoveByName(String moveName, Connection conn) {
        String sql = "SELECT move_id FROM moves_base WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, moveName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getMoveById(rs.getInt("move_id"), conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void healAndReviveParty(List<Pokemon> party) {
        if (party == null || party.isEmpty()) {
            return;
        }

        for (Pokemon p : party) {
            p.setCurrentHp(p.getMaxHp());
            savePokemonStatus(p);
        }
        System.out.println("Semua Pokémon di Party berhasil di-Heal & Revive!");
    }

    public static void addItem(String itemName, int quantity) {
        String sqlGetId = "SELECT item_id FROM items WHERE name = ?";
        String sqlCheck = "SELECT quantity FROM player_inventory WHERE player_id = ? AND item_id = ?";
        String sqlUpdate = "UPDATE player_inventory SET quantity = quantity + ? WHERE player_id = ? AND item_id = ?";
        String sqlInsert = "INSERT INTO player_inventory (player_id, item_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                return;
            }

            int itemId = -1;
            try (PreparedStatement ps1 = conn.prepareStatement(sqlGetId)) {
                ps1.setString(1, itemName);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) {
                        itemId = rs1.getInt("item_id");
                    }
                }
            }

            if (itemId == -1) {
                System.out.println("EROR: Item '" + itemName + "' tidak ditemukan di database!");
                return;
            }

            boolean hasItem = false;
            try (PreparedStatement ps2 = conn.prepareStatement(sqlCheck)) {
                ps2.setInt(1, currentSaveSlot);
                ps2.setInt(2, itemId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        hasItem = true;
                    }
                }
            }

            if (hasItem) {
                try (PreparedStatement ps3 = conn.prepareStatement(sqlUpdate)) {
                    ps3.setInt(1, quantity);
                    ps3.setInt(2, currentSaveSlot);
                    ps3.setInt(3, itemId);
                    ps3.executeUpdate();
                }
            } else {
                try (PreparedStatement ps4 = conn.prepareStatement(sqlInsert)) {
                    ps4.setInt(1, currentSaveSlot);
                    ps4.setInt(2, itemId);
                    ps4.setInt(3, quantity);
                    ps4.executeUpdate();
                }
            }
            System.out.println("SUKSES: " + quantity + " " + itemName + " masuk ke tas database slot " + currentSaveSlot + "!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void consumeItem(String itemName) {
        String sqlGetId = "SELECT item_id FROM items WHERE name = ?";
        String sqlUpdate = "UPDATE player_inventory SET quantity = quantity - 1 WHERE player_id = ? AND item_id = ? AND quantity > 0";
        String sqlDelete = "DELETE FROM player_inventory WHERE player_id = ? AND item_id = ? AND quantity <= 0";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) {
                return;
            }

            int itemId = -1;
            try (PreparedStatement ps1 = conn.prepareStatement(sqlGetId)) {
                ps1.setString(1, itemName);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) {
                        itemId = rs1.getInt("item_id");
                    }
                }
            }

            if (itemId != -1) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)) {
                    ps2.setInt(1, currentSaveSlot);
                    ps2.setInt(2, itemId);
                    ps2.executeUpdate();
                }

                try (PreparedStatement ps3 = conn.prepareStatement(sqlDelete)) {
                    ps3.setInt(1, currentSaveSlot);
                    ps3.setInt(2, itemId);
                    ps3.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
