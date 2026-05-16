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

    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[30]; // Kapasitas diperbesar biar aman buat ubin baru
        mapData = new int[MAX_WORLD_ROWS][MAX_WORLD_COLS];

        loadTileImages();
        loadMapData();
    }

    private void loadTileImages() {
        try {
            // UBIN DASAR (Membaca file individual seperti semula)
            tiles[0] = new Tile(SpriteLoader.loadSprite("/tiles/grass.png"), false);      // ID 0: Rumput biasa
            tiles[1] = new Tile(SpriteLoader.loadSprite("/tiles/wall.png"), true);        // ID 1: Batas/Pagar (Solid)
            tiles[2] = new Tile(SpriteLoader.loadSprite("/tiles/tallgrass.png"), false); // ID 2: Semak/Bush tempat Pokemon
            tiles[3] = new Tile(SpriteLoader.loadSprite("/tiles/water.png"), true);       // ID 3: Air (Solid)
            tiles[4] = new Tile(SpriteLoader.loadSprite("/tiles/dirt.png"), false);       // ID 4: Jalan tanah
            tiles[5] = new Tile(SpriteLoader.loadSprite("/tiles/sand.png"), false);       // ID 5: Pasir
            tiles[6] = new Tile(SpriteLoader.loadSprite("/tiles/flowers.png"), false);    // ID 6: Bunga hias
            tiles[7] = new Tile(SpriteLoader.loadSprite("/tiles/signpost.png"), true);    // ID 7: Papan bicara (Solid)

            // KUNCI PENGAMAN: Biar game gak crash pas di-run sekarang, kita beri pelindung try-catch internal.
            // Nanti kalau file potongan pohon barumu sudah siap di folder, ubin ID 8-15 di bawah ini bakal otomatis aktif!
            try { tiles[8] = new Tile(SpriteLoader.loadSprite("/tiles/tree_bot_left.png"), true); } catch(Exception e){}
            try { tiles[9] = new Tile(SpriteLoader.loadSprite("/tiles/tree_bot_right.png"), true); } catch(Exception e){}
            try { tiles[12] = new Tile(SpriteLoader.loadSprite("/tiles/tree_top_left.png"), false); } catch(Exception e){}
            try { tiles[13] = new Tile(SpriteLoader.loadSprite("/tiles/tree_top_right.png"), false); } catch(Exception e){}
            try { tiles[14] = new Tile(SpriteLoader.loadSprite("/tiles/tree_mid_left.png"), false); } catch(Exception e){}
            try { tiles[15] = new Tile(SpriteLoader.loadSprite("/tiles/tree_mid_right.png"), false); } catch(Exception e){}

        } catch (Exception e) {
            System.err.println("EROR: Gagal memuat file ubin individual! Periksa folder res/tiles/");
            e.printStackTrace();
        }
    }

    private void loadMapData() {
        try {
            InputStream is = getClass().getResourceAsStream("/map/map01.txt");
            if (is == null) {
                System.err.println("EROR: map01.txt tidak ditemukan!");
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
            System.out.println("Peta 100x100 berhasil dimuat!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menandai ID mana saja yang merupakan daun pelindung di atas kepala
    private boolean isForegroundTile(int tileIndex) {
        return tileIndex >= 12 && tileIndex <= 15;
    }

    // LAYER 1: Menggambar tanah, jalan, air, semak, dan batang bawah (Sebelum Player)
    public void renderBackground(Graphics2D g2d, int cameraX, int cameraY) {
        for (int row = 0; row < MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < MAX_WORLD_COLS; col++) {
                int tileIndex = mapData[row][col];

                int worldX = col * GamePanel.TILE_SIZE;
                int worldY = row * GamePanel.TILE_SIZE;
                int screenX = worldX - cameraX;
                int screenY = worldY - cameraY;

                if (worldX + GamePanel.TILE_SIZE > cameraX &&
                    worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                    worldY + GamePanel.TILE_SIZE > cameraY &&
                    worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {

                    if (tileIndex >= 0 && tileIndex < tiles.length && tiles[tileIndex] != null) {
                        if (isForegroundTile(tileIndex)) {
                            // Gambar rumput dasar (ID 0) sebagai alas daun pelindung
                            g2d.drawImage(tiles[0].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                        } else {
                            g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                        }
                    }
                }
            }
        }
    }

    // LAYER 2: Menggambar khusus daun pohon pelindung (Setelah Player)
    public void renderForeground(Graphics2D g2d, int cameraX, int cameraY) {
        for (int row = 0; row < MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < MAX_WORLD_COLS; col++) {
                int tileIndex = mapData[row][col];

                int worldX = col * GamePanel.TILE_SIZE;
                int worldY = row * GamePanel.TILE_SIZE;
                int screenX = worldX - cameraX;
                int screenY = worldY - cameraY;

                if (worldX + GamePanel.TILE_SIZE > cameraX &&
                    worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                    worldY + GamePanel.TILE_SIZE > cameraY &&
                    worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {

                    if (tileIndex >= 0 && tileIndex < tiles.length && tiles[tileIndex] != null && isForegroundTile(tileIndex)) {
                        g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                    }
                }
            }
        }
    }

    // --- LOGIKA BAWAAN ASLI COLLISION DAN RUMPUT LIAR ---
    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return true;
        int tileIndex = mapData[row][col];
        return tileIndex >= 0 && tileIndex < tiles.length && tiles[tileIndex] != null && tiles[tileIndex].solid;
    }

    public boolean isTallGrass(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return false;
        return mapData[row][col] == 2; // ID 2 dihitung sebagai rumput tempat berantem
    }
    
    public int getTileNum(int col, int row) {
        if (col >= 0 && col < MAX_WORLD_COLS && row >= 0 && row < MAX_WORLD_ROWS) {
            return mapData[row][col]; 
        }
        return 0;
    }

    public boolean isCollision(int tileNum) {
        if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null) {
            return tiles[tileNum].solid; 
        }
        return false;
    }

    public int getTileID(int col, int row) {
        if (col >= 0 && col < MAX_WORLD_COLS && row >= 0 && row < MAX_WORLD_ROWS) {
            return mapData[row][col];
        }
        return 1;
    }

    public boolean isTileSolid(int tileIndex) {
        if (tileIndex >= 0 && tileIndex < tiles.length && tiles[tileIndex] != null) {
            return tiles[tileIndex].solid; 
        }
        return false;
    }
}