/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;


import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.util.SaveManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
/**
 *
 * @author user
 */
public class ShopState extends GameState {
    private KeyHandler keyHandler;
    private GameState previousState;
    private Font font;

    // --- DATA BARANG DAGANGAN ---
    private String[] itemNames = {"Poke Ball", "Great Ball", "Ultra Ball", "Potion", "Super Potion"};
    private int[] itemPrices = {200, 600, 1200, 300, 700};
    private int selectedItem = 0;
    
    private String shopMessage = "Welcome to Poké Mart! What do you need?";
    private int messageTimer = 0;

    public ShopState(GamePanel gp, GameState previousState) {
        super(gp);
        this.previousState = previousState;
        this.keyHandler = gp.getKeyHandler();
        
        // Coba load font Pokemon, kalau gagal pakai font default
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("res/font/PKMN RBYGSC.ttf")).deriveFont(18f);
        } catch (Exception e) {
            this.font = new Font("Monospaced", Font.BOLD, 18);
        }
        
        // Reset input biar kursor nggak lari pas baru masuk
        keyHandler.upPressed = false;
        keyHandler.downPressed = false;
        keyHandler.actionPressed = false;
    }

    @Override
    public void update() {
        // Kalau lagi nampilin pesan beli/gagal, tunggu sampai timernya habis
        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) shopMessage = "Anything else?";
            return;
        }

        // Navigasi Atas Bawah
        if (keyHandler.upPressed) {
            keyHandler.upPressed = false;
            if (selectedItem > 0) selectedItem--;
        }
        if (keyHandler.downPressed) {
            keyHandler.downPressed = false;
            if (selectedItem < itemNames.length - 1) selectedItem++;
        }

        // Tombol Beli (Action)
        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            buyItem();
        }

        // Tombol Keluar (Back)
        if (keyHandler.backPressed) {
            keyHandler.backPressed = false;
            gamePanel.setCurrentState(previousState); // Balik ke Overworld
        }
    }

    private void buyItem() {
        int price = itemPrices[selectedItem];
        
        if (SaveManager.playerMoney >= price) {
            SaveManager.playerMoney -= price; // Duit dikurangi!
            shopMessage = "Bought 1 " + itemNames[selectedItem] + "!";
            
            // --- BARANG RESMI MASUK TAS / DATABASE! ---
            SaveManager.addItem(itemNames[selectedItem], 1);
            
        } else {
            shopMessage = "You don't have enough money!"; 
        }
        messageTimer = 90; 
    }

    @Override
    public void render(Graphics2D g2d) {
        // 1. Background transparan biar map overworld masih kelihatan samar
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        g2d.setFont(font);

        // 2. KOTAK UANG (Kiri Atas)
        g2d.setColor(new Color(30, 30, 30, 240));
        g2d.fillRoundRect(20, 20, 240, 60, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.drawRoundRect(20, 20, 240, 60, 15, 15);
        g2d.drawString("MONEY: $" + SaveManager.playerMoney, 40, 55);

        // 3. KOTAK TOKO & DAFTAR BARANG (Kanan)
        int shopW = 380;
        int shopH = 340;
        int shopX = GamePanel.SCREEN_WIDTH - shopW - 20;
        int shopY = 20;
        
        g2d.setColor(new Color(30, 30, 30, 240));
        g2d.fillRoundRect(shopX, shopY, shopW, shopH, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(shopX, shopY, shopW, shopH, 15, 15);
        
        g2d.drawString("POKÉ MART", shopX + 130, shopY + 40);
        g2d.drawLine(shopX + 20, shopY + 50, shopX + shopW - 20, shopY + 50);

        int itemY = shopY + 90;
        for (int i = 0; i < itemNames.length; i++) {
            if (i == selectedItem) {
                g2d.setColor(Color.YELLOW);
                
                // Gambar Kursor Segitiga
                int[] xPoints = {shopX + 20, shopX + 30, shopX + 20};
                int[] yPoints = {itemY - 11, itemY - 6, itemY - 1}; 
                g2d.fillPolygon(xPoints, yPoints, 3);
            } else {
                g2d.setColor(Color.WHITE);
            }
            g2d.drawString(itemNames[i].toUpperCase(), shopX + 40, itemY);
            g2d.drawString("$" + itemPrices[i], shopX + 280, itemY);
            itemY += 40;
        }

        // 4. KOTAK PESAN / DIALOG (Bawah)
        int msgY = GamePanel.SCREEN_HEIGHT - 130;
        g2d.setColor(new Color(30, 30, 30, 240));
        g2d.fillRoundRect(20, msgY, GamePanel.SCREEN_WIDTH - 40, 110, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(20, msgY, GamePanel.SCREEN_WIDTH - 40, 110, 15, 15);
        
        g2d.drawString(shopMessage, 50, msgY + 50);
        
        // Petunjuk tombol
        g2d.setFont(font.deriveFont(12f));
        g2d.setColor(Color.GRAY);
        g2d.drawString("[ACTION] Buy   [BACK] Exit", 50, msgY + 90);
    }
}