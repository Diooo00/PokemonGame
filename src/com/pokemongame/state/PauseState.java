package com.pokemongame.state;

import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.util.SaveManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class PauseState extends GameState {

    private GameState previousState;
    private KeyHandler keyHandler;
    private Font font;
    private Font smallFont;

    private enum MenuMode { MAIN, SAVE_SLOTS, LOAD_SLOTS }
    private MenuMode currentMode = MenuMode.MAIN;

    private String[] mainOptions = {"Resume", "Save", "Load", "Exit"};
    private String[] slotPreviews = new String[4];
    private int currentSelection = 0;

    private boolean showingNotification = false;
    private String notificationMessage = "";
    private boolean justLoaded = false;
    
    private boolean actionWasPressed = false;
    private boolean backWasPressed = true; 

    public PauseState(GamePanel gamePanel, GameState previousState) {
        super(gamePanel);
        this.previousState = previousState; 
        this.keyHandler = gamePanel.getKeyHandler();

        try {
            java.io.File fontFile = new java.io.File("res/font/PKMN RBYGSC.ttf");
            this.font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(22f);
            this.smallFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f);
        } catch (Exception e) {
            this.font = new Font("Monospaced", Font.BOLD, 22);
            this.smallFont = new Font("Monospaced", Font.PLAIN, 16);
        }
        refreshSlotPreviews();
    }

    private void refreshSlotPreviews() {
        for (int i = 0; i < 4; i++) slotPreviews[i] = SaveManager.getSlotPreviewInfo(i + 1);
    }

    @Override
    public void update() {
        if (showingNotification) {
            if (keyHandler.actionPressed) {
                if (!actionWasPressed) {
                    showingNotification = false;
                    actionWasPressed = true;
                    keyHandler.actionPressed = false;
                    gamePanel.playSoundEffect("res/sound/select.wav");
                    if (justLoaded) { gamePanel.setCurrentState(previousState); justLoaded = false; }
                }
            } else { actionWasPressed = false; }
            return;
        }

        if (!keyHandler.backPressed) backWasPressed = false;
        if (keyHandler.backPressed && !backWasPressed) {
            keyHandler.backPressed = false; backWasPressed = true;
            gamePanel.playSoundEffect("res/sound/select.wav");
            if (currentMode == MenuMode.MAIN) gamePanel.setCurrentState(previousState);
            else { currentMode = MenuMode.MAIN; currentSelection = 0; }
            return;
        }

        int maxItems = (currentMode == MenuMode.MAIN) ? mainOptions.length : 4;
        if (keyHandler.upPressed) { currentSelection--; if (currentSelection < 0) currentSelection = maxItems - 1; keyHandler.upPressed = false; gamePanel.playSoundEffect("res/sound/select.wav"); }
        if (keyHandler.downPressed) { currentSelection++; if (currentSelection >= maxItems) currentSelection = 0; keyHandler.downPressed = false; gamePanel.playSoundEffect("res/sound/select.wav"); }

        if (keyHandler.actionPressed) {
            if (!actionWasPressed) {
                actionWasPressed = true; keyHandler.actionPressed = false; gamePanel.playSoundEffect("res/sound/select.wav");
                if (currentMode == MenuMode.MAIN) {
                    switch (currentSelection) {
                        case 0: gamePanel.setCurrentState(previousState); break;
                        case 1: currentMode = MenuMode.SAVE_SLOTS; currentSelection = 0; refreshSlotPreviews(); break;
                        case 2: currentMode = MenuMode.LOAD_SLOTS; currentSelection = 0; refreshSlotPreviews(); break;
                        case 3: System.exit(0); break;
                    }
                } else if (currentMode == MenuMode.SAVE_SLOTS) {
                    if (previousState instanceof OverworldState) {
                        SaveManager.saveGameToSlot(((OverworldState)previousState).getPlayer(), currentSelection + 1);
                        refreshSlotPreviews();
                        notificationMessage = "GAME DISIMPAN DI SLOT " + (currentSelection + 1) + "!";
                        showingNotification = true;
                    }
                } else if (currentMode == MenuMode.LOAD_SLOTS) {
                    if (!slotPreviews[currentSelection].equals("EMPTY")) {
                        SaveManager.loadGameFromSlot(((OverworldState)previousState).getPlayer(), currentSelection + 1);
                        notificationMessage = "DATA SLOT " + (currentSelection + 1) + " DIMUAT!";
                        justLoaded = true; showingNotification = true;
                    } else { notificationMessage = "SLOT KOSONG!"; showingNotification = true; }
                }
            }
        } else { actionWasPressed = false; }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (previousState != null) previousState.render(g2d);
        g2d.setColor(new Color(0, 0, 0, 150)); 
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        drawMenuBox(g2d);
        if (showingNotification) drawBottomPopup(g2d, notificationMessage);
    }

    private void drawMenuBox(Graphics2D g2d) {
        int boxWidth = (currentMode == MenuMode.MAIN) ? 300 : 500; // Lebih lebar
        int boxHeight = 300; // Lebih tinggi
        int boxX = (GamePanel.SCREEN_WIDTH - boxWidth) / 2;
        int boxY = (GamePanel.SCREEN_HEIGHT - boxHeight) / 2;

        g2d.setColor(new Color(25, 25, 25, 240));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

        int textX = boxX + 60;
        int textY = boxY + 70;

        g2d.setFont(font);
        if (currentMode == MenuMode.MAIN) {
            for (int i = 0; i < mainOptions.length; i++) {
                if (i == currentSelection) drawArrow(g2d, textX - 35, textY - 15);
                g2d.drawString(mainOptions[i], textX, textY);
                textY += 55;
            }
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.drawString((currentMode == MenuMode.SAVE_SLOTS) ? "SAVE GAME" : "LOAD GAME", boxX + 30, boxY + 40);
            textY = boxY + 90;
            for (int i = 0; i < 4; i++) {
                if (i == currentSelection) drawArrow(g2d, textX - 35, textY - 15);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Slot " + (i + 1), textX, textY);
                g2d.setFont(smallFont);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(slotPreviews[i], textX + 130, textY - 2);
                g2d.setFont(font);
                textY += 50;
            }
        }
    }

    private void drawArrow(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        int[] xPoints = {x, x + 16, x + 8};
        int[] yPoints = {y, y, y + 12};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    private void drawBottomPopup(Graphics2D g2d, String message) {
        int boxW = GamePanel.SCREEN_WIDTH - 40;
        int boxH = 100;
        int boxX = 20;
        int boxY = GamePanel.SCREEN_HEIGHT - 120;

        g2d.setColor(new Color(25, 25, 25, 240));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 15, 15);

        g2d.setFont(font);
        g2d.drawString(message, boxX + 30, boxY + 60);
    }
}