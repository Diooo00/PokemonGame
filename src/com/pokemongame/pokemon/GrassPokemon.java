/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

/**
 *
 * @author thety
 */
public class GrassPokemon extends Pokemon {

    private int healCooldown = 0;

    public GrassPokemon(String name, int level) {
        super(name, level,
              32 + level * 3,   // maxHp
              13 + level * 2,   // attack
              10 + level,       // defense
              11 + level,       // speed
              Type.GRASS);

        addMove(new PhysicalMove("Tackle", 40, 100, Type.NORMAL));
        addMove(new SpecialMove("Vine Whip", 45, 100, Type.GRASS,
                SpecialMove.StatusEffect.NONE, 0));
    }

    @Override
    public void useSpecialAbility() {
        // Overgrow — heal sedikit HP tiap giliran saat HP rendah
        if (currentHp < maxHp / 3 && healCooldown <= 0) {
            int healAmount = maxHp / 10;
            currentHp = Math.min(maxHp, currentHp + healAmount);
            healCooldown = 3; // cooldown 3 giliran
            System.out.println(name + "'s Overgrow healed " + healAmount + " HP!");
        }
        if (healCooldown > 0) healCooldown--;
    }
}

