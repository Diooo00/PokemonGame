/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.main;

import com.pokemongame.input.KeyHandler;
import com.pokemongame.state.GameState;
import com.pokemongame.world.TileMap;
import com.pokemongame.state.OverworldState;


import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * 
 *
 * @author thety
 */
public class GamePanel extends JPanel {

    // Ukuran tile & layar
    public static final int TILE_SIZE = 32;
    public static final int SCREEN_COLS = 20;
    public static final int SCREEN_ROWS = 16;
    public static final int SCREEN_WIDTH = TILE_SIZE * SCREEN_COLS;   // 640px
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;  // 512px

    private GameLoop gameLoop;
    private KeyHandler keyHandler;
    private TileMap tileMap;
    
    // State aktif saat ini
    private GameState currentState;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        keyHandler = new KeyHandler();
        addKeyListener(keyHandler);
        setFocusable(true);
        requestFocusInWindow();

        gameLoop = new GameLoop(this);
        tileMap = new TileMap(this);
        currentState = new OverworldState(this);
    }

    public TileMap getTileMap() {
        return tileMap;
    }
    
    public void startGameLoop() {
        gameLoop.start();
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (currentState != null) {
            currentState.render(g2d);
        }

        g2d.dispose();
    }
}
