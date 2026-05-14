/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.main;

import com.pokemongame.input.KeyHandler;
import com.pokemongame.state.GameState;
import com.pokemongame.world.TileMap;
import com.pokemongame.state.OverworldState;
import com.pokemongame.util.CollisionChecker;
import com.pokemongame.world.Camera;


import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * 
 *
 * @author thety
 */
public class GamePanel extends JPanel {

    // Ukuran tile & layar
    public static final int ORIGINAL_TILE_SIZE = 32;
    public static final int SCALE = 2;                              // Turun dari 3 → 2
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; // 64px per tile
    public static final int SCREEN_COLS = 30;                       // Naik dari 20 → 30
    public static final int SCREEN_ROWS = 18;                       // Naik dari 12 → 18
    public static final int SCREEN_WIDTH  = TILE_SIZE * SCREEN_COLS;  // 1920px
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;  // 1152px

    private GameLoop gameLoop;
    private KeyHandler keyHandler;
    private TileMap tileMap;
    private Camera camera;
    public CollisionChecker cChecker = new CollisionChecker(this);
    
    // State aktif saat ini
    private GameState currentState;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        keyHandler = new KeyHandler(this);
        addKeyListener(keyHandler);
        setFocusable(true);
        requestFocusInWindow();

        gameLoop = new GameLoop(this);
        tileMap = new TileMap(this);
        cChecker = new CollisionChecker(this); // Inisialisasi ini penting
        this.camera = new Camera(this);
        currentState = new OverworldState(this);
    }

    public Camera getCamera() {
        return camera;
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
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (currentState != null) {
            currentState.render(g2d);
        }

        g2d.dispose();
    }

    public OverworldState getOverworldState() {
        if (currentState instanceof OverworldState) {
            return (OverworldState) currentState;
        }
        return null;
    }
}