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
import com.pokemongame.util.SaveManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.*;

/**
 *
 * @author thety
 */
public class Player extends Entity {
    
    KeyHandler keyHandler;
    CollisionChecker cChecker;
    
    // --- VARIABEL ANIMASI BARU ---
    private int spriteCounter = 0;
    private int spriteNum = 1; // 1 = Idle, 2 = Walk 1, 3 = Walk 2
    private int walkSequence = 0; // Buat ngatur urutan (Kaki 1 -> Idle -> Kaki 2 -> Idle)

    // Wadah khusus untuk masing-masing potongan gambar
    public BufferedImage up1, up2, up3;
    public BufferedImage down1, down2, down3;
    public BufferedImage left1, left2, left3;
    public BufferedImage right1, right2, right3;
    
    public Player(GamePanel gp, KeyHandler keyHandler) {
        super(gp);
        this.gamePanel = gp;
        this.keyHandler = keyHandler;
//        this.cChecker = new CollisionChecker(gp);
        this.worldX = GamePanel.TILE_SIZE * 61; 
        this.worldY = GamePanel.TILE_SIZE * 62;
        this.speed = 7;
        this.direction = "DOWN"; // Set arah default biar nggak error pas pertama spawn
        
        setupPlayerSprites();
        
        this.hitbox = new java.awt.Rectangle(8, 16, 32, 32);
        this.hitboxDefaultX = this.hitbox.x;
        this.hitboxDefaultY = this.hitbox.y;
        SaveManager.loadGame(this);
    }

    private void setupPlayerSprites() {
        // PERHATIAN: Pastikan path ini sesuai sama letak gambar player barumu!
        BufferedImage fullSheet = SpriteLoader.loadSprite("/sprites/player.png"); 
        if (fullSheet == null) {
            System.out.println("ERROR: player.png gagal dimuat!");
            return;
        }

        // Karena layout 3x4
        int sW = fullSheet.getWidth() / 3;
        int sH = fullSheet.getHeight() / 4;

        // --- POTONG MANUAL SESUAI POSISI DARI KAMU ---
        
        // ARAH ATAS
        up1 = fullSheet.getSubimage(0 * sW, 0 * sH, sW, sH); // Idle Atas (Row 1, Col 1)
        up2 = fullSheet.getSubimage(2 * sW, 0 * sH, sW, sH); // Jalan Atas 1 (Row 1, Col 3)
        up3 = fullSheet.getSubimage(1 * sW, 3 * sH, sW, sH); // Jalan Atas 2 (Row 4, Col 2)

        // ARAH KANAN
        right1 = fullSheet.getSubimage(1 * sW, 0 * sH, sW, sH); // Idle Kanan (Row 1, Col 2)
        right2 = fullSheet.getSubimage(1 * sW, 1 * sH, sW, sH); // Jalan Kanan 1 (Row 2, Col 2)
        right3 = fullSheet.getSubimage(1 * sW, 2 * sH, sW, sH); // Jalan Kanan 2 (Row 3, Col 2)

        // ARAH KIRI
        left1 = fullSheet.getSubimage(0 * sW, 2 * sH, sW, sH); // Idle Kiri (Row 3, Col 1)
        left2 = fullSheet.getSubimage(0 * sW, 1 * sH, sW, sH); // Jalan Kiri 1 (Row 2, Col 1)
        left3 = fullSheet.getSubimage(0 * sW, 3 * sH, sW, sH); // Jalan Kiri 2 (Row 4, Col 1)

        // ARAH BAWAH
        down1 = fullSheet.getSubimage(2 * sW, 1 * sH, sW, sH); // Idle Bawah (Row 2, Col 3)
        down2 = fullSheet.getSubimage(2 * sW, 2 * sH, sW, sH); // Jalan Bawah 1 (Row 3, Col 3)
        down3 = fullSheet.getSubimage(2 * sW, 3 * sH, sW, sH); // Jalan Bawah 2 (Row 4, Col 3)
    }

    @Override
    public void update() {
        if (keyHandler.upPressed || keyHandler.downPressed || keyHandler.leftPressed || keyHandler.rightPressed) {
            
            // 1. Tentukan Arah
            if (keyHandler.upPressed) { direction = "UP"; }
            else if (keyHandler.downPressed) { direction = "DOWN"; }
            else if (keyHandler.leftPressed) { direction = "LEFT"; }
            else if (keyHandler.rightPressed) { direction = "RIGHT"; }

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

            // 4. Logika Ganti Gambar Animasi (Jalan ala Pokemon)
            spriteCounter++;
            if (spriteCounter > 12) { // 12 adalah kecepatan animasi kaki, bisa dikecilin biar makin cepet
                walkSequence++;
                if (walkSequence > 3) {
                    walkSequence = 0; // Reset siklus
                }

                // Pola: Idle -> Kaki 1 -> Idle -> Kaki 2
                if (walkSequence == 0 || walkSequence == 2) {
                    spriteNum = 1; // Posenya Idle
                } else if (walkSequence == 1) {
                    spriteNum = 2; // Angkat kaki pertama
                } else if (walkSequence == 3) {
                    spriteNum = 3; // Angkat kaki sebelahnya
                }
                spriteCounter = 0;
            }
        } else {
            // JIKA TOMBOL DILEPAS: Langsung setel jadi berdiri tegak (Idle)
            spriteNum = 1; 
            walkSequence = 0;
        }
    }

    public void render(Graphics2D g2d) {
        int screenX = worldX - gamePanel.getCamera().x;
        int screenY = worldY - gamePanel.getCamera().y;

        BufferedImage image = null;

        // Pilih gambar berdasarkan Arah dan Pose (Idle / Kaki 1 / Kaki 2)
        switch (direction) {
            case "UP":
                if (spriteNum == 1) image = up1;
                else if (spriteNum == 2) image = up2;
                else if (spriteNum == 3) image = up3;
                break;
            case "DOWN":
                if (spriteNum == 1) image = down1;
                else if (spriteNum == 2) image = down2;
                else if (spriteNum == 3) image = down3;
                break;
            case "LEFT":
                if (spriteNum == 1) image = left1;
                else if (spriteNum == 2) image = left2;
                else if (spriteNum == 3) image = left3;
                break;
            case "RIGHT":
                if (spriteNum == 1) image = right1;
                else if (spriteNum == 2) image = right2;
                else if (spriteNum == 3) image = right3;
                break;
        }
        
        if (image != null) {
            // --- JURUS MEMBESARKAN KARAKTER ---
            // Atur seberapa besar karakternya di sini (Misal: 1.5x lipat)
            double scaleModifier = 1.5; 
            int drawSize = (int) (gamePanel.TILE_SIZE * scaleModifier);
            
            // Biar posisinya tetep di tengah kotak dan kakinya napak:
            // Geser X ke kiri setengah dari kelebihan lebarnya
            int drawX = screenX - ((drawSize - gamePanel.TILE_SIZE) / 2);
            // Geser Y full ke atas biar yang membesar kepalanya, bukan kakinya nembus tanah
            int drawY = screenY - (drawSize - gamePanel.TILE_SIZE);

            g2d.drawImage(image, drawX, drawY, drawSize, drawSize, null);
        } else {
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