/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

import com.pokemongame.main.GamePanel;
import java.awt.Graphics2D;
/**
 *
 * @author thety
 */
public abstract class GameState {

    protected GamePanel gamePanel;

    public GameState(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // Wajib diimplementasi oleh setiap state
    public abstract void update();
    public abstract void render(Graphics2D g2d);
}
