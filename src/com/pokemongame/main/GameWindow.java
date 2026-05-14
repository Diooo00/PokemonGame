/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.main;

import javax.swing.JFrame;

/**
 *
 * @author thety
 */
public class GameWindow {
    private JFrame frame;
    private GamePanel gamePanel;

    public GameWindow() {
        frame = new JFrame("Pokemon Game");
        gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void startGame() {
        gamePanel.startGameLoop();
    }
}
