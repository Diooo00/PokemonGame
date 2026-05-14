/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

import com.pokemongame.entity.NPC;
import com.pokemongame.entity.Player;
import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.pokemon.Pokemon;
import com.pokemongame.world.Camera;
import com.pokemongame.world.TileMap;
import java.awt.AlphaComposite;
import java.awt.Color;

import java.awt.Graphics2D;

/**
 *
 * @author thety
 */
public class OverworldState extends GameState {

    private Player player;
    private Camera camera;
    private TileMap tileMap;
    private KeyHandler keyHandler;
    private NPC[] npcs = new NPC[10];
    
    private boolean isTransitioning = false;
    private int transitionCounter = 0;
    private final int MAX_TRANSITION_TIME = 180; // 1 detik (60 frame)
    private Pokemon pendingWildPokemon;
    private Pokemon pendingPlayerActive;

    public OverworldState(GamePanel gamePanel) {
        super(gamePanel);
        tileMap   = gamePanel.getTileMap();
        keyHandler = gamePanel.getKeyHandler();
        player    = new Player(gamePanel, keyHandler);     
        this.camera = gamePanel.getCamera();
        setupNPCs();
    }
    
    @Override
    public void update() {
        if (isTransitioning) {
            transitionCounter++;

            // JIKA SUDAH 5 DETIK (300 Frame), BARU PINDAH!
            if (transitionCounter >= MAX_TRANSITION_TIME) {
                isTransitioning = false; // Reset saklar

                // PINDAH KE BATTLE SEKARANG
                BattleState battle = new BattleState(gamePanel, pendingPlayerActive, pendingWildPokemon, this);
                gamePanel.setCurrentState(battle);
            }
            return; // Selama transisi, jangan jalanin logika gerak player di bawah
        }

        // Logika normal Overworld
        if (player != null) {
            player.update();
            camera.update(player);
            checkWildEncounter();
        }
    }

    private void checkWildEncounter() {
        // 1. Dapatkan posisi grid player
        int playerCol = (player.worldX + player.hitbox.x) / GamePanel.TILE_SIZE;
        int playerRow = (player.worldY + player.hitbox.y) / GamePanel.TILE_SIZE;

        // 2. Cek apakah ubin tersebut adalah Rumput Tinggi (ID 2)
        if (tileMap.isTallGrass(playerRow, playerCol)) {

            // 3. Hanya cek encounter jika player sedang bergerak (biar nggak spawn kalau diem)
            if (keyHandler.isMoving()) {

                // 4. Kocok dadu (0.008 = sekitar 0.8% peluang per frame saat jalan)
                // Kamu bisa naikkan ke 0.01 kalau mau lebih sering ketemu
                if (Math.random() < 0.008) {
                    System.out.println("A wild Pokemon appears!");
                    triggerBattle();
                }
            }
        }
    }

    private void triggerBattle() {
        // 1. Matikan input biar player diem
        keyHandler.upPressed = false;
        keyHandler.downPressed = false;
        keyHandler.leftPressed = false;
        keyHandler.rightPressed = false;

        // 2. Siapkan data Pokemon yang mau diadu (simpan di variabel class)
        int[] ids = {1, 4, 7};
        int randomId = ids[(int)(Math.random() * ids.length)];
        pendingWildPokemon = Pokemon.loadFromDB(randomId, 3);
        pendingPlayerActive = Pokemon.loadFromDB(4, 5); 

        // 3. MULAI ANIMASI (Jangan pindah state dulu!)
        this.isTransitioning = true;
        this.transitionCounter = 0;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void render(Graphics2D g2d) {
        // 1. Render Map & Player
        tileMap.render(g2d, camera.x, camera.y);
        if (player != null) player.render(g2d);

        if (isTransitioning) {
            // --- LOGIKA PULSA AMAN (0 - 2.5 Detik / Frame 0-150) ---
            if (transitionCounter < 150) {
                // Kita gunakan fungsi Sinus untuk membuat efek "napas" atau pulsa yang halus
                // Math.sin bakal menghasilkan gelombang naik turun yang smooth
                double speed = (transitionCounter < 60) ? 0.1 : 0.3; // Makin lama makin cepet dikit
                float alpha = (float) (Math.abs(Math.sin(transitionCounter * speed)) * 0.5f); 

                // Batasi alpha maksimal 0.7f biar nggak terlalu gelap mendadak
                if (alpha > 0.7f) alpha = 0.7f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // Efek getar tipis saja (opsional)
                applyScreenShake(g2d, (transitionCounter < 60) ? 2 : 5);
            } 

            // --- FASE FINAL: SMOOTH FADE OUT (0.5 Detik terakhir / Frame 150-180) ---
            else {
                float finalAlpha = (float)(transitionCounter - 150) / 30f;
                if (finalAlpha > 1f) finalAlpha = 1f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, finalAlpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
    }

    // Method helper (pastikan sudah ada)
    private void applyScreenShake(Graphics2D g2d, int intensity) {
        int offsetX = (int)(Math.random() * intensity) - (intensity/2);
        int offsetY = (int)(Math.random() * intensity) - (intensity/2);

        // Alih-alih nge-fill layar, kita gambar border hitam tipis yang gerak
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new java.awt.BasicStroke(intensity));
        g2d.drawRect(offsetX, offsetY, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    private void drawFlash(Graphics2D g2d, java.awt.Color color) {
        g2d.setColor(color);
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    private void setupNPCs() {
        String[] dialog = {"Halo!", "Selamat datang di dunia Pokemon.", "Semoga harimu menyenangkan!"};
        npcs[0] = new NPC(gamePanel, GamePanel.TILE_SIZE * 15, GamePanel.TILE_SIZE * 15, dialog);
    }
    
    public NPC[] getNPCs() {
        return npcs;
    }
}
