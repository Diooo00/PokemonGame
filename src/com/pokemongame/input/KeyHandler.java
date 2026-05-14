/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.input;

import com.pokemongame.main.GamePanel;
import com.pokemongame.util.SaveManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 *
 * @author thety
 */
public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }
    
    public boolean actionPressed;
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Biarkan kosong
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        //Tombol P  untuk Save
        if (code == KeyEvent.VK_P) {
            if (gp.getOverworldState() != null) {
                SaveManager.saveGame(gp.getOverworldState().getPlayer());
            }
        }
        
        // Tombol W atau Panah Atas
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = true; }
        // Tombol S atau Panah Bawah
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = true; }
        // Tombol A atau Panah Kiri
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = true; }
        // Tombol D atau Panah Kanan
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = true; }
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) { upPressed = false; }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) { downPressed = false; }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = false; }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = false; }
    }
    
    // FUNGSI INI YANG BIKIN OVERWORLD-MU MERAH KEMARIN
    // Ini mengembalikan nilai true jika salah satu tombol arah sedang ditekan
    public boolean isMoving() {
        return upPressed || downPressed || leftPressed || rightPressed;
    }
}