/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

/**
 *
 * @author thety
 */
public class WaterPokemon extends Pokemon {

    public WaterPokemon(String name, int level) {
        super(name, level,
              35 + level * 3,   // maxHp — sedikit lebih tanky
              12 + level * 2,   // attack
              12 + level,       // defense — lebih tinggi
              10 + level,       // speed
              Type.WATER);

        addMove(new PhysicalMove("Tackle", 40, 100, Type.NORMAL));
        addMove(new SpecialMove("Water Gun", 40, 100, Type.WATER,
                SpecialMove.StatusEffect.NONE, 0));
    }

    @Override
    public void useSpecialAbility() {
        // Torrent — boost attack saat HP rendah
        if (currentHp < maxHp / 3) {
            attack = (int)(attack * 1.5);
            System.out.println(name + "'s Torrent activated! Attack boosted!");
        }
    }
}
