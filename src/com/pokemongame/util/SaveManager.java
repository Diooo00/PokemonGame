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
                    int bSpAtk = rs.getInt("base_sp_atk"); // <--- TARIK DARI DB
                    int bSpDef = rs.getInt("base_sp_def"); // <--- TARIK DARI DB
                    int bSpd = rs.getInt("base_spd");

                    // Hitung stat asli berdasarkan level
                    int finalHp = bHp + (level * 2) + 10;
                    int finalAtk = bAtk + (int)(level * 1.2);
                    int finalDef = bDef + (int)(level * 1.2);
                    int finalSpAtk = bSpAtk + (int)(level * 1.2); // <--- HITUNG
                    int finalSpDef = bSpDef + (int)(level * 1.2); // <--- HITUNG
                    int finalSpd = bSpd + (int)(level * 1.2);

                    // PANGGIL CONSTRUCTOR BARU (Pastikan Pokemon.java mu udah nerima 10 parameter ini)
                    p = new Pokemon(id, name, t1, level, finalHp, finalAtk, finalDef, finalSpAtk, finalSpDef, finalSpd);
                    if (t2 != null) p.setType2(t2);

                    // Muat jurus default bawaan Pokemon liar
                    loadMovesForPokemon(p, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static void loadMovesForPokemon(Pokemon p, Connection conn) throws SQLException {
        // 1. Ambil jurus dari database, TAPI URUTKAN DARI POWER PALING GEDE (Jalur VIP!)
        String sql = "SELECT mb.move_id FROM pokemon_moves pm " +
                     "JOIN moves_base mb ON pm.move_id = mb.move_id " +
                     "WHERE pm.poke_id = ? ORDER BY mb.power DESC"; 
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPokeId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Move m = getMoveById(rs.getInt("move_id"), conn);
                    if (m != null) p.addMove(m); 
                    // Karena di Pokemon.java udah dilimit 4, 
                    // jurus ke-5 (yang powernya paling kecil) otomatis bakal ditolak!
                }
            }
        }
        
        // 2. JIKA MASIH KOSONG, BERIKAN JURUS OTOMATIS SESUAI TIPE! (AUTO-ASSIGN)
        if (p.getMoves().isEmpty()) {
            
            // A. Berikan Basic Attack Wajib: "Tackle"
            Move tackle = getMoveByName("Tackle", conn);
            if (tackle != null) p.addMove(tackle);
            
            // B. Cari 1 jurus di database yang TIPENYA SAMA dengan tipe Pokemon ini
            String sqlTipe = "SELECT move_id FROM moves_base WHERE type = ? AND name != 'Tackle' LIMIT 1";
            try (PreparedStatement psType = conn.prepareStatement(sqlTipe)) {
                psType.setString(1, p.getType1()); // Ambil elemen Pokemon (FIRE/WATER/GRASS dll)
                try (ResultSet rsType = psType.executeQuery()) {
                    if (rsType.next()) {
                        Move elementalMove = getMoveById(rsType.getInt("move_id"), conn);
                        if (elementalMove != null) {
                            p.addMove(elementalMove); // Tambahkan jurus elemennya!
                        }
                    }
                }
            }
            
            // C. (Opsional) Cek tipe kedua kalau dia punya dua elemen
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

    // Fungsi baru untuk mengundi 1 ID Pokemon secara acak dari database
    public static int getRandomPokemonId() {
        int randomId = 495; // Default Snivy (buat jaga-jaga kalau koneksi terputus)
        
        // Perintah SQL ini mengocok seluruh isi tabel dan hanya mengambil 1 baris teratas
        String sql = "SELECT poke_id FROM pokemon_base ORDER BY RAND() LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Jika database terhubung dan ada isinya
            if (conn != null && rs.next()) {
                randomId = rs.getInt("poke_id"); // Ambil ID pemenangnya
            }
        } catch (SQLException e) {
            System.err.println("EROR: Gagal mengambil ID Pokemon acak dari database!");
            e.printStackTrace();
        }
        
        return randomId;
    }
    
    public static void saveGame(Player player) {
        String sql = "UPDATE player_save SET world_x = ?, world_y = ? WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (conn == null) return;
            ps.setInt(1, player.worldX);
            ps.setInt(2, player.worldY);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        // UPDATE QUERY: Tarik juga HP, Exp, dan 4 Slot Jurus!
        String sql = "SELECT poke_id, level, current_hp, exp, move1_id, move2_id, move3_id, move4_id FROM player_pokemon WHERE player_id = 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (conn == null) return party;
            
            while (rs.next()) {
                Pokemon p = loadPokemonById(rs.getInt("poke_id"), rs.getInt("level"));
                if (p != null) {
                    // Update darah & EXP sesuai progress save-an player
                    p.setCurrentHp(rs.getInt("current_hp"));
                    p.setExp(rs.getInt("exp"));
                    
                    // Bersihkan jurus liar bawaan pabrik, ganti sama jurus milik player
                    p.getMoves().clear();
                    
                    int[] moveIds = {
                        rs.getInt("move1_id"), rs.getInt("move2_id"), 
                        rs.getInt("move3_id"), rs.getInt("move4_id")
                    };
                    
                    for (int moveId : moveIds) {
                        if (moveId > 0) {
                            Move m = getMoveById(moveId, conn); // Panggil fungsi helper baru
                            if (m != null) p.getMoves().add(m);
                        }
                    }
                    
                    // Jaga-jaga kalau player belum punya move sama sekali
                    // Jaga-jaga kalau player belum punya move sama sekali
                    if (p.getMoves().isEmpty()) {
                        loadMovesForPokemon(p, conn); // <--- SURUH DIA MANGGIL FUNGSI AUTO-ASSIGN KITA!
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
        
        // MENGGUNAKAN JOIN: Gabungkan tabel player_inventory dan items
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
                    rs.getString("name"),         // Di tabel items namanya 'name'
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
    // Fungsi helper baru untuk menarik detail 1 jurus dari database
    private static Move getMoveById(int moveId, Connection conn) {
        String sql = "SELECT name, power, accuracy, type, category FROM moves_base WHERE move_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, moveId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String catStr = rs.getString("category");
                    Move.Category cat = Move.Category.valueOf(catStr); // Ubah string DB jadi Enum
                    
                    // Panggil constructor lengkap Move kamu (nama, power, akurasi, tipe, kategori)
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
    
    // Fungsi pembantu untuk mencari move berdasarkan Nama
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
}