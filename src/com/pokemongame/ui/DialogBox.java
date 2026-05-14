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
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.setColor(Color.WHITE);
        g2d.drawString(displayText, BOX_X + 16, boxY + 36);

        // Indikator "tekan Z untuk lanjut" saat teks selesai
        if (finished) {
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.setColor(new Color(200, 200, 200));
            g2d.drawString("▼ Z / Enter", BOX_X + boxWidth - 90, boxY + BOX_HEIGHT - 12);
        }
    }
}

