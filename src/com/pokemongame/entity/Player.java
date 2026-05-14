/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;
import com.pokemongame.input.KeyHandler;
import com.pokemongame.world.TileMap;
import com.pokemongame.util.SpriteLoader;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
/**
 *
 * @author thety
 */
public class Player extends Entity {
    private KeyHandler keyHandler;
    private BufferedImage[][] playerSprites;
    
    public enum Direction { DOWN, LEFT, RIGHT, UP }
    public Direction direction = Direction.DOWN;

    private int spriteCounter = 0;
    private int spriteNum = 0; 

    // SESUAI GAMBAR KAMU: 4 frame menyamping, 1 baris
    private final int COLS = 4; 
    private final int ROWS = 1; 

    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        super(gamePanel);
        this.keyHandler = keyHandler;
        this.speed = 4;
        
        // Titik spawn
        this.worldX = GamePanel.TILE_SIZE * 15;
        this.worldY = GamePanel.TILE_SIZE * 15;
        
        // Hitbox biar enak jalannya
        this.hitbox = new Rectangle(8, 16, 16, 16);
        
        setupPlayerSprites();
    }

    private void setupPlayerSprites() {
        BufferedImage fullSheet = SpriteLoader.loadSprite("/sprites/player.png");
        if (fullSheet == null) return;

        playerSprites = new BufferedImage[ROWS][COLS];

        // INI KUNCINYA: Java menghitung lebar asli per frame
        int singleFrameWidth = fullSheet.getWidth() / COLS;
        int singleFrameHeight = fullSheet.getHeight() / ROWS;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                playerSprites[row][col] = fullSheet.getSubimage(
                    col * singleFrameWidth, 
                    row * singleFrameHeight, 
                    singleFrameWidth, 
                    singleFrameHeight
                );
            }
        }
    }

    @Override
    public void update() {
        boolean isMoving = false;
        
        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
            isMoving = true;
            int nextX = worldX;
            int nextY = worldY;

            if (keyHandler.downPressed) { direction = Direction.DOWN; nextY += speed; }
            else if (keyHandler.leftPressed) { direction = Direction.LEFT; nextX -= speed; }
            else if (keyHandler.rightPressed) { direction = Direction.RIGHT; nextX += speed; }
            else if (keyHandler.upPressed) { direction = Direction.UP; nextY -= speed; }

            if (!checkCollision(nextX, nextY)) {
                worldX = nextX;
                worldY = nextY;
            }
        }

        if (isMoving) {
            spriteCounter++;
            if (spriteCounter > 12) { 
                spriteNum++; 
                if (spriteNum >= COLS) spriteNum = 0; 
                spriteCounter = 0;
            }
        } else {
            spriteNum = 0; 
        }
    }

    private boolean checkCollision(int nextX, int nextY) {
        int leftTile = (nextX + hitbox.x) / GamePanel.TILE_SIZE;
        int rightTile = (nextX + hitbox.x + hitbox.width) / GamePanel.TILE_SIZE;
        int topTile = (nextY + hitbox.y) / GamePanel.TILE_SIZE;
        int bottomTile = (nextY + hitbox.y + hitbox.height) / GamePanel.TILE_SIZE;

        TileMap tm = gamePanel.getTileMap();
        return tm.isSolid(topTile, leftTile) || tm.isSolid(topTile, rightTile) ||
               tm.isSolid(bottomTile, leftTile) || tm.isSolid(bottomTile, rightTile);
    }

    @Override
    public void render(Graphics2D g2d, int cameraX, int cameraY) {
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        if (playerSprites != null && playerSprites[0][spriteNum] != null) {
            BufferedImage image = playerSprites[0][spriteNum];
            
            // AGAR TIDAK GEPENG: 
            // Kita gambar sesuai ukuran asli potongan gambarnya, 
            // tapi diskalakan ke TILE_SIZE. 
            // Jika gambar aslimu tidak kotak (misal 16x20), g2d akan memaksanya jadi kotak TILE_SIZE.
        g2d.drawImage(image, screenX, screenY, null);        }
    }
}
