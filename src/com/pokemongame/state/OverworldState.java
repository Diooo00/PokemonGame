/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

import com.pokemongame.entity.NPC;
import com.pokemongame.entity.Player;
import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.world.Camera;
import com.pokemongame.world.TileMap;

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

    public OverworldState(GamePanel gamePanel) {
        super(gamePanel);
        tileMap   = gamePanel.getTileMap();
        keyHandler = gamePanel.getKeyHandler();
        player    = new Player(gamePanel, keyHandler);        
        setupNPCs();
    }
    
    @Override
    public void update() {
        player.update();
        camera.update(player);
        checkWildEncounter();
    }

    private void checkWildEncounter() {
        // 1. Pastikan nama variabel konsisten
        int playerCol = player.worldX / GamePanel.TILE_SIZE;
        int playerRow = player.worldY / GamePanel.TILE_SIZE;

        // 2. Gunakan nama yang sama di sini
        if (tileMap.isTallGrass(playerRow, playerCol)) {

            // 3. Pastikan method isMoving() sudah ditambahkan ke KeyHandler
            if (keyHandler.isMoving()) { 
                if (Math.random() < 0.005) {
                    System.out.println("A wild Pokemon appears!");

                    // Nanti di sini kita buat transisi ke BattleState
                    // triggerBattle(); 
                }
            }
        }
    }

    private void triggerBattleState() {
        // Chat 4 nanti — BattleState di-set di sini
        System.out.println("Wild Pokemon appeared!");
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void render(Graphics2D g2d) {
        tileMap.render(g2d, camera.x, camera.y);
        player.render(g2d);
    }

    private void setupNPCs() {
        String[] dialog = {"Halo!", "Selamat datang di dunia Pokemon.", "Semoga harimu menyenangkan!"};
        npcs[0] = new NPC(gamePanel, GamePanel.TILE_SIZE * 15, GamePanel.TILE_SIZE * 15, dialog);
    }
    
    public NPC[] getNPCs() {
        return npcs;
    }
}
