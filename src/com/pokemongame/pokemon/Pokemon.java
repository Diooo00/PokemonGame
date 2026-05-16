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

    // Tipe (Gunakan String agar fleksibel dengan 18 tipe Gen 5 di DB)
    protected String type1;
    protected String type2;

    protected List<Move> moves;

    /**
     * CONSTRUCTOR UTAMA: 8 Parameter agar sinkron dengan SaveManager.
     */
    public Pokemon(int pokeId, String name, String type1, int level, int hp, int atk, int def, int spd) {
        this.pokeId = pokeId;
        this.name = name;
        this.type1 = type1.toUpperCase();
        this.level = level;
        this.maxHp = hp;
        this.currentHp = hp;
        this.attack = atk;
        this.defense = def;
        this.speed = spd;
        this.moves = new ArrayList<>();
    }

    // --- LOGIKA BATTLE ---

    public void useSpecialAbility() {
        if (currentHp < maxHp / 3) {
            // Logika Ability berdasarkan Type1
            if (type1.equals("FIRE")) {
                attack = (int)(attack * 1.5);
                System.out.println(name + "'s Blaze activated!");
            } else if (type1.equals("WATER")) {
                defense = (int)(defense * 1.5);
                System.out.println(name + "'s Torrent activated!");
            } else if (type1.equals("GRASS")) {
                int heal = maxHp / 10;
                currentHp = Math.min(maxHp, currentHp + heal);
                System.out.println(name + "'s Overgrow activated!");
            }
        }
    }

    public double getEffectiveness(String moveType, String defenderType) {
        if (moveType.equals("FIRE")  && defenderType.equals("GRASS")) return 2.0;
        if (moveType.equals("WATER") && defenderType.equals("FIRE"))  return 2.0;
        if (moveType.equals("GRASS") && defenderType.equals("WATER")) return 2.0;
        if (moveType.equals("FIRE")  && defenderType.equals("WATER")) return 0.5;
        if (moveType.equals("WATER") && defenderType.equals("GRASS")) return 0.5;
        if (moveType.equals("GRASS") && defenderType.equals("FIRE"))  return 0.5;
        return 1.0;
    }

    public void takeDamage(int damage, String attackerType) {
        double multiplier = getEffectiveness(attackerType, this.type1);
        
        // Cek juga tipe kedua kalau ada
        if (type2 != null) {
            multiplier *= getEffectiveness(attackerType, this.type2);
        }

        int finalDamage = (int)(damage * multiplier);
        this.currentHp = Math.max(0, this.currentHp - finalDamage);

        if (multiplier > 1.0) System.out.println("It's super effective!");
        else if (multiplier < 1.0 && multiplier > 0) System.out.println("It's not very effective...");
    }

    public int calculateDamage(Move move) {
        // Rumus damage: (Attack * Power) / Defense_Lawan (Defense nanti diurus di BattleState)
        // Untuk sekarang pakai rumus dasarmu tapi dibagi 10
        return (int) ((this.attack * move.getPower()) / 10.0);
    }

    // --- SISTEM LEVEL & EXP ---

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
        this.defense += 1;
        this.exp = 0;
        this.expToNextLevel += 50;
        System.out.println(name + " leveled up to " + level + "!");
    }

    // --- GETTERS & SETTERS ---

    public void addMove(Move move) {
        if (moves.size() < 4) moves.add(move);
    }

    public Move getMove(int index) {
        return (index >= 0 && index < moves.size()) ? moves.get(index) : null;
    }
    
    public String getType1() {
    return type1; 
    }

    public String getType2() {
        return type2; // Ini yang tadi kurang, makanya harus ditambahin
    }

    // --- Bagian Setter (Buat ngisi data) ---

    public void setType1(String t1) {
        this.type1 = t1.toUpperCase();
    }

    public void setType2(String t2) {
        // Pakai proteksi: kalau t2 null (ga punya tipe kedua), jangan di-toUpperCase
        if (t2 != null) {
            this.type2 = t2.toUpperCase();
        } else {
            this.type2 = null;
        }
    }
    
    public int getPokeId() {
        return pokeId; 
    }
    public String getName()  {
        return name; 
    }
    public int getLevel() {
        return level; 
    }
    public int getCurrentHp() {
        return currentHp; 
    }
    public void setCurrentHp(int hp) {
        this.currentHp = hp; 
    }
    public int getMaxHp() {
        return maxHp; 
    }
    public List<Move> getMoves() {
        return moves; 
    }
    public boolean isFainted() {
        return currentHp <= 0; 
    }
    public int getExp() {
        return exp; 
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    // Catatan: Sesuaikan nama variabel internal kamu (apakah 'attack' atau 'atk')
    public int getAttack() {
        return this.attack; // Ganti ke 'this.atk' jika nama variabelmu 'atk'
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return this.defense; // Ganti ke 'this.def' jika nama variabelmu 'def'
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpeed() {
        return this.speed; // Ganti ke 'this.spd' jika nama variabelmu 'spd'
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
