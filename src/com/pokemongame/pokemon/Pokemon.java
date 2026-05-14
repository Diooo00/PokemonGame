/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author thety
 */
public abstract class Pokemon {

    // Data dasar
    protected String name;
    protected int level;

    // Stats
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int speed;

    // Tipe
    public enum Type { FIRE, WATER, GRASS, NORMAL }
    protected Type type;

    // Daftar serangan yang dimiliki
    protected List<Move> moves;

    public Pokemon(String name, int level, int maxHp,
                   int attack, int defense, int speed, Type type) {
        this.name     = name;
        this.level    = level;
        this.maxHp    = maxHp;
        this.currentHp = maxHp;
        this.attack   = attack;
        this.defense  = defense;
        this.speed    = speed;
        this.type     = type;
        this.moves    = new ArrayList<>();
    }

    // Wajib diimplementasi — efek khusus tiap tipe
    public abstract void useSpecialAbility();

    // Hitung damage yang diterima dengan type advantage
    public void takeDamage(int rawDamage, Type attackerType) {
        double multiplier = getTypeMultiplier(attackerType, this.type);
        int finalDamage = (int)(rawDamage * multiplier);
        currentHp = Math.max(0, currentHp - finalDamage);
        System.out.println(name + " took " + finalDamage +
                           " damage! (x" + multiplier + ")");
    }

    // Table type advantage sederhana
    private double getTypeMultiplier(Type attacker, Type defender) {
        if (attacker == Type.FIRE  && defender == Type.GRASS)  return 2.0;
        if (attacker == Type.WATER && defender == Type.FIRE)   return 2.0;
        if (attacker == Type.GRASS && defender == Type.WATER)  return 2.0;
        if (attacker == Type.FIRE  && defender == Type.WATER)  return 0.5;
        if (attacker == Type.WATER && defender == Type.GRASS)  return 0.5;
        if (attacker == Type.GRASS && defender == Type.FIRE)   return 0.5;
        return 1.0; // neutral
    }

    public void addMove(Move move) {
        if (moves.size() < 4) moves.add(move); // max 4 serangan
    }

    public Move getMove(int index) {
        if (index >= 0 && index < moves.size()) return moves.get(index);
        return null;
    }

    // Hitung damage serangan berdasarkan stat attack
    public int calculateDamage(Move move) {
        return (int)((attack * move.getPower()) / 10.0);
    }

    public boolean isFainted() { return currentHp <= 0; }

    // Getters
    public String getName()     { return name; }
    public int getLevel()       { return level; }
    public int getCurrentHp()   { return currentHp; }
    public int getMaxHp()       { return maxHp; }
    public int getAttack()      { return attack; }
    public int getDefense()     { return defense; }
    public int getSpeed()       { return speed; }
    public Type getType()       { return type; }
    public List<Move> getMoves(){ return moves; }

    public void healFull() { currentHp = maxHp; }
}
