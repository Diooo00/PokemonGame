/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.ui;

import com.pokemongame.item.Item;
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
        if (hpRatio < 0) hpRatio = 0;
        int fillW = (int)(barW * hpRatio);
        
        if (hpRatio > 0.5) g2d.setColor(new Color(65, 225, 65));
        else if (hpRatio > 0.2) g2d.setColor(Color.YELLOW);
        else g2d.setColor(Color.RED);
        
        g2d.fillRect(barX, barY, fillW, barH);
    }

    public void renderBattleMenu(Graphics2D g2d, String[] options, int selected, Font font) {
        if (font != null) g2d.setFont(font.deriveFont(18f));
        else g2d.setFont(new Font("Monospaced", Font.BOLD, 18));

        int width = 340, height = 110;
        int x = GamePanel.SCREEN_WIDTH - 20 - width; // Nempel ke margin kanan
        int y = GamePanel.SCREEN_HEIGHT - 130;
        drawWindow(g2d, x, y, width, height);

        for (int i = 0; i < options.length; i++) {
            int col = i % 2;
            int row = i / 2;
            int optX = x + 45 + (col * 140);
            int optY = y + 45 + (row * 40);

            if (i == selected) {
                drawCursor(g2d, optX - 25, optY - 5); 
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(options[i], optX, optY);
        }
    }

    public void renderMoveMenu(Graphics2D g2d, List<Move> moves, int selectedMove, Font font) {
        if (font != null) g2d.setFont(font.deriveFont(16f));
        else g2d.setFont(new Font("Monospaced", Font.BOLD, 16));

        // 1. SISTEM HALAMAN: Tampilkan maksimal 4 jurus per layar
        int startIdx = (selectedMove / 4) * 4;
        int endIdx = Math.min(startIdx + 4, moves.size());

        // 2. SISTEM RESPONSIF: Hitung teks paling panjang di halaman ini
        int maxTextWidth = 0;
        for (int i = startIdx; i < endIdx; i++) {
            int w = g2d.getFontMetrics().stringWidth(moves.get(i).getName().toUpperCase());
            if (w > maxTextWidth) maxTextWidth = w;
        }

        int colWidth = maxTextWidth + 40; // Spasi untuk kursor segitiga
        int width = Math.max(340, (colWidth * 2) + 40); // Minimal 340px, tapi bisa melar
        int x = GamePanel.SCREEN_WIDTH - 20 - width; 
        int y = GamePanel.SCREEN_HEIGHT - 130;
        int height = 110;

        drawWindow(g2d, x, y, width, height);

        for (int i = startIdx; i < endIdx; i++) {
            int displayIdx = i - startIdx;
            int col = displayIdx % 2;
            int row = displayIdx / 2;
            int moveX = x + 40 + (col * colWidth);
            int moveY = y + 45 + (row * 40);

            if (i == selectedMove) {
                drawCursor(g2d, moveX - 20, moveY - 5);
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(moves.get(i).getName().toUpperCase(), moveX, moveY);
        }
        
        // Render indikator panah jika halaman bisa di-scroll
        renderScrollIndicators(g2d, x, y, width, height, startIdx, endIdx, moves.size());
    }

     public void renderBagMenu(Graphics2D g2d, List<Item> inventory, int selectedItem, Font font) {
        // 1. TENTUKAN UKURAN KOTAK (Tinggi & Rapi)
        int width = 340;
        int height = 360; 
        
        // 2. POSISIKAN DI KANAN BAWAH
        int x = GamePanel.SCREEN_WIDTH - width - 20; 
        int y = GamePanel.SCREEN_HEIGHT - height - 20;

        // 3. GAMBAR KOTAK BACKGROUND TAS
        g2d.setColor(new Color(30, 30, 30, 240)); // Abu gelap transparan
        g2d.fillRoundRect(x, y, width, height, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.drawRoundRect(x, y, width, height, 15, 15);

        // Judul Menu Tas
        g2d.setFont(font.deriveFont(18f));
        g2d.drawString("BAG", x + 130, y + 40);
        g2d.drawLine(x + 20, y + 50, x + width - 20, y + 50); // Garis bawah judul

        g2d.setFont(font.deriveFont(14f));

        // 4. LOGIKA SCROLLING (Nampilin 6 item)
        int maxVisible = 6;
        int startIndex = 0;
        if (selectedItem >= maxVisible) {
            startIndex = selectedItem - maxVisible + 1;
        }

        // 5. GAMBAR ITEM-ITEMNYA
        int drawCount = 0;
        int itemY = y + 90; // Jarak item pertama dari atas

        for (int i = startIndex; i < inventory.size(); i++) {
            if (drawCount >= maxVisible) break;

            Item item = inventory.get(i);

            // --- PERBAIKAN POINTER: SEGITIGA SOLID ALA PANEL UTAMA ---
            if (i == selectedItem) {
                g2d.setColor(Color.YELLOW); // Warna Kuning pas dipilih

                // A. Hitung Koordinat Segitiga Lancip Kanan
                // Kita buat tinggi segitiga ~10px agar pas sama tinggi teks 14pt
                int pointerX = x + 20; // Posisi kiri segitiga
                
                // Koordinat X: {Kiri, Kiri, Kanan/Lancip}
                int[] xPoints = {pointerX, pointerX, pointerX + 10};
                // Koordinat Y: {Atas, Bawah, Tengah}. Disesuaikan agar presisi vertikal sama teks.
                int[] yPoints = {itemY - 11, itemY - 1, itemY - 6}; 

                g2d.fillPolygon(xPoints, yPoints, 3); // Gambar Segitiga Solid

                // B. Gambar Teks Item (Dikasih jarak/indent 35px biar ga ditabrak segitiga)
                g2d.drawString(item.getName().toUpperCase(), x + 35, itemY);
            } else {
                g2d.setColor(Color.WHITE);
                // Gambar Teks Item Normal (Tetep indent 35px biar lurus)
                g2d.drawString(item.getName().toUpperCase(), x + 35, itemY);
            }

            // Gambar jumlah item (x5, x10)
            g2d.drawString("x" + item.getQuantity(), x + width - 60, itemY);

            itemY += 40; // Jarak vertikal ke item berikutnya
            drawCount++;
        }
    }

    public void renderPartyMenu(Graphics2D g2d, List<Pokemon> party, int selected, Font font) {
        // Background transparan
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // Kotak gede
        int boxX = 50, boxY = 50, boxW = GamePanel.SCREEN_WIDTH - 100, boxH = GamePanel.SCREEN_HEIGHT - 100;
        drawWindow(g2d, boxX, boxY, boxW, boxH);

        if (font != null) g2d.setFont(font.deriveFont(24f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("--- POKEMON STORAGE ---", boxX + 30, boxY + 45);

        // --- SISTEM SLIDER (9 BARIS x 3 KOLOM) ---
        int maxRows = 9; 
        int maxCols = 3;
        int itemsPerPage = maxRows * maxCols; // 27 Pokemon per halaman
        
        int currentPage = selected / itemsPerPage; 
        int startIdx = currentPage * itemsPerPage;
        int endIdx = Math.min(startIdx + itemsPerPage, party.size());

        // Pengaturan Jarak
        int colWidth = 550; // Jarak horizontal tetap
        int rowHeight = 90; // Dirapetin dikit biar baris ke-9 nggak mentok bawah

        for (int i = startIdx; i < endIdx; i++) {
            int displayIdx = i - startIdx; 
            int col = displayIdx / maxRows; // Nentuin kolom 0, 1, 2
            int row = displayIdx % maxRows; // Nentuin baris 0 sampai 8
            
            int xPos = boxX + 60 + (col * colWidth);
            int yPos = boxY + 105 + (row * rowHeight); // Y start dinaikin dikit biar lega

            // 1. Gambar Kursor
            if (i == selected) {
                drawCursor(g2d, xPos - 25, yPos - 5);
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.WHITE);
            }
            
            // 2. Teks Nama & Level
            if (font != null) g2d.setFont(font.deriveFont(18f));
            g2d.drawString(party.get(i).getName().toUpperCase() + "  Lv" + party.get(i).getLevel(), xPos, yPos);
            
            // 3. Teks HP
            if (font != null) g2d.setFont(font.deriveFont(12f));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString("HP: " + party.get(i).getCurrentHp() + "/" + party.get(i).getMaxHp(), xPos, yPos + 22);
            
            // 4. Bar HP Mini
            int barW = 120, barH = 8;
            g2d.setColor(new Color(60, 60, 60)); 
            g2d.fillRect(xPos, yPos + 30, barW, barH);
            
            double hpRatio = (double) party.get(i).getCurrentHp() / party.get(i).getMaxHp();
            if (hpRatio < 0) hpRatio = 0;
            int fillW = (int)(barW * hpRatio);
            
            if (hpRatio > 0.5) g2d.setColor(new Color(65, 225, 65)); 
            else if (hpRatio > 0.2) g2d.setColor(Color.YELLOW);      
            else g2d.setColor(Color.RED);                            
            
            g2d.fillRect(xPos, yPos + 30, fillW, barH);
        }

        // --- TOMBOL KEMBALI ---
        if (font != null) g2d.setFont(font.deriveFont(16f));
        g2d.setColor(Color.WHITE);
        g2d.drawString("[X] BACK", boxX + 30, boxY + boxH - 25);
        
        // --- INDIKATOR HALAMAN ---
        int totalPages = (party.size() > 0) ? ((party.size() - 1) / itemsPerPage) + 1 : 1;
        g2d.drawString("PAGE " + (currentPage + 1) + " / " + totalPages, boxX + boxW - 160, boxY + boxH - 25);
    }
    
    // --- METHOD BANTUAN UNTUK GAMBAR PANAH SCROLL ---
    private void renderScrollIndicators(Graphics2D g2d, int x, int y, int width, int height, int startIdx, int endIdx, int totalSize) {
        g2d.setColor(Color.YELLOW);
        // Kalau masih ada item di halaman bawah
        if (totalSize > endIdx) {
            int[] xPoints = {x + width - 25, x + width - 15, x + width - 20};
            int[] yPoints = {y + height - 20, y + height - 20, y + height - 10}; 
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        // Kalau masih ada item di halaman atas
        if (startIdx > 0) {
            int[] xPoints = {x + width - 25, x + width - 15, x + width - 20};
            int[] yPoints = {y + 20, y + 20, y + 10}; 
            g2d.fillPolygon(xPoints, yPoints, 3);
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
    
    public void renderDialogBox(Graphics2D g2d, String message1, String message2, Font font) {
        // Bikin kotak dialog gede di bawah layar (nutupin menu)
        int x = 20;
        int y = GamePanel.SCREEN_HEIGHT - 130;
        int width = GamePanel.SCREEN_WIDTH - 40;
        int height = 110;
        
        drawWindow(g2d, x, y, width, height); // Panggil fungsi drawWindow milikmu

        // Set font buat teks
        if (font != null) g2d.setFont(font.deriveFont(18f));
        else g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        
        g2d.setColor(Color.WHITE);

        // Nggambar Baris 1: Nama Jurus (Pasti ada)
        if (message1 != null && !message1.isEmpty()) {
            g2d.drawString(message1, x + 30, y + 45);
        }
        
        // Nggambar Baris 2: Super Effective / Not Very Effective (Kalau ada isinya)
        if (message2 != null && !message2.isEmpty()) {
            g2d.drawString(message2, x + 30, y + 80); // y + 80 biar turun ke baris bawah
        }
    }
}