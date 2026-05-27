/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

import com.pokemongame.entity.NPC;
import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.ui.DialogBox;

import java.awt.Graphics2D;
/**
 *
 * @author thety
 */
public class DialogState extends GameState {

    private NPC npc;
    private DialogBox dialogBox;
    private KeyHandler keyHandler;

    // State sebelumnya untuk dikembalikan setelah dialog selesai
    private GameState previousState;

    private boolean actionWasPressed = false;

    public DialogState(GamePanel gamePanel, NPC npc, GameState previousState) {
        super(gamePanel);
        this.npc           = npc;
        this.previousState = previousState;
        this.keyHandler    = gamePanel.getKeyHandler();
        this.dialogBox     = new DialogBox(gamePanel);

        // Mulai dari dialog pertama NPC
        if (npc.getCurrentDialog() != null) {
            dialogBox.setText(npc.getCurrentDialog());
        }
    }

    @Override
    public void update() {
        dialogBox.update();

        // --- JURUS PENGHANCUR SINYAL TOMBOL (VERSI DIALOG) ---
        if (keyHandler.actionPressed) {
            if (!actionWasPressed) {
                actionWasPressed = true;
                keyHandler.actionPressed = false; // MANTRA SAKTI: Hancurkan sinyal!

                if (!dialogBox.isFinished()) {
                    // Skip typewriter — langsung tampil penuh
                    dialogBox.skipToEnd();
                } else {
                    // Lanjut ke baris dialog berikutnya
                    npc.nextDialog();
                    if (npc.hasMoreDialog()) {
                        dialogBox.setText(npc.getCurrentDialog());
                    } else {
                        // Dialog habis — kembali ke state sebelumnya
                        npc.resetDialog();
                        gamePanel.setCurrentState(previousState);
                    }
                }
            }
        } else {
            // Buka gembok kalau tombol dilepas
            actionWasPressed = false;
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        // Render state sebelumnya sebagai background
        previousState.render(g2d);

        // Render dialog box di atas
        dialogBox.render(g2d);
    }
}