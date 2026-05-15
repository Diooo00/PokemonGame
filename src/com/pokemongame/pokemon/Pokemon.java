/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

import com.pokemongame.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author thety
 */
public class Pokemon {

    // Data Dasar
    protected String name;
    protected int level;
    protected int pokeId;
    protected int exp = 0;
    protected int expToNextLevel = 100;

    // Stats
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int speed;

    // Tipe (Gunakan Enum yang sudah kamu buat)
    public enum Type { FIRE, WATER, GRASS, NORMAL }
    protected Type type;

    // Daftar serangan (Moves)
    protected List<Move> moves;

    // CONSTRUCTOR: Untuk membuat objek Pokemon secara manual (misal untuk testing)
    public Pokemon(String name, int level, int maxHp, int attack, int defense, int speed, Type type) {
        this.name = name;
        this.level = level;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.type = type;
        this.moves = new ArrayList<>();
    }

//    /**
//     * METHOD UTAMA: Mengambil data Pokemon dari Database berdasarkan ID.
//     * @param id ID Pokemon di tabel pokemon_base (misal 1 untuk Bulbasaur)
//     * @param level Level yang diinginkan
//     * @return Objek Pokemon yang sudah terisi datanya
//     */
    public static Pokemon loadFromDB(int id, int level) {
        String sql = "SELECT * FROM pokemon_base WHERE poke_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                // Konversi string dari DB ke Enum Type
                Type type = Type.valueOf(rs.getString("type1").toUpperCase());
                
                // RUMUS STATS: Base Stat + (Level * Bonus)
                // Kamu bisa modifikasi rumus ini agar lebih seimbang (balancing)
                int hp  = rs.getInt("base_hp") + (level * 3);
                int atk = rs.getInt("base_atk") + (level * 2);
                int def = rs.getInt("base_def") + level;
                int spd = rs.getInt("base_spd") + level;

                Pokemon p = new Pokemon(name, level, hp, atk, def, spd, type);
                p.pokeId = id;
                
                // Ambil serangan bawaan (Default Move)
                p.addDefaultMoves();
                
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat Pokemon ID " + id + " dari database!");
            e.printStackTrace();
        }
        return null;
    }

    // Method untuk memberikan serangan awal
    private void addDefaultMoves() {
        // Untuk sekarang kita hardcode, nanti bisa kamu buatkan tabel pokemon_moves di DB
        this.addMove(new PhysicalMove("Tackle", 40, 100, Type.NORMAL));
        
        if (this.type == Type.FIRE) this.addMove(new SpecialMove("Ember", 40, 100, Type.FIRE, SpecialMove.StatusEffect.BURN, 10));
        if (this.type == Type.WATER) this.addMove(new SpecialMove("Water Gun", 40, 100, Type.WATER, SpecialMove.StatusEffect.NONE, 0));
        if (this.type == Type.GRASS) this.addMove(new SpecialMove("Vine Whip", 45, 100, Type.GRASS, SpecialMove.StatusEffect.NONE, 0));
    }

    public void useSpecialAbility() {
        if (currentHp < maxHp / 3) {
            switch (this.type) {
                case FIRE -> {
                    attack = (int)(attack * 1.5);
                    System.out.println(name + "'s Blaze activated! Attack boosted!");
                }
                case WATER -> {
                    defense = (int)(defense * 1.5);
                    System.out.println(name + "'s Torrent activated! Defense boosted!");
                }
                case GRASS -> {
                    int heal = maxHp / 10;
                    currentHp = Math.min(maxHp, currentHp + heal);
                    System.out.println(name + "'s Overgrow activated! Healed " + heal + " HP!");
                }
            }
        }
    }

    // Logika perhitungan Damage & Efektivitas Tipe
    public double getEffectiveness(Type attacker, Type defender) {
        if (attacker == Type.FIRE  && defender == Type.GRASS) return 2.0;
        if (attacker == Type.WATER && defender == Type.FIRE)  return 2.0;
        if (attacker == Type.GRASS && defender == Type.WATER) return 2.0;
        if (attacker == Type.FIRE  && defender == Type.WATER) return 0.5;
        if (attacker == Type.WATER && defender == Type.GRASS) return 0.5;
        if (attacker == Type.GRASS && defender == Type.FIRE)  return 0.5;
        return 1.0;
    }

    public void loadMovesFromDB() {
        this.moves.clear();
        String sql = "SELECT m.* FROM moves_base m " +
                     "JOIN pokemon_moves pm ON m.move_id = pm.move_id " +
                     "WHERE pm.poke_id = ? AND pm.level_learned <= ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, this.pokeId);
            ps.setInt(2, this.level);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String cat = rs.getString("category");
                Move move;
                if (cat.equals("PHYSICAL")) {
                    move = new PhysicalMove(rs.getString("name"), rs.getInt("power"), rs.getInt("accuracy"), Type.valueOf(rs.getString("type")));
                } else {
                    move = new SpecialMove(rs.getString("name"), rs.getInt("power"), rs.getInt("accuracy"), Type.valueOf(rs.getString("type")), SpecialMove.StatusEffect.NONE, 0);
                }
                addMove(move);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Fungsi EXP (Opsi 3)
    public boolean gainExp(int amount) {
        this.exp += amount;
        if (this.exp >= expToNextLevel) {
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        this.level++;
        this.maxHp += 5;
        this.currentHp = maxHp;
        this.attack += 2;
        this.exp = 0;
        this.expToNextLevel += 50;
        System.out.println(name + " leveled up to " + level + "!");
    }
    
    public void addMove(Move move) {
        if (moves.size() < 4) moves.add(move);
    }

    public Move getMove(int index) {
        if (index >= 0 && index < moves.size()) {
            return moves.get(index);
        }
        return null;
    }
    
    public void takeDamage(int damage, Type attackerType) {
        // Hitung multiplier berdasarkan tabel efektifitas yang sudah ada di getEffectiveness()
        double multiplier = getEffectiveness(attackerType, this.type);
        int finalDamage = (int)(damage * multiplier);

        this.currentHp -= finalDamage;

        // Pastikan HP tidak minus
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }

        // (Opsional) Debug untuk melihat efektifitas di konsol
        if (multiplier > 1.0) System.out.println("It's super effective!");
        else if (multiplier < 1.0) System.out.println("It's not very effective...");
    }
    
    public int calculateDamage(Move move) {
        // Rumus sederhana: (Stat Attack * Power Serangan) / 10
        // Kamu bisa menyesuaikan pembaginya (10.0) untuk menyeimbangkan (balancing) game
        return (int) ((this.attack * move.getPower()) / 10.0);
    } 
    
    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }
    
    public boolean isFainted() { return currentHp <= 0; }

    // Getters
    public int getExp() {
        return exp;
    }
    
    public int getPokeId() {
        return pokeId; 
    }
    public String getName()     { return name; }
    public int getLevel()       { return level; }
    public int getCurrentHp()   { return currentHp; }
    public int getMaxHp()       { return maxHp; }
    public Type getType()       { return type; }
    public List<Move> getMoves() { return moves; }
}
