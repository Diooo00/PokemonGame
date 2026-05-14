/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.main;

/**
 *
 * @author thety
 */
public class GameLoop implements Runnable {

    private final GamePanel gamePanel;
    private Thread thread;
    private boolean running = false;

    // Target 60 FPS
    private static final int FPS = 60;
    private static final double DRAW_INTERVAL = 1_000_000_000.0 / FPS;

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / DRAW_INTERVAL;
            lastTime = currentTime;

            if (delta >= 1) {
                gamePanel.update();
                gamePanel.repaint();
                delta--;
            }
        }
    }
}