/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/**
 *
 * @author thety
 */
public class KeyHandler extends KeyAdapter {

    // Gerakan
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // Aksi (konfirmasi dialog, dll)
    public boolean actionPressed;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed    = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed  = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed  = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_ENTER) actionPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed    = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed  = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed  = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_ENTER) actionPressed = false;
    }

    public boolean isMoving() {
        // Kembalikan true jika salah satu tombol arah sedang ditekan
        return upPressed || downPressed || leftPressed || rightPressed;
    }
}