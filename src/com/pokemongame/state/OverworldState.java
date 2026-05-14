/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

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

    public OverworldState(GamePanel gamePanel) {
        super(gamePanel);

        tileMap   = gamePanel.getTileMap();
        keyHandler = gamePanel.getKeyHandler();
        player    = new Player(gamePanel, keyHandler);
        camera    = new Camera(gamePanel);
    }

    @Override
    public void update() {
        player.update();
        camera.update(player);
        checkWildEncounter();
    }

    private void checkWildEncounter() {
        // Cek apakah player berdiri di tall grass
        int playerTileCol = player.worldX / GamePanel.TILE_SIZE;
        int playerTileRow = player.worldY / GamePanel.TILE_SIZE;

        if (tileMap.isTallGrass(playerTileRow, playerTileCol)) {
            // 1% chance per update = sekitar 1 encounter per 1-2 detik di rumput
            if (Math.random() < 0.01) {
                triggerBattleState();
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
        player.render(g2d, camera.x, camera.y);
    }
}
