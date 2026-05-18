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
            
            if (keyHandler.backPressed) {
                keyHandler.backPressed = false;
                SaveManager.saveGame(player);
                System.out.println("Game koordinat berhasil disimpan ke Database!");
            }
            
            if (keyHandler.actionPressed) {
                keyHandler.actionPressed = false;
                checkNPCDialogInteraction();
            }
        }
    }
    
    private void checkNPCDialogInteraction() {
        for (NPC npc : npcs) {
            if (npc != null) {
                int diffX = Math.abs(player.worldX - npc.worldX);
                int diffY = Math.abs(player.worldY - npc.worldY);
                
                if (diffX <= GamePanel.TILE_SIZE && diffY <= GamePanel.TILE_SIZE) {
                    npc.speak(); 
                    break;
                }
            }
        }
    }

    private void checkWildEncounter() {
        int playerCol = (player.worldX + player.hitbox.x) / GamePanel.TILE_SIZE;
        int playerRow = (player.worldY + player.hitbox.y) / GamePanel.TILE_SIZE;

        if (tileMap.isTallGrass(playerRow, playerCol)) {
            if (keyHandler.isMoving()) {
                if (Math.random() < 0.008) {
                    System.out.println("A wild Pokemon appears!");
                    triggerBattle();
                }
            }
        }
    }

    private void triggerBattle() {
        keyHandler.upPressed = false;
        keyHandler.downPressed = false;
        keyHandler.leftPressed = false;
        keyHandler.rightPressed = false;

        int randomId = SaveManager.getRandomPokemonId();
        int randomLevel = 2 + (int)(Math.random() * 4); 
        
        pendingWildPokemon = SaveManager.loadPokemonById(randomId, randomLevel);

        java.util.List<Pokemon> party = SaveManager.loadPlayerParty();
        if (!party.isEmpty()) {
            pendingPlayerActive = null;
            for (Pokemon p : party) {
                if (p.getCurrentHp() > 0) {
                    pendingPlayerActive = p;
                    break;
                }
            }
            if (pendingPlayerActive == null) {
                pendingPlayerActive = party.get(0);
            }
        } else {
            pendingPlayerActive = SaveManager.loadPokemonById(25, 5); 
            if(pendingPlayerActive == null){
                 pendingPlayerActive = SaveManager.loadPokemonById(495, 5);
            }
        }

        isTransitioning = true;
        transitionCounter = 0;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void render(Graphics2D g2d) {
        // 1. Render Map Background (Tanah, rumput dasar, air, batang pohon)
        tileMap.renderBackground(g2d, camera.x, camera.y);
        
        // --- TAMBAHAN FIX: Render NPC biar gak gaib ---
        for (NPC npc : npcs) {
            if (npc != null) {
                npc.render(g2d); 
            }
        }
        
        // 2. Render Player (Karakter berjalan di atas background)
        if (player != null) player.render(g2d);
        
        // 3. Render Map Foreground (Daun rimbun otomatis menimpa kepala player)
        tileMap.renderForeground(g2d, camera.x, camera.y);

        // 4. Efek Transisi Masuk Battle 
        if (isTransitioning) {
            if (transitionCounter < 150) {
                double speed = (transitionCounter < 60) ? 0.1 : 0.3; 
                float alpha = (float) (Math.abs(Math.sin(transitionCounter * speed)) * 0.5f); 
                if (alpha > 0.7f) alpha = 0.7f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                applyScreenShake(g2d, (transitionCounter < 60) ? 2 : 5);
            } 
            else {
                float finalAlpha = (float)(transitionCounter - 150) / 30f;
                if (finalAlpha > 1f) finalAlpha = 1f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, finalAlpha));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            int bottomY = GamePanel.SCREEN_HEIGHT - 130;
            int boxX = 20;
            int boxW = GamePanel.SCREEN_WIDTH - 40;

            g2d.setColor(new Color(25, 25, 25, 240)); 
            g2d.fillRoundRect(boxX, bottomY, boxW, 110, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new java.awt.BasicStroke(3));
            g2d.drawRoundRect(boxX, bottomY, boxW, 110, 15, 15);

            g2d.setFont(new Font("Monospaced", Font.BOLD, 18)); 
            g2d.drawString("A wild Pokémon appeared...!", boxX + 40, bottomY + 65);
        }
    }

    private void applyScreenShake(Graphics2D g2d, int intensity) {
        int offsetX = (int)(Math.random() * intensity) - (intensity/2);
        int offsetY = (int)(Math.random() * intensity) - (intensity/2);
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new java.awt.BasicStroke(intensity));
        g2d.drawRect(offsetX, offsetY, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
    }

    private void setupNPCs() {
        String[] dialog = {"Halo!", "Selamat datang di dunia Pokemon.", "Semoga harimu menyenangkan!"};
        npcs[0] = new NPC(gamePanel, GamePanel.TILE_SIZE * 15, GamePanel.TILE_SIZE * 15, dialog);
    }
    
    public NPC[] getNPCs() {
        return npcs;
    }
}