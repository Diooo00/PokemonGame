/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import com.pokemongame.pokemon.Move;
import com.pokemongame.pokemon.Pokemon;

/**
 *
 * @author user
 */
public class TypeChart {
    
    // Fungsi ini mengembalikan multiplier (0.5, 1.0, 2.0)
    public static double getEffectiveness(String attackType, String defendType) {
        if (attackType == null || defendType == null) return 1.0;
        
        attackType = attackType.toUpperCase();
        defendType = defendType.toUpperCase();
        
        // --- SERANGAN FIRE ---
        if (attackType.equals("FIRE")) {
            if (defendType.equals("GRASS") || defendType.equals("BUG") || defendType.equals("ICE") || defendType.equals("STEEL")) return 2.0;
            if (defendType.equals("FIRE") || defendType.equals("WATER") || defendType.equals("ROCK") || defendType.equals("DRAGON")) return 0.5;
        }
        // --- SERANGAN WATER ---
        else if (attackType.equals("WATER")) {
            if (defendType.equals("FIRE") || defendType.equals("GROUND") || defendType.equals("ROCK")) return 2.0;
            if (defendType.equals("WATER") || defendType.equals("GRASS") || defendType.equals("DRAGON")) return 0.5;
        }
        // --- SERANGAN GRASS ---
        else if (attackType.equals("GRASS")) {
            if (defendType.equals("WATER") || defendType.equals("GROUND") || defendType.equals("ROCK")) return 2.0;
            if (defendType.equals("FIRE") || defendType.equals("GRASS") || defendType.equals("POISON") || defendType.equals("FLYING") || defendType.equals("BUG") || defendType.equals("DRAGON") || defendType.equals("STEEL")) return 0.5;
        }
        // --- SERANGAN ELECTRIC ---
        else if (attackType.equals("ELECTRIC")) {
            if (defendType.equals("WATER") || defendType.equals("FLYING")) return 2.0;
            if (defendType.equals("ELECTRIC") || defendType.equals("GRASS") || defendType.equals("DRAGON")) return 0.5;
            if (defendType.equals("GROUND")) return 0.0; // Immune!
        }
        // --- NORMAL ---
        else if (attackType.equals("NORMAL")) {
            if (defendType.equals("ROCK") || defendType.equals("STEEL")) return 0.5;
            if (defendType.equals("GHOST")) return 0.0; // Immune!
        }
        // Nanti kamu bisa tambahin tipe lain (Fighting, Ground, dll) di sini!
        
        return 1.0; // Default kalau netral
    }

    // Fungsi buat ngecek STAB (Same Type Attack Bonus)
    // Kalau tipe Pokemon = tipe Jurus, damage dikali 1.5
    public static double getStabMultiplier(Pokemon p, Move m) {
        if (p.getType1().equalsIgnoreCase(m.getType())) return 1.5;
        if (p.getType2() != null && p.getType2().equalsIgnoreCase(m.getType())) return 1.5;
        return 1.0;
    }
}
