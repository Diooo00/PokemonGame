/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;


/**
 *
 * @author thety
 */
public class Move {

    protected String name;
    protected int power;
    protected int accuracy; // 0-100
    protected String type;  // Diubah dari Enum ke String
    
    protected int pp;
    protected int maxPp;

    public enum Category { PHYSICAL, SPECIAL }
    protected Category category;

    /**
     * CONSTRUCTOR UTAMA: Digunakan oleh SaveManager untuk memuat jurus dari Database.
     */
    public Move(String name, String type, int power, int accuracy) {
        this.name = name;
        this.type = type.toUpperCase();
        this.power = power;
        this.accuracy = accuracy;
        this.category = Category.PHYSICAL; // Default category
    }

    /**
     * CONSTRUCTOR LENGKAP: Jika kamu ingin menentukan kategori secara manual.
     */
    public Move(String name, int power, int accuracy, String type, Category category) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
        this.type = type.toUpperCase();
        this.category = category;
    }

    // Cek apakah serangan kena (berdasarkan accuracy)
    public boolean doesHit() {
        return Math.random() * 100 < accuracy;
    }

    // Efek tambahan opsional
    public void applyEffect(Pokemon user, Pokemon target) {
        // Bisa di-override di subclass jika perlu
    }

    // --- GETTERS ---
    public String getName()     { return name; }
    public int getPower()       { return power; }
    public int getAccuracy()    { return accuracy; }
    public String getType()     { return type; } // Mengembalikan String (FIRE, WATER, dll)
    public Category getCategory() { return category; }
    
    public int getPp() { return pp; }
    public void setPp(int pp) { this.pp = pp; }

    public int getMaxPp() { return maxPp; }
    public void setMaxPp(int maxPp) { this.maxPp = maxPp; }
    
    @Override
    public String toString() { return name; }
}
