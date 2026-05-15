/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.pokemon;

/**
 *
 * @author thety
 */
public class SpecialMove extends Move {

    // Efek status yang dibawa serangan ini
    public enum StatusEffect { NONE, BURN, FREEZE, PARALYZE }
    private StatusEffect statusEffect;
    private int effectChance; // persen chance efek terjadi

    public SpecialMove(String name, int power, int accuracy,
                       String type, StatusEffect effect, int effectChance) {
        super(name, power, accuracy, type, Category.SPECIAL);
        this.statusEffect = effect;
        this.effectChance = effectChance;
    }

    @Override
    public void applyEffect(Pokemon user, Pokemon target) {
        if (statusEffect != StatusEffect.NONE) {
            if (Math.random() * 100 < effectChance) {
                System.out.println(target.getName() +
                                   " is affected by " + statusEffect + "!");
                // Implementasi efek status di Chat 4 (BattleState)
            }
        }
    }
}

