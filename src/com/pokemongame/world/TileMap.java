package com.pokemongame.world;

import com.pokemongame.main.GamePanel;
import com.pokemongame.util.SpriteLoader;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileMap {
    private GamePanel gamePanel;
    private Tile[] tiles;
    private boolean[] isForeground; 
    
    public int[][][] mapData;

    public static final int MAX_WORLD_COLS = 100;
    public static final int MAX_WORLD_ROWS = 100;
    public static final int MAX_LAYERS = 2; 

    public TileMap(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        tiles = new Tile[200]; 
        isForeground = new boolean[200];
        mapData = new int[MAX_LAYERS][MAX_WORLD_ROWS][MAX_WORLD_COLS];

        loadTileImages();
        
        loadMapLayer("/map/map01_layer0.csv", 0);
        loadMapLayer("/map/map01_layer1.csv", 1);
    }

    private void setupTile(int id, String imageName, boolean collision, boolean foreground) {
        try {
            tiles[id] = new Tile(SpriteLoader.loadSprite("/tiles/" + imageName + ".png"), collision);
            isForeground[id] = foreground;
        } catch (Exception e) {}
    }

    private void loadTileImages() {
        // GROUND
        setupTile(6, "dirt", false, false);
        setupTile(9, "grass", false, false);
        setupTile(79, "grass2", false, false);
        setupTile(80, "grass3", false, false);
        setupTile(81, "grass4", false, false);
        setupTile(82, "grass5", false, false);
        setupTile(83, "grass6", false, false);
        setupTile(84, "grass7", false, false);
        setupTile(85, "grass8", false, false);
        setupTile(86, "grass9", false, false);
        setupTile(7, "flower", false, false);
        setupTile(8, "footstep", false, false);
        setupTile(102, "footstep2", false, false);
        setupTile(103, "footstep3", false, false);
        setupTile(35, "tallgrass", false, false); 
        setupTile(21, "sand", false, false);
        setupTile(5, "bush", false, false);
        
        // OBSTACLES SEDERHANA
        setupTile(22, "sign", true, false);
        setupTile(15, "mailbox1", true, false); 
        setupTile(16, "mailbox2", false, false);

        // STONES
        setupTile(36, "top_stone1", true, false);
        setupTile(37, "top_stone2", true, false);
        setupTile(0, "bottom_stone1", true, false);
        setupTile(1, "bottom_stone2", true, false);
        setupTile(11, "leftbottom_stone", true, false);
        setupTile(18, "rightbottom_stone", true, false);
        setupTile(12, "lefttop_stone", true, false);
        setupTile(19, "righttop_stone", true, false);
        setupTile(10, "left_stone", true, false);
        setupTile(17, "right_stone", true, false);

        // FENCES
        setupTile(2, "bottomhfence", true, false);
        setupTile(38, "tophfence", true, false);
        setupTile(39, "topleft_fence", true, false);
        setupTile(40, "topright_fence", true, false);
        setupTile(4, "bottomright_fence", true, false);
        setupTile(3, "bottomleft_fence", true, false);
        setupTile(13, "leftvfence", true, false);
        setupTile(14, "little_tree", true, false);
        setupTile(20, "rightvfence", true, false);
        
        // POHON & DAUN (Pucuknya di-set Foreground TRUE!)
        setupTile(23, "singletree1", true, true); 
        setupTile(24, "singletree2", true, true); 
        setupTile(25, "singletree3", true, false); // Batang
        setupTile(26, "singletree4", true, false); // Batang
        setupTile(27, "singletree5", true, false); // Batang
        setupTile(28, "singletree6", true, false); // Batang
        setupTile(29, "slayeredtree1", true, true); // Daun
        setupTile(30, "slayeredtree2", true, true); // Daun
        setupTile(31, "slayeredtree3", true, true); // Daun
        setupTile(32, "slayeredtree4", true, true); // Daun
        setupTile(33, "slayeredtree5", true, false); // Batang
        setupTile(34, "slayeredtree6", true, false); // Batang
        
        // WATER
        for (int i=41; i<=49; i++) setupTile(i, "water"+(i-40), true, false);
        
        // BRIDGES (Diinjak)
        for (int i=50; i<=69; i++) {
            boolean solid = (i==50 || i==54 || i==55 || i==59 || i==60 || i==64 || i==65 || i==69);
            setupTile(i, "bridge"+(i-49), solid, false);
        }
        
        // CORNER WALLS
        String[] wcorners = {"wcorner_b", "wcorner_bl", "wcorner_br", "wcorner_l", "wcorner_m", "wcorner_r", "wcorner_t", "wcorner_tl", "wcorner_tp"};
        for (int i=0; i<wcorners.length; i++) setupTile(70+i, wcorners[i], true, false);

        // KANOPI STAND (Atap melayang)
        setupTile(87, "stand1", false, true);
        setupTile(88, "stand2", false, true);
        setupTile(89, "stand3", false, true);
        setupTile(90, "stand4", false, true);
        setupTile(91, "stand5", false, true);
        setupTile(92, "stand6", false, true);
        setupTile(93, "stand7", true, false); // Tiang Solid
        setupTile(95, "stand9", true, false); // Tiang Solid

        // TUGU (Pucuk melayang)
        setupTile(96, "tugu1", false, true);
        setupTile(97, "tugu2", false, true);
        setupTile(98, "tugu3", true, false);
        setupTile(99, "tugu4", true, false);
        setupTile(100, "tugu5", true, false);
        setupTile(101, "tugu6", true, false);
    }

    private void loadMapLayer(String mapPath, int layer) {
        try {
            InputStream is = getClass().getResourceAsStream(mapPath);
            if (is == null) return;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (int row = 0; row < MAX_WORLD_ROWS; row++) {
                String line = br.readLine();
                if (line == null) break;
                String[] tokens = line.trim().split(",");
                for (int col = 0; col < MAX_WORLD_COLS; col++) {
                    if (col < tokens.length) {
                        try {
                            int tileVal = Integer.parseInt(tokens[col].trim());
                            if (tileVal < 0) tileVal = 0; // FIX Tiled Error
                            mapData[layer][row][col] = tileVal;
                        } catch (Exception e) {
                            mapData[layer][row][col] = 0;
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void renderBackground(Graphics2D g2d, int cameraX, int cameraY) {
        for (int layer = 0; layer < MAX_LAYERS; layer++) {
            for (int row = 0; row < MAX_WORLD_ROWS; row++) {
                for (int col = 0; col < MAX_WORLD_COLS; col++) {
                    int tileIndex = mapData[layer][row][col];
                    if (tileIndex == 0 || (tileIndex < tiles.length && isForeground[tileIndex])) continue;

                    int worldX = col * GamePanel.TILE_SIZE;
                    int worldY = row * GamePanel.TILE_SIZE;
                    int screenX = worldX - cameraX;
                    int screenY = worldY - cameraY;

                    if (worldX + GamePanel.TILE_SIZE > cameraX && worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                        worldY + GamePanel.TILE_SIZE > cameraY && worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {
                        if (tileIndex > 0 && tileIndex < tiles.length && tiles[tileIndex] != null) {
                            // --- FIX GRID GARIS (+1 PIXEL) ---
                            g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE + 1, GamePanel.TILE_SIZE + 1, null);
                        }
                    }
                }
            }
        }
    }

    public void renderForeground(Graphics2D g2d, int cameraX, int cameraY) {
        for (int row = 0; row < MAX_WORLD_ROWS; row++) {
            for (int col = 0; col < MAX_WORLD_COLS; col++) {
                int tileIndex = mapData[1][row][col];
                if (tileIndex > 0 && tileIndex < tiles.length && isForeground[tileIndex]) {
                    int worldX = col * GamePanel.TILE_SIZE;
                    int worldY = row * GamePanel.TILE_SIZE;
                    int screenX = worldX - cameraX;
                    int screenY = worldY - cameraY;

                    if (worldX + GamePanel.TILE_SIZE > cameraX && worldX - GamePanel.TILE_SIZE < cameraX + GamePanel.SCREEN_WIDTH &&
                        worldY + GamePanel.TILE_SIZE > cameraY && worldY - GamePanel.TILE_SIZE < cameraY + GamePanel.SCREEN_HEIGHT) {
                        if (tiles[tileIndex] != null) {
                            // --- FIX GRID GARIS (+1 PIXEL) ---
                            g2d.drawImage(tiles[tileIndex].image, screenX, screenY, GamePanel.TILE_SIZE + 1, GamePanel.TILE_SIZE + 1, null);
                        }
                    }
                }
            }
        }
    }

    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return true;
        int tile0 = mapData[0][row][col];
        int tile1 = mapData[1][row][col];
        boolean solid0 = (tile0 > 0 && tile0 < tiles.length && tiles[tile0] != null && tiles[tile0].solid);
        boolean solid1 = (tile1 > 0 && tile1 < tiles.length && tiles[tile1] != null && tiles[tile1].solid);
        return solid0 || solid1;
    }

    public boolean isTallGrass(int row, int col) {
        if (row < 0 || row >= MAX_WORLD_ROWS || col < 0 || col >= MAX_WORLD_COLS) return false;
        return mapData[0][row][col] == 35 || mapData[1][row][col] == 35; 
    }
}