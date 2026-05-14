/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.ui;

import com.pokemongame.main.GamePanel;
import com.pokemongame.pokemon.Pokemon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints; 
/**
 *
 * @author thety
 */
public class HUD {
      // Render HP bar + nama Pokemon di battle
    public void renderPokemonHUD(Graphics2D g2d,
                                  Pokemon pokemon,
                                  int x, int y,
                                  boolean isPlayer) {

        int boxW = 200, boxH = 60;

        // Background HUD
        g2d.setColor(new Color(30, 30, 30, 220));
        g2d.fillRoundRect(x, y, boxW, boxH, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x, y, boxW, boxH, 10, 10);

        // Nama & level
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.setColor(Color.WHITE);
        g2d.drawString(pokemon.getName(), x + 10, y + 20);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawString("Lv." + pokemon.getLevel(), x + boxW - 40, y + 20);

        // HP label
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(new Color(150, 255, 150));
        g2d.drawString("HP", x + 10, y + 40);

        // HP bar background
        int barX = x + 30, barY = y + 32;
        int barW = 150, barH = 10;
        g2d.setColor(new Color(80, 80, 80));
        g2d.fillRoundRect(barX, barY, barW, barH, 5, 5);

        // HP bar fill
        double hpRatio = (double) pokemon.getCurrentHp() / pokemon.getMaxHp();
        int fillW = (int)(barW * hpRatio);
        Color barColor = hpRatio > 0.5 ? new Color(50, 200, 50)
                       : hpRatio > 0.25 ? new Color(255, 200, 0)
                       : new Color(220, 50, 50);
        g2d.setColor(barColor);
        g2d.fillRoundRect(barX, barY, fillW, barH, 5, 5);

        // HP angka (hanya untuk player)
        if (isPlayer) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.WHITE);
            g2d.drawString(pokemon.getCurrentHp() + "/" + pokemon.getMaxHp(),
                           barX + barW - 45, barY + barH + 12);
        }
    }

    // Render menu aksi battle (Fight / Run)
    public void renderBattleMenu(Graphics2D g2d, String[] options, int selectedIndex) {
        int menuX = GamePanel.SCREEN_WIDTH  - 220;
        int menuY = GamePanel.SCREEN_HEIGHT - 110;
        int menuW = 200, menuH = 90;

        g2d.setColor(new Color(30, 30, 30, 220));
        g2d.fillRoundRect(menuX, menuY, menuW, menuH, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(menuX, menuY, menuW, menuH, 10, 10);

        g2d.setFont(new Font("Arial", Font.PLAIN, 13));

        for (int i = 0; i < options.length; i++) {
            int optX = menuX + 20 + (i % 2) * 90;
            int optY = menuY + 30 + (i / 2) * 30;

            if (i == selectedIndex) {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("▶ " + options[i], optX - 14, optY);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString(options[i], optX, optY);
            }
        }
    }
}
