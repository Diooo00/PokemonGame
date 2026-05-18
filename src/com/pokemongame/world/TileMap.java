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
    private boolean[] isForeground; // Untuk mendeteksi mana yang daun/atap
    
    // ARRAY 3D: [Layer][Baris][Kolom]
    // Layer 0 = Tanah/Rumput, Layer 1 = Objek/Pohon/Pagar
    public int[][][] mapData;

    public static final int MAX_WORLD_COLS = 100;
    public static final int MAX_WORLD_ROWS = 100;
    public static final int MAX_LAYERS = 2; // Kita pakai 2 Layer sekarang

    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        tiles = new Tile[100]; // Kapasitas diperbesar jadi 100 untuk nampung semua list barumu
        isForeground = new boolean[100];
        mapData = new int[MAX_LAYERS][MAX_WORLD_ROWS][MAX_WORLD_COLS];

        loadTileImages();
        
        // Memuat 2 layer peta terpisah (Tanah & Objek)
        loadMapLayer("/map/map01_layer0.txt", 0);
        loadMapLayer("/map/map01_layer1.txt", 1);
    }

    // --- FUNGSI BANTUAN BIAR KODINGAN RAPI & GAK CRASH KALO PNG BELUM ADA ---
    private void setupTile(int id, String imageName, boolean collision, boolean foreground) {
        try {
            tiles[id] = new Tile(SpriteLoader.loadSprite("/tiles/" + imageName + ".png"), collision);
            isForeground[id] = foreground;
        } catch (Exception e) {
            // Abaikan diam-diam kalau file gambarnya belum kamu masukin ke folder
        }
    }

    private void loadTileImages() {
        // GROUND (1-6) - Tidak Collision, Tidak Foreground
        setupTile(1, "dirt", false, false);
        setupTile(2, "grass", false, false);
        setupTile(3, "flower", false, false);
        setupTile(4, "footstep", false, false);
        setupTile(5, "tallgrass", false, false); // ID 5 Tempat Battle
        setupTile(6, "sand", false, false);
        
        // OBSTACLES SEDERHANA (7-10)
        setupTile(7, "sign", true, false);
        setupTile(8, "bush", true, false);
        setupTile(9, "mailbox1", true, false); 
        setupTile(10, "mailbox2", true, false);

        // STONES / BATU (11-20) - Solid
        setupTile(11, "top_stone1", true, false);
        setupTile(12, "top_stone2", true, false);
        setupTile(13, "bottom_stone1", true, false);
        setupTile(14, "bottom_stone2", true, false);
        setupTile(15, "leftbottom_stone", true, false);
        setupTile(16, "rightbottom_stone", true, false);
        setupTile(17, "lefttop_stone", true, false);
        setupTile(18, "righttop_stone", true, false);
        setupTile(19, "left_stone", true, false);
        setupTile(20, "right_stone", true, false);

        // FENCES / PAGAR (21-26) - Solid
        setupTile(21, "bottomhfence", true, false);
        setupTile(22, "tophfence", true, false);
        setupTile(23, "topleft_fence", true, false);
        setupTile(24, "topright_fence", true, false);
        setupTile(25, "bottomright_fence", true, false);
        setupTile(26, "bottoleft_fence", true, false);

        // WATER 3x3 (31-39) - Solid
        for (int i = 1; i <= 9; i++) {
            setupTile(30 + i, "water" + i, true, false);
        }

        // --- SIHIR LAYER: SINGLE TREE (41-46) ---
        // Bagian atas pohon (41-44) tidak solid tapi FOREGROUND (Menutupi kepala player)
        // Bagian batang bawah (45-46) SOLID (Menahan player berjalan)
        setupTile(41, "singletree1", false, true);
        setupTile(42, "singletree2", false, true);
        setupTile(43, "singletree3", false, true);
        setupTile(44, "singletree4", false, true);
        setupTile(45, "singletree5", true, false);
        setupTile(46, "singletree6", true, false);

        // --- SIHIR LAYER: LAYERED TREE (51-56) ---
        setupTile(51, "slayeredtree1", false, true);
        setupTile(52, "slayeredtree2", false, true);
        setupTile(53, "slayeredtree3", false, true);
        setupTile(54, "slayeredtree4", false, true);
        setupTile(55, "slayeredtree5", true, false);
        setupTile(56, "slayeredtree6", true, false);
    }

    private void loadMapLayer(String mapPath, int layer) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            if (is == null) {
                System.out.println("INFO: Peta layer " + layer + " (" + mapPath + ") tidak ada. Melewati...");
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int row = 0; row < MAX_WORLD_ROWS; row++) {
                String line = br.readLine();
                if (line == null) break;
                
                String[] tokens = line.trim().split("\\s+");
                for (int col = 0; col < MAX_WORLD_COLS; col++) {
                    if (col < tokens.length) {
                        mapData[layer][row][col] = Integer.parseInt(tokens[col]);
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LAYER 0 & LAYER 1 (Non-Foreground) - Digambar di bawah kaki player
    public void renderBackground(Graphics2D g2d, int cameraX, int cameraY) {
        // Looping Layer 0 lalu Layer 1
        for (int layer = 0; layer < MAX_LAYERS; layer++) {
            for (int row = 0; row < MAX_WORLD_ROWS; row++) {
                for (int col = 0; col < MAX_WORLD_COLS; col++) {
                    int tileIndex = mapData[layer][row][col];

                    // Abaikan ruang kosong (ID 0) atau tile foreground (Daun pohon)
                    if (tileIndex == 0 || (tileIndex < tiles.length && isForeground[tileIndex])) {
                        continue;
                    }

                    int worldX = col * GamePanel.TILE_SIZE;
                    int worldY = row * GamePanel.TILE_SIZE;
                    int screenX = worldX - cameraX;
                    int screenY = worldY - cameraY;

                    // Culling: Hanya gambar yang masuk ke dalam layar
                    if (worldX + GamePanel.TILE_SIZE > cameraX &&
                        worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                        worldY + GamePanel.TILE_SIZE > cameraY &&
                        worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {

                        if (tileIndex > 0 && tileIndex < tiles.length && tiles[tileIndex] != null) {
                            g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                        }
                    }
                }
            }
        }
    }

    // LAYER 1 (Foreground Khusus) - Digambar menutupi kepala player
    public void renderForeground(Graphics2D g2d, int cameraX, int cameraY) {
        // Cukup cek Layer 1 (karena Layer 0 gak mungkin punya atap)
        for (int row = 0; row < MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < MAX_WORLD_COLS; col++) {
                int tileIndex = mapData[1][row][col];

                // Hanya render jika ini tile Foreground (misal ID 41-44 / pucuk daun)
                if (tileIndex > 0 && tileIndex < tiles.length && isForeground[tileIndex]) {
                    
                    int worldX = col * GamePanel.TILE_SIZE;
                    int worldY = row * GamePanel.TILE_SIZE;
                    int screenX = worldX - cameraX;
                    int screenY = worldY - cameraY;

                    if (worldX + GamePanel.TILE_SIZE > cameraX &&
                        worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                        worldY + GamePanel.TILE_SIZE > cameraY &&
                        worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {

                        if (tiles[tileIndex] != null) {
                            g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
                        }
                    }
                }
            }
        }
    }

    // --- LOGIKA BAWAAN ASLI COLLISION DAN RUMPUT LIAR ---
    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return true;
        
        // Cek solid dari Layer 0 (Misal Air) dan Layer 1 (Misal Pagar/Batang Pohon)
        int tile0 = mapData[0][row][col];
        int tile1 = mapData[1][row][col];
        
        boolean solid0 = (tile0 > 0 && tile0 < tiles.length && tiles[tile0] != null && tiles[tile0].solid);
        boolean solid1 = (tile1 > 0 && tile1 < tiles.length && tiles[tile1] != null && tiles[tile1].solid);
        
        return solid0 || solid1;
    }

    public boolean isTallGrass(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return false;
        // Sekarang tallgrass dipindah ke ID 5
        return mapData[0][row][col] == 5 || mapData[1][row][col] == 5; 
    }
}