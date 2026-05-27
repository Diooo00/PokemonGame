/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.ui;

import com.pokemongame.main.GamePanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author thety
 */
public class DialogBox {
    private java.awt.Font pokemonFont;
    private static final int BOX_X      = 20;
    private static final int BOX_HEIGHT = 100;
    private int boxY;
    private int boxWidth;

    // Teks yang sedang ditampilkan (efek typewriter)
    private String fullText    = "";
    private String displayText = "";
    private int charIndex      = 0;
    private int typewriterDelay = 0;
    private static final int TYPEWRITER_SPEED = 2; // frame per karakter

    private boolean finished = false;

    public DialogBox(GamePanel gamePanel) {
        try {
            java.io.File fontFile = new java.io.File("res/font/PKMN RBYGSC.ttf"); 
            this.pokemonFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontFile).deriveFont(20f);
        } catch (Exception e) {
            this.pokemonFont = new java.awt.Font("Monospaced", java.awt.Font.BOLD, 20);
        }
        
        boxWidth = GamePanel.SCREEN_WIDTH - 40;
        boxY     = GamePanel.SCREEN_HEIGHT - BOX_HEIGHT - 20;
    }

    public void setText(String text) {
        fullText    = text;
        displayText = "";
        charIndex   = 0;
        finished    = false;
    }

    public void update() {
        if (charIndex < fullText.length()) {
            typewriterDelay++;
            if (typewriterDelay >= TYPEWRITER_SPEED) {
                displayText += fullText.charAt(charIndex);
                charIndex++;
                typewriterDelay = 0;
            }
        } else {
            finished = true;
        }
    }

    // Skip langsung ke teks penuh
    public void skipToEnd() {
        displayText = fullText;
        charIndex   = fullText.length();
        finished    = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public void render(Graphics2D g2d) {
        // Background kotak dialog
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(BOX_X, boxY, boxWidth, BOX_HEIGHT, 12, 12);

        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(BOX_X, boxY, boxWidth, BOX_HEIGHT, 12, 12);

        // Teks
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(pokemonFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString(displayText, BOX_X + 16, boxY + 36);

        // Indikator "tekan Z untuk lanjut" saat teks selesai
        if (isFinished()) {
            // Efek kedip: Tampil 500ms, Hilang 500ms
            if (System.currentTimeMillis() % 1000 < 500) {
                g2d.setColor(java.awt.Color.WHITE);
                
                // Posisikan di pojok kanan bawah layar
                int arrowX = GamePanel.SCREEN_WIDTH - 60;
                int arrowY = GamePanel.SCREEN_HEIGHT - 45;
                
                // Gambar Segitiga Hadap Bawah
                int[] xPoints = {arrowX, arrowX + 16, arrowX + 8}; 
                int[] yPoints = {arrowY, arrowY, arrowY + 12};     
                
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }
}

