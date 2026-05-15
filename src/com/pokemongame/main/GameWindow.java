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
public class GameWindow extends JFrame {
    
    private GamePanel gp;

    public GameWindow() {
        gp = new GamePanel();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Pokemon Game");

        // --- FITUR FULLSCREEN ---
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        this.add(gp);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void startGame() {
        gp.startGameLoop();
    }
}
