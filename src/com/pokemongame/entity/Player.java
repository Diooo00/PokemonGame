/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;
import com.pokemongame.input.KeyHandler;
import com.pokemongame.state.DialogState;
import com.pokemongame.state.OverworldState;
import com.pokemongame.util.SpriteLoader;
import com.pokemongame.util.DatabaseManager;
import com.pokemongame.util.CollisionChecker;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.*;
/**
 *
 * @author thety
 */
public class Player extends Entity {
    
    KeyHandler keyHandler;
    BufferedImage[][] playerSprites;
    CollisionChecker cChecker;
    
    private final int ROWS = 4; // Baris arah
    private final int COLS = 3; // Kolom animasi
    private int spriteCounter = 0;
    private int spriteCol = 0; 
    private int spriteRow = 0; 

    public BufferedImage up1, down1, left1, right1;
    
    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);
        this.gamePanel = gp;
        this.keyHandler = keyHandler;
        this.cChecker = new CollisionChecker(gp);
        this.speed = 6;
        
        setupPlayerSprites();
        loadPosition();
    }

    private void setupPlayerSprites() {
        BufferedImage fullSheet = SpriteLoader.loadSprite("/sprites/player.png");
        if (fullSheet == null) return;
        playerSprites = new BufferedImage[ROWS][COLS];
        int sW = fullSheet.getWidth() / COLS;
        int sH = fullSheet.getHeight() / ROWS;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                playerSprites[r][c] = fullSheet.getSubimage(c * sW, r * sH, sW, sH);
            }
        }
    }

    @Override
    public void update() {
        boolean isMoving = false;
        collisionOn = false;

        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
            if (keyHandler.downPressed) { direction = "DOWN"; spriteRow = 0; isMoving = true; }
            else if (keyHandler.upPressed) { direction = "UP"; spriteRow = 1; isMoving = true; }
            else if (keyHandler.leftPressed) { direction = "LEFT"; spriteRow = 2; isMoving = true; }
            else if (keyHandler.rightPressed) { direction = "RIGHT"; spriteRow = 3; isMoving = true; }

            gamePanel.cChecker.checkTile(this);
            
            if (collisionOn == false) {
                switch(direction) {
                    case "UP": worldY -= speed; break;
                    case "DOWN": worldY += speed; break;
                    case "LEFT": worldX -= speed; break;
                    case "RIGHT": worldX += speed; break;
                }
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteCol = (spriteCol == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteCol = 0;
            spriteCounter = 0;
        }
    }

    public void render(Graphics2D g2d) {
        // 1. Ambil gambar dari array sprite
        BufferedImage image = playerSprites[spriteRow][spriteCol];
        if (playerSprites != null && spriteRow < playerSprites.length && spriteCol < playerSprites[0].length) {
            image = playerSprites[spriteRow][spriteCol];
        }

        // 2. Hitung posisi layar
        int screenX = worldX - gamePanel.getCamera().x;
        int screenY = worldY - gamePanel.getCamera().y;

        System.out.println("Player World: " + worldX + "," + worldY + 
                   " | Camera: " + gamePanel.getCamera().x + "," + gamePanel.getCamera().y + 
                   " | Screen: " + screenX + "," + screenY);
        
        // 3. Hanya gambar jika gambar ada
        if (image != null) {
            g2d.drawImage(image, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
        } else {
            // DEBUG: Jika masuk ke sini, artinya gambarmu belum ter-load!
            System.out.println("ERROR: Player sprite is NULL! Row: " + spriteRow + " Col: " + spriteCol);
         }
    }

    public void loadPosition() {
        String sql = "SELECT * FROM player_save WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                this.worldX = GamePanel.TILE_SIZE * 50;
                this.worldY = GamePanel.TILE_SIZE * 50;
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void interact() {
        OverworldState os = gamePanel.getOverworldState();
        if (os != null) {
            int npcIndex = gamePanel.cChecker.checkEntity(this, os.getNPCs());

            if (npcIndex != 999) {
                NPC targetNPC = os.getNPCs()[npcIndex];
                gamePanel.setCurrentState(new DialogState(gamePanel, targetNPC, gamePanel.getCurrentState()));
            }
        }
    }
}
