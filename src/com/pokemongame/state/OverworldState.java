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
import com.pokemongame.util.SaveManager;
import com.pokemongame.world.Camera;
import com.pokemongame.world.TileMap;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

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
        gamePanel.playMusic("res/sound/overworld.wav");
        setupNPCs();
    }
    
    @Override
    public void update() {
        if (isTransitioning) {
            transitionCounter++;
            if (transitionCounter >= MAX_TRANSITION_TIME) {
                isTransitioning = false;
                BattleState battle = new BattleState(gamePanel, pendingPlayerActive, pendingWildPokemon, this);
                gamePanel.setCurrentState(battle);
            }
            return;
        }

        if (player != null) {
            player.update();
            camera.update(player);
            checkWildEncounter();
            
            // 1. FITUR SAVE GAME OTOMATIS (Jika player menekan tombol Back/Escape)
            if (keyHandler.backPressed) {
                keyHandler.backPressed = false;
                SaveManager.saveGame(player);
                System.out.println("Game koordinat berhasil disimpan ke Database!");
            }
            
            // 2. FITUR DIALOG NPC (Jika menekan tombol Action dekat NPC)
            if (keyHandler.actionPressed) {
                keyHandler.actionPressed = false;
                checkNPCDialogInteraction();
            }
        }
    }
    
    private void checkNPCDialogInteraction() {
        for (NPC npc : npcs) {
            if (npc != null) {
                // Hitung jarak matematika absolut antara koordinat Player dan NPC
                int diffX = Math.abs(player.worldX - npc.worldX);
                int diffY = Math.abs(player.worldY - npc.worldY);
                
                // Jika jarak dekat (radius 1 ubin / TILE_SIZE)
                if (diffX <= GamePanel.TILE_SIZE && diffY <= GamePanel.TILE_SIZE) {
                    // Picu fungsi bicara milik NPC kamu
                    npc.speak(); 
                    break;
                }
            }
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

        // 2. SISTEM ENCOUNTER DINAMIS: Ambil ID Pokemon liar acak dari Database
        int randomId = SaveManager.getRandomPokemonId();
        
        // Bikin level musuhnya bervariasi (misal Level 2 sampai 5)
        int randomLevel = 2 + (int)(Math.random() * 4); 
        
        // Panggil fungsi andalanmu untuk memuat Pokemon lengkap dengan jurusnya!
        pendingWildPokemon = SaveManager.loadPokemonById(randomId, randomLevel);

        // 3. Ambil Pokemon milik Player
        java.util.List<Pokemon> party = SaveManager.loadPlayerParty();
        if (!party.isEmpty()) {
            pendingPlayerActive = null;
            // Cari pokemon di tim yang HP-nya belum 0 untuk maju duluan
            for (Pokemon p : party) {
                if (p.getCurrentHp() > 0) {
                    pendingPlayerActive = p;
                    break;
                }
            }
            // Kalau amit-amit mati semua, paksa panggil yang pertama (nanti otomatis kalah di BattleState)
            if (pendingPlayerActive == null) {
                pendingPlayerActive = party.get(0);
            }
        } else {
            // JIKA DB PLAYER KOSONG: Paksa muat Pikachu (ID 25) sebagai cadangan
            pendingPlayerActive = SaveManager.loadPokemonById(25, 5); 
            if(pendingPlayerActive == null){
                 // Kalau Pikachu nggak ada di db, pakai Snivy
                 pendingPlayerActive = SaveManager.loadPokemonById(495, 5);
            }
        }

        // Mulai efek layar kedap-kedip transisi
        isTransitioning = true;
        transitionCounter = 0;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void render(Graphics2D g2d) {
        // 1. Render Map Background (Tanah, rumput dasar, batang pohon bawah)
        tileMap.renderBackground(g2d, camera.x, camera.y);
        
        // 2. Render Player (Karakter berjalan di atas background)
        if (player != null) player.render(g2d);
        
        // 3. Render Map Foreground (Daun rimbun/pucuk pohon otomatis menimpa kepala player)
        tileMap.renderForeground(g2d, camera.x, camera.y);

        // 4. Efek Transisi Masuk Battle (Blinking Flash & Fade Out)
        if (isTransitioning) {
            // --- LOGIKA PULSA AMAN (0 - 2.5 Detik / Frame 0-150) ---
            if (transitionCounter < 150) {
                // Kita gunakan fungsi Sinus untuk membuat efek "napas" atau pulsa yang halus
                double speed = (transitionCounter < 60) ? 0.1 : 0.3; 
                float alpha = (float) (Math.abs(Math.sin(transitionCounter * speed)) * 0.5f); 

                // Batasi alpha maksimal 0.7f biar nggak terlalu gelap mendadak
                if (alpha > 0.7f) alpha = 0.7f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // Efek getar layar
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

            // =========================================================================
            // --- TAMBAHAN: TAMPILKAN KOTAK DIALOG NOTIFIKASI DI LAYAR OVERWORLD ---
            // =========================================================================
            int bottomY = GamePanel.SCREEN_HEIGHT - 130;
            int boxX = 20;
            int boxW = GamePanel.SCREEN_WIDTH - 40;

            // 1. Gambar Kotak Dialog Hitam Transparan (Sama persis seperti gaya BattleState)
            g2d.setColor(new Color(25, 25, 25, 240)); // Hitam pekat tapi agak transparan dikit
            g2d.fillRoundRect(boxX, bottomY, boxW, 110, 15, 15);
            
            // 2. Gambar Border Putih di pinggir kotak
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new java.awt.BasicStroke(3));
            g2d.drawRoundRect(boxX, bottomY, boxW, 110, 15, 15);

            // 3. Gambar Teks Pengumuman di dalam kotak
            g2d.setFont(new Font("Monospaced", Font.BOLD, 18)); // Pakai font tebal bawaan Java biar aman
            g2d.drawString("A wild Pokémon appeared...!", boxX + 40, bottomY + 65);
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
