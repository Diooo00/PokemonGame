/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.world;

import com.pokemongame.main.GamePanel;
import com.pokemongame.entity.Entity;
/**
 *
 * @author thety
 */
public class Camera {
    private GamePanel gp;

    public int x; // offset render horizontal
    public int y; // offset render vertikal

    public Camera(GamePanel gp) {
        this.gp = gp;
        this.x = 0;
        this.y = 0;
    }

    public void update(Entity target) {
        // Menghitung offset agar target berada di tengah frame 1080p
        x = target.worldX - (GamePanel.SCREEN_WIDTH / 2) + (GamePanel.TILE_SIZE / 2);
        y = target.worldY - (GamePanel.SCREEN_HEIGHT / 2) + (GamePanel.TILE_SIZE / 2);

        // Batas agar kamera tidak memperlihatkan area kosong di luar map 100x100
        int worldWidth  = TileMap.MAX_WORLD_COLS * GamePanel.TILE_SIZE;
        int worldHeight = TileMap.MAX_WORLD_ROWS * GamePanel.TILE_SIZE;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + GamePanel.SCREEN_WIDTH > worldWidth) x = worldWidth - GamePanel.SCREEN_WIDTH;
        if (y + GamePanel.SCREEN_HEIGHT > worldHeight) y = worldHeight - GamePanel.SCREEN_HEIGHT;
    }
}
