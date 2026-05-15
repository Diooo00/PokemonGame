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
                    int bSpd = rs.getInt("base_spd");

                    int finalHp = bHp + (level * 2) + 10;
                    int finalAtk = bAtk + (int)(level * 1.2);
                    int finalDef = bDef + (int)(level * 1.2);
                    int finalSpd = bSpd + (int)(level * 1.2);

                    p = new Pokemon(id, name, t1, level, finalHp, finalAtk, finalDef, finalSpd);
                    if (t2 != null) p.setType2(t2);

                    // Muat jurus (Moves)
                    loadMovesForPokemon(p, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    private static void loadMovesForPokemon(Pokemon p, Connection conn) throws SQLException {
        // MENGGUNAKAN JOIN: Gabungkan tabel pokemon_moves dan moves_base
        String sql = "SELECT mb.name, mb.type, mb.power " +
                     "FROM pokemon_moves pm " +
                     "JOIN moves_base mb ON pm.move_id = mb.move_id " +
                     "WHERE pm.poke_id = ?";
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPokeId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    p.addMove(new Move(
                        rs.getString("name"),   // Di database namanya 'name', bukan 'move_name'
                        rs.getString("type"),
                        rs.getInt("power"),     // Di database namanya 'power', bukan 'damage'
                        100 
                    ));
                }
            }
        }
        
        if (p.getMoves().isEmpty()) {
            p.addMove(new Move("Tackle", "Normal", 40, 100));
        }
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
        String sql = "SELECT poke_id, level FROM player_pokemon WHERE player_id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (conn == null) return party;
            while (rs.next()) {
                Pokemon p = loadPokemonById(rs.getInt("poke_id"), rs.getInt("level"));
                if (p != null) party.add(p);
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
}