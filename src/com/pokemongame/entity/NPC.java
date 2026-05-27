/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author thety
 */
public class NPC extends Entity {
    
    // Tiap NPC sekarang punya gambarnya sendiri-sendiri!
    private BufferedImage sprite; 
    private String[] dialogLines;
    private int dialogIndex = 0;
    
    // --- CONSTRUCTOR BARU: Minta parameter String spriteName ---
    public NPC(GamePanel gamePanel, int worldX, int worldY, String[] dialogLines, String spriteName) {
        super(gamePanel);
        this.worldX = worldX;
        this.worldY = worldY;
        this.dialogLines = dialogLines;
        this.speed = 0; 
        
        loadSprite(spriteName); // Panggil fungsi buat muat gambar
    }

    // --- FUNGSI MUAT GAMBAR ---
    private void loadSprite(String spriteName) {
        try {
            // Bakal nyari file sesuai nama, misal "res/sprites/nurse_joy.png"
            File file = new File("res/sprites/" + spriteName + ".png");
            BufferedImage sheet = ImageIO.read(file);
            
            // Kita pakai gambar utuh. 
            // (Kalau ternyata pas di-Run gambarnya kelebaran / dempet 2 orang, 
            // kabarin aku ya, nanti gampang tinggal kita potong kodingannya)
            this.sprite = sheet; 
            
        } catch (Exception e) {
            System.err.println("EROR: Gagal memuat sprite NPC -> " + spriteName);
            e.printStackTrace();
            this.sprite = null;
        }
    }

    @Override
    public void update() {
        // NPC statis — tidak ada logika gerak untuk sekarang
    }

    public void speak() {
        System.out.println("NPC: " + dialogLines[dialogIndex]);
        dialogIndex++;
        if (dialogIndex >= dialogLines.length) {
            dialogIndex = 0; 
        }
    }

    public void render(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        int cameraX = gamePanel.getCamera().x;
        int cameraY = gamePanel.getCamera().y;

        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        if (sprite != null) {
            g2d.drawImage(sprite, screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
        } else {
            g2d.setColor(Color.GRAY);
            g2d.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
            g2d.setColor(Color.RED);
            g2d.drawLine(screenX, screenY, screenX + GamePanel.TILE_SIZE, screenY + GamePanel.TILE_SIZE);
            g2d.drawLine(screenX + GamePanel.TILE_SIZE, screenY, screenX, screenY + GamePanel.TILE_SIZE);
        }
    }
    
    // --- KUMPULAN FUNGSI DIALOG ---
    public String getCurrentDialog() {
        if (dialogLines != null && dialogIndex < dialogLines.length) {
            return dialogLines[dialogIndex];
        }
        return null;
    }

    public void nextDialog() {
        dialogIndex++;
    }

    public boolean hasMoreDialog() {
        return dialogLines != null && dialogIndex < dialogLines.length;
    }

    public void resetDialog() {
        dialogIndex = 0;
    }
}