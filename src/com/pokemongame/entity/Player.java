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
//        this.cChecker = new CollisionChecker(gp);
        this.worldX = GamePanel.TILE_SIZE * 50; 
        this.worldY = GamePanel.TILE_SIZE * 50;
        this.speed = 7;
        
        setupPlayerSprites();
//        loadPosition();
        
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
    // Cek apakah ada tombol arah yang sedang ditekan
    if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
        
        // 1. Tentukan Arah dan Ganti Baris Sprite (spriteRow)
        if (keyHandler.upPressed) {
            direction = "UP";
            spriteRow = 1; // Baris ke-2 di player.png (Atas)
        } else if (keyHandler.downPressed) {
            direction = "DOWN";
            spriteRow = 0; // Baris ke-1 di player.png (Bawah)
        } else if (keyHandler.leftPressed) {
            direction = "LEFT";
            spriteRow = 2; // Baris ke-3 di player.png (Kiri)
        } else if (keyHandler.rightPressed) {
            direction = "RIGHT";
            spriteRow = 3; // Baris ke-4 di player.png (Kanan)
        }

        // 2. Logika Collision
        collisionOn = false;
        gamePanel.cChecker.checkTile(this);

        // 3. Logika Pergerakan
        if (!collisionOn) {
            switch (direction) {
                case "UP":    worldY -= speed; break;
                case "DOWN":  worldY += speed; break;
                case "LEFT":  worldX -= speed; break;
                case "RIGHT": worldX += speed; break;
            }
        }

        // 4. Logika Jalannya Animasi (Ganti Kolom/spriteCol)
        spriteCounter++;
        if (spriteCounter > 12) { // Kecepatan ganti frame
            spriteCol++;
            if (spriteCol >= COLS) { 
                spriteCol = 0; // Reset ke frame pertama jika sudah mentok
            }
            spriteCounter = 0;
        }
    } else {
        // OPSIONAL: Jika tidak gerak, kembalikan ke frame berdiri diam (kolom tengah)
        spriteCol = 1; 
    }
}

    public void render(Graphics2D g2d) {
        // Menghitung posisi render di layar relatif terhadap kamera
        int screenX = worldX - gamePanel.getCamera().x;
        int screenY = worldY - gamePanel.getCamera().y;

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
        String sql = "SELECT world_x, world_y FROM player_save WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                this.worldX = rs.getInt("world_x");
                this.worldY = rs.getInt("world_y");
            }
        } catch (SQLException e) { 
            // Jika DB mati, gunakan posisi default agar tidak bug
            this.worldX = GamePanel.TILE_SIZE * 50;
            this.worldY = GamePanel.TILE_SIZE * 50;
        }
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