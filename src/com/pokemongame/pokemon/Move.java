/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;


/**
 *
 * @author thety
 */
public abstract class Move {

    protected String name;
    protected int power;
    protected int accuracy; // 0-100
    protected Pokemon.Type type;

    public enum Category { PHYSICAL, SPECIAL }
    protected Category category;

    public Move(String name, int power, int accuracy,
                Pokemon.Type type, Category category) {
        this.name     = name;
        this.power    = power;
        this.accuracy = accuracy;
        this.type     = type;
        this.category = category;
    }

    // Cek apakah serangan kena (berdasarkan accuracy)
    public boolean doesHit() {
        return Math.random() * 100 < accuracy;
    }

    // Efek tambahan opsional — subclass bisa override
    public void applyEffect(Pokemon user, Pokemon target) {
        // default: tidak ada efek tambahan
    }

    // Getters
    public String getName()       { return name; }
    public int getPower()         { return power; }
    public int getAccuracy()      { return accuracy; }
    public Pokemon.Type getType() { return type; }
    public Category getCategory() { return category; }

    @Override
    public String toString() { return name; }
}

