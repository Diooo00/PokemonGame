/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;
import com.pokemongame.pokemon.FirePokemon;
import com.pokemongame.pokemon.GrassPokemon;
import com.pokemongame.pokemon.Pokemon;
import com.pokemongame.pokemon.WaterPokemon;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author thety
 */
public class WildPokemon extends Entity {

    private Pokemon pokemonData;

    // Roam timer
    private int roamTimer = 0;
    private int roamInterval = 120; // ganti arah tiap ~2 detik
    private int roamDirectionX = 0;
    private int roamDirectionY = 0;

    public WildPokemon(GamePanel gamePanel, int worldX, int worldY) {
        super(gamePanel);
        this.worldX = worldX;
        this.worldY = worldY;
        this.speed  = 1;

        pokemonData = generateRandom();
    }

    // Spawn pokemon random berdasarkan probabilitas
    private Pokemon generateRandom() {
        int level = 2 + (int)(Math.random() * 5);

        // Pilih ID Pokemon secara acak (Misal ID 1, 4, atau 7 sesuai SQL yang kita buat tadi)
        int[] availableIds = {1, 4, 7}; 
        int randomId = availableIds[(int)(Math.random() * availableIds.length)];

        // Panggil method statis yang baru kita buat!
        return Pokemon.loadFromDB(randomId, level);
    }

    @Override
    public void update() {
        roamTimer++;

        if (roamTimer >= roamInterval) {
            // Pilih arah acak atau berhenti
            int rand = (int)(Math.random() * 5);
            switch (rand) {
                case 0 -> { roamDirectionX =  0; roamDirectionY = -1; } // atas
                case 1 -> { roamDirectionX =  0; roamDirectionY =  1; } // bawah
                case 2 -> { roamDirectionX = -1; roamDirectionY =  0; } // kiri
                case 3 -> { roamDirectionX =  1; roamDirectionY =  0; } // kanan
                case 4 -> { roamDirectionX =  0; roamDirectionY =  0; } // diam
            }
            roamTimer = 0;
        }

        worldX += roamDirectionX * speed;
        worldY += roamDirectionY * speed;
    }

    public Pokemon getPokemonData() {
        return pokemonData;
    }

    public void render(Graphics2D g2d) {}

    @Override
    public void render(Graphics2D g2d, int cameraX, int cameraY) {
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        // Placeholder — warna sesuai tipe
        switch (pokemonData.getType()) {
            case FIRE  -> g2d.setColor(new Color(255, 100, 50));
            case WATER -> g2d.setColor(new Color(50, 150, 255));
            case GRASS -> g2d.setColor(new Color(50, 200, 50));
            default    -> g2d.setColor(Color.GRAY);
        }

        g2d.fillOval(screenX + 4, screenY + 4,
                     GamePanel.TILE_SIZE - 8, GamePanel.TILE_SIZE - 8);
    }
}
