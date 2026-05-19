package com.pokemongame.main;

import com.pokemongame.audio.AudioThread;
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
import java.awt.image.BufferedImage; // JANGAN LUPA IMPORT INI

public class GamePanel extends JPanel {

    public static final int ORIGINAL_TILE_SIZE = 32;
    public static final int SCALE = 2;                             
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE; 
    public static final int SCREEN_COLS = 30;                       
    public static final int SCREEN_ROWS = 18;                       
    public static final int SCREEN_WIDTH  = TILE_SIZE * SCREEN_COLS;  
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;  

    private GameLoop gameLoop;
    private KeyHandler keyHandler;
    private TileMap tileMap;
    private Camera camera;
    private AudioThread currentBGM;
    public CollisionChecker cChecker;
    
    private GameState currentState;

    // --- TAMBAHAN KANVAS GAIB (BUFFER) ---
    private BufferedImage screenBuffer;
    private Graphics2D g2Buffer;

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
        cChecker = new CollisionChecker(this); 
        this.camera = new Camera(this);
        
        // --- SETUP KANVAS GAIB ---
        screenBuffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2Buffer = (Graphics2D) screenBuffer.getGraphics();

        currentState = new OverworldState(this);
    }

    public Camera getCamera() { return camera; }
    public TileMap getTileMap() { return tileMap; }
    public void startGameLoop() { gameLoop.start(); }
    public void setCurrentState(GameState state) { this.currentState = state; }
    public GameState getCurrentState() { return currentState; }
    public KeyHandler getKeyHandler() { return keyHandler; }

    public void update() {
        if (currentState != null) {
            currentState.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Bersihkan kanvas gaib (Pakai warna hijau rumput biar aman)
        g2Buffer.setColor(new Color(34, 139, 34)); 
        g2Buffer.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 2. Gambar SEMUANYA ke Kanvas Gaib (Ukuran Normal)
        if (currentState != null) {
            currentState.render(g2Buffer);
        }

        // 3. Render ke layar komputer
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        double windowWidth = this.getWidth();
        double windowHeight = this.getHeight();
        
        // --- JURUS ANTI STUTTERING: Kunci skala terkecil biar gambar proporsional ---
        double scale = Math.min(windowWidth / (double) SCREEN_WIDTH, windowHeight / (double) SCREEN_HEIGHT);

        // Hitung ukuran akhir setelah di-scale seragam
        int scaledWidth = (int) (SCREEN_WIDTH * scale);
        int scaledHeight = (int) (SCREEN_HEIGHT * scale);
        
        // Hitung koordinat X dan Y biar posisinya selalu di TENGAH layar (Letterboxing)
        int xPos = (int) ((windowWidth - scaledWidth) / 2);
        int yPos = (int) ((windowHeight - scaledHeight) / 2);

        // Gambar kanvasnya di tengah layar tanpa merusak rasio pixel-nya
        g2.drawImage(screenBuffer, xPos, yPos, scaledWidth, scaledHeight, null);
        
        g2.dispose();
    }

    public OverworldState getOverworldState() {
        if (currentState instanceof OverworldState) {
            return (OverworldState) currentState;
        }
        return null;
    }
    
    public void playMusic(String path) {
        if (currentBGM != null) { currentBGM.stopAudio(); }
        currentBGM = new AudioThread(path, true);
        currentBGM.start();
    }
    
    public void stopMusic() {
        if (currentBGM != null) {
            currentBGM.stopAudio();
            currentBGM = null;
        }
    }
    
    public void playSoundEffect(String path) {
        AudioThread se = new AudioThread(path, false);
        se.start();
    }
}