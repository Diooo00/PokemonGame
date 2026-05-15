/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

/**
 *
 * @author thety
 */
public class PhysicalMove extends Move {

    public PhysicalMove(String name, int power, int accuracy, String type) {
        super(name, power, accuracy, type, Category.PHYSICAL);
    }

    @Override
    public void applyEffect(Pokemon user, Pokemon target) {
        // Physical move — tidak ada efek status tambahan
        // Bisa di-extend nanti untuk moves seperti Tackle, Scratch, dll
    }
}
