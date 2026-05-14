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
        
        this.hitbox = new java.awt.Rectangle(8, 16, 32, 32);
        
        this.hitboxDefaultX = this.hitbox.x;
        this.hitboxDefaultY = this.hitbox.y;
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
            
            // 1. Tentukan arah karakter menghadap ke mana
            if (keyHandler.upPressed) { direction = "up"; }
            else if (keyHandler.downPressed) { direction = "down"; }
            else if (keyHandler.leftPressed) { direction = "left"; }
            else if (keyHandler.rightPressed) { direction = "right"; }
            
            // 2. Cek apakah di depannya ada tembok (Collision)
            collisionOn = false;
            if (cChecker != null) {
                cChecker.checkTile(this);
            }
            
            // 3. JIKA TIDAK ADA TEMBOK (!collisionOn), BARU BOLEH JALAN
            if (collisionOn == false) {
                switch (direction) {
                    case "UP": 
                    case "up": worldY -= speed; break;
                    
                    case "DOWN": 
                    case "down": worldY += speed; break;
                    
                    case "LEFT": 
                    case "left": worldX -= speed; break;  // Ini Obat Kiri
                    
                    case "RIGHT": 
                    case "right": worldX += speed; break; // Ini Obat Kanan
                }
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteCol = (spriteCol == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteCol = 0;
            spriteCounter = 0;
        }
    }

    public void render(Graphics2D g2d) {
        // Menghitung posisi render di layar relatif terhadap kamera
        int screenX = (gamePanel.SCREEN_WIDTH / 2) - (gamePanel.TILE_SIZE / 2);
        int screenY = (gamePanel.SCREEN_HEIGHT / 2) - (gamePanel.TILE_SIZE / 2);

        // Ambil gambar sprite sesuai arah (sesuaikan variabel dengan kodemu)
        BufferedImage image = null; // Default ke arah bawah jika animasi belum jalan

        if (playerSprites != null) {
            image = playerSprites[spriteRow][spriteCol]; 
        }
        
        if (image != null) {
            // Jika sprite ketemu, gambar karakternya
            g2d.drawImage(image, screenX, screenY, gamePanel.TILE_SIZE, gamePanel.TILE_SIZE, null);
        } else {
            // JIKA SPRITE GAGAL DIMUAT, TAMPILKAN KOTAK MERAH (Untuk Debugging)
            g2d.setColor(java.awt.Color.RED);
            g2d.fillRect(screenX, screenY, gamePanel.TILE_SIZE, gamePanel.TILE_SIZE);
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
