/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.ui;

import com.pokemongame.main.GamePanel;
import com.pokemongame.pokemon.Move;
import com.pokemongame.pokemon.Pokemon;
import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints; 
import java.util.List;
/**
 *
 * @author thety
 */
public class HUD {

    // Render HP bar + nama Pokemon di battle
    public void renderPokemonHUD(Graphics2D g2d, Pokemon pokemon, int x, int y, boolean isPlayer, Font font) {
        int boxW = 200, boxH = 60;
        drawWindow(g2d, x, y, boxW, boxH); // Pakai window standar biar rapi

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Set Font Nama
        if (font != null) g2d.setFont(font.deriveFont(14f));
        else g2d.setFont(new Font("Arial", Font.BOLD, 12));
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(pokemon.getName().toUpperCase(), x + 10, y + 22);

        // Set Font Level
        if (font != null) g2d.setFont(font.deriveFont(10f));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("Lv" + pokemon.getLevel(), x + boxW - 50, y + 22);

        // HP bar logic
        int barX = x + 35, barY = y + 35;
        int barW = 140, barH = 10;
        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(barX, barY, barW, barH);

        double hpRatio = (double) pokemon.getCurrentHp() / pokemon.getMaxHp();
        int fillW = (int)(barW * hpRatio);
        
        if (hpRatio > 0.5) g2d.setColor(new Color(65, 225, 65));
        else if (hpRatio > 0.2) g2d.setColor(Color.YELLOW);
        else g2d.setColor(Color.RED);
        
        g2d.fillRect(barX, barY, fillW, barH);
    }

    public void renderBattleMenu(Graphics2D g2d, String[] options, int selected, Font font) {
        int x = 440, y = GamePanel.SCREEN_HEIGHT - 130, width = 340, height = 110;
        drawWindow(g2d, x, y, width, height);

        if (font != null) g2d.setFont(font.deriveFont(18f));
        else g2d.setFont(new Font("Monospaced", Font.BOLD, 18));

        for (int i = 0; i < options.length; i++) {
            int col = i % 2;
            int row = i / 2;
            int optX = x + 45 + (col * 140);
            int optY = y + 45 + (row * 40);

            if (i == selected) {
                // Gambar segitiga, bukan teks ">"
                drawCursor(g2d, optX - 25, optY - 5); 
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            // Gambar teks tanpa embel-embel ">"
            g2d.drawString(options[i], optX, optY);
        }
    }

    public void renderMoveMenu(Graphics2D g2d, List<Move> moves, int selectedMove, Font font) {
        int x = 440, y = GamePanel.SCREEN_HEIGHT - 130, width = 340, height = 110;
        drawWindow(g2d, x, y, width, height);

        if (font != null) g2d.setFont(font.deriveFont(16f));
        else g2d.setFont(new Font("Monospaced", Font.BOLD, 16));

        for (int i = 0; i < moves.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            int moveX = x + 45 + (col * 150);
            int moveY = y + 45 + (row * 40);

            if (i == selectedMove) {
                drawCursor(g2d, moveX - 25, moveY - 5);
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(moves.get(i).getName(), moveX, moveY);
        }
    }

    public void renderPartyMenu(Graphics2D g2d, List<Pokemon> party, int selected, Font font) {
        // Gambar background overlay transparan biar fokus ke menu
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // Gambar kotak utama di tengah
        int boxX = 50, boxY = 50, boxW = GamePanel.SCREEN_WIDTH - 100, boxH = GamePanel.SCREEN_HEIGHT - 100;
        drawWindow(g2d, boxX, boxY, boxW, boxH);

        if (font != null) g2d.setFont(font.deriveFont(20f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("--- PARTY POKEMON ---", boxX + 30, boxY + 40);

        for (int i = 0; i < party.size(); i++) {
            int yPos = boxY + 80 + (i * 60);
            if (i == selected) {
                drawCursor(g2d, boxX + 20, yPos - 5);
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(party.get(i).getName().toUpperCase() + "  Lv" + party.get(i).getLevel(), boxX + 50, yPos);
            g2d.setFont(font.deriveFont(12f));
            g2d.drawString("HP: " + party.get(i).getCurrentHp() + "/" + party.get(i).getMaxHp(), boxX + 50, yPos + 20);
            if (font != null) g2d.setFont(font.deriveFont(20f)); // Balikin font size
        }

        g2d.setFont(font.deriveFont(14f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("[X] BACK", boxX + 30, boxY + boxH - 20);
    }
    
    // Ganti method renderBagMenu di HUD.java dengan ini:
    public void renderBagMenu(Graphics2D g2d, List<com.pokemongame.item.Item> items, int selected, Font font) {
        int x = 440, y = GamePanel.SCREEN_HEIGHT - 130, width = 340, height = 110;
        drawWindow(g2d, x, y, width, height);

        if (font != null) g2d.setFont(font.deriveFont(14f)); // Ukuran 14 agar muat teks panjang

        for (int i = 0; i < items.size(); i++) {
            int col = i % 2;
            int row = i / 2;
            int itemX = x + 45 + (col * 150);
            int itemY = y + 45 + (row * 40);

            if (i == selected) {
                drawCursor(g2d, itemX - 25, itemY - 5);
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }

            // Tampilkan Nama + Jumlah (Contoh: POTION x5)
            String text = items.get(i).getName().toUpperCase() + " x" + items.get(i).getQuantity();
            g2d.drawString(text, itemX, itemY);
        }
    }
    
    private void drawWindow(Graphics2D g2d, int x, int y, int w, int h) {
        g2d.setColor(new Color(30, 30, 30, 240));
        g2d.fillRoundRect(x, y, w, h, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.drawRoundRect(x, y, w, h, 15, 15);
    }
    
    private void drawCursor(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.YELLOW);
        int[] xPoints = {x, x + 10, x};      // Titik-titik X segitiga
        int[] yPoints = {y - 12, y - 6, y}; // Titik-titik Y segitiga (disesuaikan dengan tinggi font)
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}
