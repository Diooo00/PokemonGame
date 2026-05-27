/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import com.pokemongame.entity.Player;
import com.pokemongame.item.Item;
import com.pokemongame.pokemon.Move;
import com.pokemongame.pokemon.Pokemon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thety
 */
public class SaveManager {
    
    public static int playerMoney = 0;
    public static int lastWildPokeId = -1;

    // --- FUNGSI LOAD KESELURUHAN (POSISI & DUIT) ---
    public static void loadGame(Player player) {
        String sql = "SELECT world_x, world_y, money FROM player_save WHERE id = 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                player.worldX = rs.getInt("world_x");
                player.worldY = rs.getInt("world_y");
                playerMoney = rs.getInt("money"); // Duit ditarik dari database!
                System.out.println("SUKSES LOAD: Posisi & Duit ($" + playerMoney + ") berhasil dimuat!");
            }
            
        } catch (SQLException e) {
            System.err.println("Gagal memuat save game dari database!");
            e.printStackTrace();
        }
    }

    // --- FUNGSI SAVE KESELURUHAN (POSISI & DUIT) ---
    public static void saveGame(Player player) {
        if (player == null) return;
        
        // Update tabel player_save (masukkan money juga)
        String sql = "UPDATE player_save SET world_x = ?, world_y = ?, money = ? WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (conn == null) return;
            ps.setInt(1, player.worldX);
            ps.setInt(2, player.worldY);
            ps.setInt(3, playerMoney); // Simpan duit terbaru
            ps.executeUpdate();
            
            System.out.println("SUKSES AUTO-SAVE: Posisi & Duit ($" + playerMoney + ") tersimpan!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Pokemon loadPokemonById(int id, int level) {
        Pokemon p = null;
        String sql = "SELECT * FROM pokemon_base WHERE poke_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return null;
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
                    int finalAtk = bAtk + (int)(level * 1.2);
                    int finalDef = bDef + (int)(level * 1.2);
                    int finalSpAtk = bSpAtk + (int)(level * 1.2); 
                    int finalSpDef = bSpDef + (int)(level * 1.2); 
                    int finalSpd = bSpd + (int)(level * 1.2);

                    p = new Pokemon(id, name, t1, level, finalHp, finalAtk, finalDef, finalSpAtk, finalSpDef, finalSpd);
                    if (t2 != null) p.setType2(t2);

                    loadMovesForPokemon(p, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static void loadMovesForPokemon(Pokemon p, Connection conn) throws SQLException {
        String sql = "SELECT mb.move_id FROM pokemon_moves pm " +
                     "JOIN moves_base mb ON pm.move_id = mb.move_id " +
                     "WHERE pm.poke_id = ? ORDER BY mb.power DESC"; 
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPokeId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Move m = getMoveById(rs.getInt("move_id"), conn);
                    if (m != null) p.addMove(m); 
                }
            }
        }
        
        if (p.getMoves().isEmpty()) {
            Move tackle = getMoveByName("Tackle", conn);
            if (tackle != null) p.addMove(tackle);
            
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
                            if (elementalMove2 != null) p.addMove(elementalMove2);
                        }
                    }
                }
            }
        }
    }

    public static int getRandomPokemonId() {
        int randomId = 495; // Default Snivy (buat jaga-jaga kalau koneksi terputus)
        
        String sql = "SELECT poke_id FROM pokemon_base WHERE poke_id != ? ORDER BY RAND() LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Masukin ID Pokemon terakhir biar di-blacklist sementara dari kocokan
            ps.setInt(1, lastWildPokeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    randomId = rs.getInt("poke_id"); 
                    
                    // Ingat ID pemenangnya buat di-blacklist di pertarungan selanjutnya!
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
        String sql = "UPDATE player_pokemon SET level = ?, current_hp = ?, exp = ? WHERE player_id = 1 AND poke_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) return;
            ps.setInt(1, p.getLevel());
            ps.setInt(2, p.getCurrentHp());
            ps.setInt(3, p.getExp());
            ps.setInt(4, p.getPokeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Pokemon> loadPlayerParty() {
        List<Pokemon> party = new ArrayList<>();
        String sql = "SELECT poke_id, level, current_hp, exp, move1_id, move2_id, move3_id, move4_id FROM player_pokemon WHERE player_id = 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (conn == null) return party;
            
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
                            if (m != null) p.getMoves().add(m);
                        }
                    }
                    
                    if (p.getMoves().isEmpty()) {
                        loadMovesForPokemon(p, conn); 
                    }
                    
                    party.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return party;
    }

    public static List<Item> loadPlayerItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.name, i.effect_value, pi.quantity " +
                     "FROM player_inventory pi " +
                     "JOIN items i ON pi.item_id = i.item_id " +
                     "WHERE pi.player_id = 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (conn == null) return items;

            while (rs.next()) {
                items.add(new Item(
                    rs.getString("name"),         
                    rs.getInt("effect_value"),
                    rs.getInt("quantity")
                ));
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
        String sql = "INSERT INTO player_pokemon (player_id, poke_id, level, current_hp, exp) VALUES (1, ?, ?, ?, 0)";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pokeId);
                ps.setInt(2, level);
                ps.setInt(3, currentHp);
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
        if (party == null || party.isEmpty()) return;

        for (Pokemon p : party) {
            p.setCurrentHp(p.getMaxHp());
            savePokemonStatus(p);
        }
        System.out.println("Semua Pokémon di Party berhasil di-Heal & Revive!");
    }
    
    public static void addItem(String itemName, int quantity) {
        String sqlGetId = "SELECT item_id FROM items WHERE name = ?";
        String sqlCheck = "SELECT quantity FROM player_inventory WHERE player_id = 1 AND item_id = ?";
        String sqlUpdate = "UPDATE player_inventory SET quantity = quantity + ? WHERE player_id = 1 AND item_id = ?";
        String sqlInsert = "INSERT INTO player_inventory (player_id, item_id, quantity) VALUES (1, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;
            
            int itemId = -1;
            try (PreparedStatement ps1 = conn.prepareStatement(sqlGetId)) {
                ps1.setString(1, itemName);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) itemId = rs1.getInt("item_id");
                }
            }
            
            if (itemId == -1) {
                System.out.println("EROR: Item '" + itemName + "' tidak ditemukan di database!");
                return; 
            }
            
            boolean hasItem = false;
            try (PreparedStatement ps2 = conn.prepareStatement(sqlCheck)) {
                ps2.setInt(1, itemId);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) hasItem = true;
                }
            }
            
            if (hasItem) {
                try (PreparedStatement ps3 = conn.prepareStatement(sqlUpdate)) {
                    ps3.setInt(1, quantity);
                    ps3.setInt(2, itemId);
                    ps3.executeUpdate();
                }
            } else {
                try (PreparedStatement ps4 = conn.prepareStatement(sqlInsert)) {
                    ps4.setInt(1, itemId);
                    ps4.setInt(2, quantity);
                    ps4.executeUpdate();
                }
            }
            System.out.println("SUKSES: " + quantity + " " + itemName + " masuk ke tas database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void consumeItem(String itemName) {
        String sqlGetId = "SELECT item_id FROM items WHERE name = ?";
        String sqlUpdate = "UPDATE player_inventory SET quantity = quantity - 1 WHERE player_id = 1 AND item_id = ? AND quantity > 0";
        // Bersihkan data dari tabel kalau jumlahnya udah 0 biar rapi
        String sqlDelete = "DELETE FROM player_inventory WHERE player_id = 1 AND item_id = ? AND quantity <= 0";

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn == null) return;

            // 1. Cari item_id dari namanya
            int itemId = -1;
            try (PreparedStatement ps1 = conn.prepareStatement(sqlGetId)) {
                ps1.setString(1, itemName);
                try (ResultSet rs1 = ps1.executeQuery()) {
                    if (rs1.next()) itemId = rs1.getInt("item_id");
                }
            }

            // 2. Kalau ketemu, kurangi 1 di database
            if (itemId != -1) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)) {
                    ps2.setInt(1, itemId);
                    ps2.executeUpdate();
                }
                
                // 3. Sapu bersih kalau itemnya udah abis (0)
                try (PreparedStatement ps3 = conn.prepareStatement(sqlDelete)) {
                    ps3.setInt(1, itemId);
                    ps3.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}