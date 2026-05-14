/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

/**
 *
 * @author thety
 */
public class FirePokemon extends Pokemon {

    public FirePokemon(String name, int level) {
        super(name, level,
              30 + level * 3,   // maxHp
              15 + level * 2,   // attack
              8  + level,       // defense
              12 + level,       // speed
              Type.FIRE);

        // Serangan bawaan
        addMove(new PhysicalMove("Scratch", 40, 100, Type.NORMAL));
        addMove(new SpecialMove("Ember", 40, 100, Type.FIRE,
                SpecialMove.StatusEffect.BURN, 10));
    }

    @Override
    public void useSpecialAbility() {
        // Blaze — boost attack saat HP rendah
        if (currentHp < maxHp / 3) {
            attack = (int)(attack * 1.5);
            System.out.println(name + "'s Blaze activated! Attack boosted!");
        }
    }
}
