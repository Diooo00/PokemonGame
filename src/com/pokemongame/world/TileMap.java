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

    public static final int MAX_WORLD_COLS = 100;
    public static final int MAX_WORLD_ROWS = 100;
    private int[][] mapTileNum;

    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[15];
        mapData = new int[MAX_WORLD_ROWS][MAX_WORLD_COLS];

        loadTileImages();
        loadMapData();
    }

    private void loadTileImages() {
        // Karena folder 'res' sudah didaftarkan, panggil langsung sub-foldernya
        tiles[0] = new Tile(SpriteLoader.loadSprite("/tiles/grass.png"), false);
        tiles[1] = new Tile(SpriteLoader.loadSprite("/tiles/wall.png"), true);
        tiles[2] = new Tile(SpriteLoader.loadSprite("/tiles/tallgrass.png"), false);
        tiles[3] = new Tile(SpriteLoader.loadSprite("/tiles/water.png"), true);
        tiles[4] = new Tile(SpriteLoader.loadSprite("/tiles/dirt.png"), false);
        tiles[5] = new Tile(SpriteLoader.loadSprite("/tiles/sand.png"), false);
        tiles[6] = new Tile(SpriteLoader.loadSprite("/tiles/flowers.png"), false);
        tiles[7] = new Tile(SpriteLoader.loadSprite("/tiles/signpost.png"), true);
        tiles[8] = new Tile(SpriteLoader.loadSprite("/tiles/tree.png"), true);
        tiles[9] = new Tile(SpriteLoader.loadSprite("/tiles/fence.png"), true);
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

                // HANYA GAMBAR JIKA TILE ADA DI DALAM LAYAR (Culling)
                if (worldX + GamePanel.TILE_SIZE > cameraX &&
                    worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                    worldY + GamePanel.TILE_SIZE > cameraY &&
                    worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {

                    if (tiles[tileIndex] != null) {
                        // TAMBAHKAN TILE_SIZE di akhir drawImage agar gambar ditarik (stretch)
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
    
    // Method untuk mengambil ID Tile pada koordinat tertentu
    public int getTileNum(int col, int row) {
        if (col >= 0 && col < MAX_WORLD_COLS && row >= 0 && row < MAX_WORLD_ROWS) {
            return mapData[row][col]; 
        }
        return 0;
    }

    // Method untuk mengecek apakah ID Tile tersebut punya sifat "solid/tabrakan"
    public boolean isCollision(int tileNum) {
    if (tiles[tileNum] != null) {
        return tiles[tileNum].solid; 
    }
    return false;
}

    public int getTileID(int col, int row) {
        if (col >= 0 && col < MAX_WORLD_COLS && row >= 0 && row < MAX_WORLD_ROWS) {
            return mapData[row][col]; // Di file kamu pakainya mapData[row][col]
        }
        return 1; // Jika di luar batas, anggap solid (tembok)
    }

    public boolean isTileSolid(int tileIndex) {
        // Pastikan index valid dan gunakan .solid (sesuai file Tile.java kamu)
        if (tileIndex >= 0 && tileIndex < tiles.length && tiles[tileIndex] != null) {
            return tiles[tileIndex].solid; 
        }
        return false; // Default: bisa dilewati jika tile null
    }
}
