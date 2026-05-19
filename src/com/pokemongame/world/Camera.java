/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.world;

import com.pokemongame.main.GamePanel;
import com.pokemongame.entity.Entity;
import com.pokemongame.entity.Player;
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

    public void update(Player player) {
        // Kunci posisi kamera tepat di tengah karakter (Tanpa delay/speed)
        this.x = player.worldX - (GamePanel.SCREEN_WIDTH / 2) + (GamePanel.TILE_SIZE / 2);
        this.y = player.worldY - (GamePanel.SCREEN_HEIGHT / 2) + (GamePanel.TILE_SIZE / 2);

        // (Opsional) Kodingan di bawah ini biar kamera nggak ngelewatin batas ujung map/jurang. 
        // Kalau map kamu bebas, ini bisa dihapus, tapi disarankan tetap dipakai:
        
        if (this.x < 0) {
            this.x = 0;
        }
        if (this.y < 0) {
            this.y = 0;
        }
        
        int rightEdge = (TileMap.MAX_WORLD_COLS * GamePanel.TILE_SIZE) - GamePanel.SCREEN_WIDTH;
        if (this.x > rightEdge) {
            this.x = rightEdge;
        }
        
        int bottomEdge = (TileMap.MAX_WORLD_ROWS * GamePanel.TILE_SIZE) - GamePanel.SCREEN_HEIGHT;
        if (this.y > bottomEdge) {
            this.y = bottomEdge;
        }
    }
}
