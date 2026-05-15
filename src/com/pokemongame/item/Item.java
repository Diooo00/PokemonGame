/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.item;

/**
 *
 * @author user
 */
public class Item {
    private String name;
    private int effectValue; // Misal: Heal 20 HP
    private int quantity;

    public Item(String name, int effectValue, int quantity) {
        this.name = name;
        this.effectValue = effectValue;
        this.quantity = quantity;
    }

    // Getters & Setters
    public String getName() { return name; }
    public int getEffectValue() { return effectValue; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Logika pakai item
    public boolean use() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }
}
