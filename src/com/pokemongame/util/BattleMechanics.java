/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

/**
 *
 * @author user
 */
public class BattleMechanics {

    // 1. Rumus HP
    public static int calculateMaxHP(int baseHp, int level) {
        return ((2 * baseHp * level) / 100) + level + 10;
    }

    // 2. Rumus Stat Asli
    public static int calculateStat(int baseStat, int level) {
        return ((2 * baseStat * level) / 100) + 5;
    }

    // 3. RUMUS DAMAGE FINAL (Sekarang pakai 7 Parameter!)
    public static int calculateDamage(int attackerLevel, int attackStat, int defenseStat, int movePower, double stabMultiplier, double typeEffectiveness, double abilityMultiplier) {
        
        // Kalau jurusnya status doang (kayak Leer), damage = 0
        if (movePower == 0) return 0;

        // Rumus Inti Damage Pokemon NDS
        double step1 = (2.0 * attackerLevel / 5.0) + 2.0;
        double step2 = step1 * movePower * ((double) attackStat / defenseStat);
        double baseDamage = (step2 / 50.0) + 2.0;

        // Faktor Hoki (Random 85% - 100%)
        double randomMod = (Math.random() * (1.0 - 0.85)) + 0.85;

        // Total Damage Akhir (Semua pengali dimasukkan)
        double finalDamage = baseDamage * stabMultiplier * typeEffectiveness * randomMod * abilityMultiplier;

        return (int) Math.max(1, finalDamage); // Minimal damage selalu 1
    }
}
