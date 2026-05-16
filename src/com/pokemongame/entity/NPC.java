/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author thety
 */
public class NPC extends Entity {

    private String[] dialogLines;
    private int dialogIndex = 0;
    private int dialogueIndex = 0;
    private String[] dialogues;

    public NPC(GamePanel gamePanel, int worldX, int worldY, String[] dialogLines) {
        super(gamePanel);
        this.worldX = worldX;
        this.worldY = worldY;
        this.dialogLines = dialogLines;
        this.speed = 0; // NPC tidak bergerak untuk sekarang
    }

    @Override
    public void update() {
        // NPC statis — tidak ada logika gerak untuk sekarang
    }

    public String getCurrentDialog() {
        if (dialogIndex < dialogLines.length) {
            return dialogLines[dialogIndex];
        }
        return null;
    }
    
    public void speak() {
        if (dialogues != null && dialogues.length > 0) {
            // Menampilkan dialog ke console untuk memastikan kodenya bekerja
            System.out.println("NPC: " + dialogues[dialogueIndex]);

            // Logika sederhana: ganti ke baris dialog berikutnya saat ditekan lagi
            dialogueIndex++;
            if (dialogueIndex >= dialogues.length) {
                dialogueIndex = 0; // Reset ke dialog pertama jika sudah habis
            }

            // JIKA kamu punya UI Dialogue Box di GamePanel, kamu bisa memicunya di sini:
            // gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
            // gamePanel.gameState = gamePanel.DIALOGUE_STATE;
        }
    }

    public void nextDialog() {
        dialogIndex++;
    }

    public boolean hasMoreDialog() {
        return dialogIndex < dialogLines.length;
    }

    public void resetDialog() {
        dialogIndex = 0;
    }

    public void render(Graphics2D g2d) {
        // kosong — NPC selalu render dengan camera
    }

    @Override
    public void render(Graphics2D g2d, int cameraX, int cameraY) {
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;

        // Placeholder — kotak coklat
        g2d.setColor(new Color(139, 90, 43));
        g2d.fillRect(screenX, screenY,
                     GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

        // Tanda kepala
        g2d.setColor(new Color(255, 220, 177));
        g2d.fillOval(screenX + 8, screenY + 2, 16, 16);
    }
}

