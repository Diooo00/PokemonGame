/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import com.pokemongame.entity.Player;
import com.pokemongame.pokemon.Pokemon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author thety
 */
public class SaveManager {

    /**
     * Menyimpan progres Player (Posisi & Uang)
     */
    public static void saveGame(Player player) {
        String sql = "UPDATE player_save SET world_x = ?, world_y = ? WHERE id = 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (conn == null) return;

            ps.setInt(1, player.worldX);
            ps.setInt(2, player.worldY);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Progres posisi berhasil disimpan!");
            }
            
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan game!");
            e.printStackTrace();
        }
    }

    public static void savePokemonStatus(Pokemon p) {
        // Kita gunakan player_id = 1 untuk slot save pertama
        String sql = "UPDATE player_pokemon SET level = ?, current_hp = ?, exp = ? " +
                     "WHERE player_id = 1 AND poke_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) return;

            ps.setInt(1, p.getLevel());
            ps.setInt(2, p.getCurrentHp());
            ps.setInt(3, p.getExp());    // Pastikan ada getter getExp() di Pokemon.java
            ps.setInt(4, p.getPokeId()); // Pastikan ada getter getPokeId() di Pokemon.java

            ps.executeUpdate();
            System.out.println("Status " + p.getName() + " berhasil di-update di database!");

        } catch (SQLException e) {
            System.err.println("Gagal update status Pokemon!");
            e.printStackTrace();
        }
    }

    /**
     * Memuat data Pokemon tim Player dari database
     * Digunakan saat game pertama kali dijalankan
     */
    public static List<Pokemon> loadPlayerParty() {
        List<Pokemon> party = new ArrayList<>();
        String sql = "SELECT poke_id, level FROM player_pokemon WHERE player_id = 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (conn == null) return party;

            while (rs.next()) {
                int id = rs.getInt("poke_id");
                int level = rs.getInt("level");
                
                // Menggunakan method loadFromDB yang kita buat di Pokemon.java tadi
                Pokemon p = Pokemon.loadFromDB(id, level);
                if (p != null) party.add(p);
            }
            
        } catch (SQLException e) {
            System.err.println("Gagal memuat tim Pokemon!");
            e.printStackTrace();
        }
        return party;
    }
}
