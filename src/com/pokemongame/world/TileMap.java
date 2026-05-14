    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.world;

import com.pokemongame.main.GamePanel;
import com.pokemongame.util.SpriteLoader;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 *
 * @author thety
 */
public class TileMap {
    private GamePanel gamePanel;
    private Tile[] tiles;
    public int[][] mapData;

    public static final int MAX_WORLD_COLS = 30;
    public static final int MAX_WORLD_ROWS = 30;

    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[10];
        mapData = new int[MAX_WORLD_ROWS][MAX_WORLD_COLS];

        loadTileImages();
        loadMapData();
    }

    private void loadTileImages() {
        // Karena folder 'res' sudah didaftarkan, panggil langsung sub-foldernya
        tiles[0] = new Tile(SpriteLoader.loadSprite("/tiles/grass.png"), false);
        tiles[1] = new Tile(SpriteLoader.loadSprite("/tiles/wall.png"), true);
        tiles[2] = new Tile(SpriteLoader.loadSprite("/tiles/tallgrass.png"), false);
    }

    private void loadMapData() {
        try {
            // Membaca file dari folder res/maps/map01.txt
            InputStream is = getClass().getResourceAsStream("/map/map01.txt");
            
            if (is == null) {
                System.err.println("EROR: map01.txt tidak ditemukan di folder maps!");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int row = 0; row < MAX_WORLD_ROWS; row++) {
                String line = br.readLine();
                if (line == null) break;
                
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < MAX_WORLD_COLS; col++) {
                    if (col < tokens.length) {
                        mapData[row][col] = Integer.parseInt(tokens[col]);
                    }
                }
            }
            br.close();
            System.out.println("Peta berhasil dimuat!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics2D g2d, int cameraX, int cameraY) {
        for (int row = 0; row < MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < MAX_WORLD_COLS; col++) {
                int tileIndex = mapData[row][col];
                
                int worldX = col * GamePanel.TILE_SIZE;
                int worldY = row * GamePanel.TILE_SIZE;
                int screenX = worldX - cameraX;
                int screenY = worldY - cameraY;

                // Render hanya yang masuk layar
                if (screenX + GamePanel.TILE_SIZE > 0 && screenX < GamePanel.SCREEN_WIDTH &&
                    screenY + GamePanel.TILE_SIZE > 0 && screenY < GamePanel.SCREEN_HEIGHT) {
                    
                    if (tiles[tileIndex] != null && tiles[tileIndex].image != null) {
                        g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                    }
                }
            }
        }
    }

    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return true;
        return tiles[mapData[row][col]] != null && tiles[mapData[row][col]].solid;
    }

    public boolean isTallGrass(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return false;
        return mapData[row][col] == 2;
    }
}
