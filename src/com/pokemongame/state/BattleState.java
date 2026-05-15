/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.state;

import com.pokemongame.input.KeyHandler;
import com.pokemongame.main.GamePanel;
import com.pokemongame.pokemon.Move;
import com.pokemongame.pokemon.Pokemon;
import com.pokemongame.ui.HUD;
import com.pokemongame.util.SaveManager;
import com.pokemongame.util.SpriteLoader;
import com.pokemongame.item.Item;

import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import java.io.File;


/**
 *
 * @author thety
 */
public class BattleState extends GameState {
    private Font pokemonFont;
    private enum SubState { MAIN_MENU, MOVE_SELECTION, BAG, POKEMON, MESSAGE_ONLY }
    private SubState subState = SubState.MESSAGE_ONLY;

    public enum Phase { PLAYER_TURN, ENEMY_TURN, BATTLE_END }
    private Phase phase = Phase.PLAYER_TURN;

    private int shakeTimer = 0; 
    
    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private KeyHandler keyHandler;
    private HUD hud;
    private GameState previousState;

    // --- DATA DINAMIS ---
    private String[] menuOptions = {"Fight", "Bag", "Pokemon", "Run"};
    private int selectedOption = 0;
    private int selectedMove = 0;
    
    private List<Item> inventory; 
    private int selectedItem = 0;

    private String battleMessage = "";
    private int messageTimer = 0;
    private static final int MESSAGE_DURATION = 90;

    public BattleState(GamePanel gp, Pokemon playerP, Pokemon enemyP, GameState previousState) {
        super(gp);
        this.playerPokemon = playerP;
        this.enemyPokemon = enemyP;
        this.previousState = previousState;
        this.keyHandler = gp.getKeyHandler();
        this.hud = new HUD();

        // 1. Load Inventory dari SaveManager (DB)
        this.inventory = SaveManager.loadPlayerItems();

        if (this.playerPokemon.getCurrentHp() <= 0) {
            this.playerPokemon.setCurrentHp(this.playerPokemon.getMaxHp()); 
        }

        // 2. Load Font
        try {
            File fontFile = new File("res/font/PKMN RBYGSC.ttf"); 
            this.pokemonFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(this.pokemonFont);
        } catch (Exception e) {
            this.pokemonFont = new Font("Monospaced", Font.BOLD, 20);
        }

        this.battleMessage = "A wild " + enemyP.getName() + " appeared!";
        this.messageTimer = MESSAGE_DURATION;
        this.subState = SubState.MESSAGE_ONLY;
    }

    @Override
    public void update() {
        if (shakeTimer > 0) shakeTimer--;

        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) processMessageEnd();
            return;
        }

        if (phase == Phase.PLAYER_TURN) {
            switch (subState) {
                case MAIN_MENU: handleMainMenu(); break;
                case MOVE_SELECTION: handleMoveSelection(); break;
                case BAG: handleBagInput(); break;
                case POKEMON: handlePartyInput(); break;
            }
        } else if (phase == Phase.ENEMY_TURN) {
            handleEnemyTurn();
        }
    }

    private void processMessageEnd() {
        battleMessage = ""; 
        if (phase == Phase.BATTLE_END) {
            gamePanel.setCurrentState(previousState);
            return; 
        }
        if (enemyPokemon.isFainted()) {
            handleVictory();
            return;
        } 
        if (playerPokemon.isFainted()) {
            phase = Phase.BATTLE_END;
            messageTimer = MESSAGE_DURATION;
            return;
        }

        if (phase == Phase.ENEMY_TURN) {
            phase = Phase.PLAYER_TURN;
            changeSubState(SubState.MAIN_MENU);
        } else if (phase == Phase.PLAYER_TURN) {
            if (subState == SubState.MESSAGE_ONLY) {
                changeSubState(SubState.MAIN_MENU);
            } else {
                phase = Phase.ENEMY_TURN;
            }
        }
    }

    private void handleMainMenu() {
        if (keyHandler.upPressed && selectedOption >= 2) { selectedOption -= 2; keyHandler.upPressed = false; }
        if (keyHandler.downPressed && selectedOption <= 1) { selectedOption += 2; keyHandler.downPressed = false; }
        if (keyHandler.leftPressed && selectedOption % 2 != 0) { selectedOption -= 1; keyHandler.leftPressed = false; }
        if (keyHandler.rightPressed && selectedOption % 2 == 0) { selectedOption += 1; keyHandler.rightPressed = false; }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            if (selectedOption == 0) changeSubState(SubState.MOVE_SELECTION);
            else if (selectedOption == 1) changeSubState(SubState.BAG);
            else if (selectedOption == 2) changeSubState(SubState.POKEMON);
            else if (selectedOption == 3) flee();
        }
    }

    private void handleMoveSelection() {
        int moveCount = playerPokemon.getMoves().size();
        if (keyHandler.upPressed && selectedMove >= 2) { selectedMove -= 2; keyHandler.upPressed = false; }
        if (keyHandler.downPressed && selectedMove <= 1 && (selectedMove + 2) < moveCount) { selectedMove += 2; keyHandler.downPressed = false; }
        if (keyHandler.leftPressed && selectedMove % 2 != 0) { selectedMove -= 1; keyHandler.leftPressed = false; }
        if (keyHandler.rightPressed && selectedMove % 2 == 0 && (selectedMove + 1) < moveCount) { selectedMove += 1; keyHandler.rightPressed = false; }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            executePlayerTurn(playerPokemon.getMove(selectedMove));
        }
        if (keyHandler.backPressed) { changeSubState(SubState.MAIN_MENU); }
    }

    private void handleBagInput() {
        int itemCount = inventory.size();
        if (itemCount == 0) {
            if (keyHandler.backPressed) changeSubState(SubState.MAIN_MENU);
            return;
        }

        if (keyHandler.upPressed && selectedItem >= 2) { selectedItem -= 2; keyHandler.upPressed = false; }
        if (keyHandler.downPressed && selectedItem <= 1 && (selectedItem + 2) < itemCount) { selectedItem += 2; keyHandler.downPressed = false; }
        if (keyHandler.leftPressed && selectedItem % 2 != 0) { selectedItem -= 1; keyHandler.leftPressed = false; }
        if (keyHandler.rightPressed && selectedItem % 2 == 0 && (selectedItem + 1) < itemCount) { selectedItem += 1; keyHandler.rightPressed = false; }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            applyItemEffect(inventory.get(selectedItem));
        }
        if (keyHandler.backPressed) { changeSubState(SubState.MAIN_MENU); }
    }
    
    private void applyItemEffect(Item item) {
        if (item.getQuantity() <= 0) return;

        item.use(); 
        subState = SubState.MESSAGE_ONLY;
        battleMessage = "Used " + item.getName().toUpperCase() + "!";
        
        if (item.getName().toLowerCase().contains("potion")) {
            playerPokemon.setCurrentHp(playerPokemon.getCurrentHp() + item.getEffectValue());
            if (playerPokemon.getCurrentHp() > playerPokemon.getMaxHp()) 
                playerPokemon.setCurrentHp(playerPokemon.getMaxHp());
        }

        messageTimer = MESSAGE_DURATION;
        phase = Phase.ENEMY_TURN; 
    }

    private void handlePartyInput() {
        if (keyHandler.backPressed) {
            changeSubState(SubState.MAIN_MENU);
        }
    }
    
    private void executePlayerTurn(Move move) {
        if (move == null) return;
        subState = SubState.MESSAGE_ONLY;
        int damage = playerPokemon.calculateDamage(move);
        enemyPokemon.takeDamage(damage, move.getType());
        battleMessage = playerPokemon.getName() + " used " + move.getName() + "!";
        messageTimer = MESSAGE_DURATION;
        shakeTimer = 20; 
        
        if (!enemyPokemon.isFainted()) phase = Phase.ENEMY_TURN;
    }

    private void handleEnemyTurn() {
        List<Move> enemyMoves = enemyPokemon.getMoves();
        if (enemyMoves.isEmpty()) { phase = Phase.PLAYER_TURN; return; }

        Move move = enemyMoves.get((int) (Math.random() * enemyMoves.size()));
        int damage = enemyPokemon.calculateDamage(move);
        playerPokemon.takeDamage(damage, move.getType());

        battleMessage = "Wild " + enemyPokemon.getName() + " used " + move.getName() + "!";
        messageTimer = MESSAGE_DURATION;
        subState = SubState.MESSAGE_ONLY;
        phase = Phase.PLAYER_TURN;

        if (playerPokemon.isFainted()) {
            battleMessage = playerPokemon.getName() + " fainted! You blacked out...";
            phase = Phase.BATTLE_END;
        }
    }

    private void handleVictory() {
        int exp = enemyPokemon.getLevel() * 20;
        playerPokemon.gainExp(exp);
        battleMessage = enemyPokemon.getName() + " fainted! Gained " + exp + " EXP.";
        messageTimer = MESSAGE_DURATION;
        phase = Phase.BATTLE_END;
        SaveManager.savePokemonStatus(playerPokemon);
    }

    private void flee() {
        battleMessage = "Got away safely!";
        messageTimer = MESSAGE_DURATION;
        phase = Phase.BATTLE_END;
    }

    private void changeSubState(SubState newState) {
        this.subState = newState;
        keyHandler.actionPressed = false;
        keyHandler.backPressed = false;
        keyHandler.upPressed = false;
        keyHandler.downPressed = false;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // 1. Background
        g2d.setColor(new Color(100, 180, 100));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // 2. HUD Musuh
        hud.renderPokemonHUD(g2d, enemyPokemon, GamePanel.SCREEN_WIDTH - 220, 20, false, pokemonFont);
        
        // 3. Render Sprites
        renderSprites(g2d);

        // 4. UI BOTTOM AREA
        int bottomY = GamePanel.SCREEN_HEIGHT - 130;
        int boxX = 20;
        int boxW = (messageTimer <= 0) ? 400 : GamePanel.SCREEN_WIDTH - 40;

        // Kotak Pesan / Status
        g2d.setColor(new Color(30, 30, 30, 250));
        g2d.fillRoundRect(boxX, bottomY, boxW, 110, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.drawRoundRect(boxX, bottomY, boxW, 110, 15, 15);

        if (!battleMessage.isEmpty()) {
            g2d.setFont(pokemonFont.deriveFont(16f)); 
            g2d.drawString(battleMessage, boxX + 40, bottomY + 65);
        } else {
            renderIntegratedStatus(g2d, boxX, bottomY);
        }

        // 5. MENU INTERAKSI DINAMIS
        if (messageTimer <= 0) {
            if (subState == SubState.MAIN_MENU) {
                hud.renderBattleMenu(g2d, menuOptions, selectedOption, pokemonFont);
            } 
            else if (subState == SubState.MOVE_SELECTION) {
                hud.renderMoveMenu(g2d, playerPokemon.getMoves(), selectedMove, pokemonFont);
            }
            else if (subState == SubState.BAG) {
                hud.renderBagMenu(g2d, inventory, selectedItem, pokemonFont);
            }
            else if (subState == SubState.POKEMON) {
                hud.renderPartyMenu(g2d, java.util.Arrays.asList(playerPokemon), 0, pokemonFont);
            }
        }                                            
    }

    private void renderSprites(Graphics2D g2d) {
        // Kunci Pixel Art: Pakai NEAREST_NEIGHBOR biar pas digedein tetep tajam
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // 2. Set ukuran berbeda untuk Player dan Musuh
        int playerSpriteSize = 600; 
        int enemySpriteSize = (int) (playerSpriteSize * 0.675); // scaling dari ukurang player

        // --- RENDER SPRITE PLAYER (Kiri Bawah) ---
        java.awt.image.BufferedImage pImg = SpriteLoader.loadPokemon(playerPokemon.getPokeId()); 

        if (pImg != null) {
            int pX = 60;
            // Posisikan di atas kotak dialog bawah
            int pY = GamePanel.SCREEN_HEIGHT - playerSpriteSize - 130; 

            // Animasi maju saat menyerang
            if (subState == SubState.MESSAGE_ONLY && phase == Phase.PLAYER_TURN && !enemyPokemon.isFainted()) {
                pX += 40; 
            }

            // Gambar menggunakan playerSpriteSize
            g2d.drawImage(pImg, pX, pY, playerSpriteSize, playerSpriteSize, null);
        }

        // --- RENDER SPRITE MUSUH (Kanan Atas) ---
        java.awt.image.BufferedImage eImg = SpriteLoader.loadPokemon(enemyPokemon.getPokeId());

        if (eImg != null) {
            // Posisikan di pojok kanan, dikurangi ukuran sprite musuh dan margin
            int eX = GamePanel.SCREEN_WIDTH - enemySpriteSize - 200; 
            
            // Y nya kita tambah sedikit biar musuhnya nggak kelihatan terlalu "terbang" ke atas
            int eY = 120; 

            // Efek getar (shake) saat kena hit
            if (shakeTimer > 0 && phase == Phase.PLAYER_TURN) {
                eX += (int)(Math.random() * 10) - 5;
                eY += (int)(Math.random() * 10) - 5;
            }

            // Gambar menggunakan enemySpriteSize
            g2d.drawImage(eImg, eX, eY, enemySpriteSize, enemySpriteSize, null);
        }
    }
    
    private void renderIntegratedStatus(Graphics2D g2d, int x, int y) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // NAMA & LEVEL
        g2d.setFont(pokemonFont.deriveFont(14f));
        g2d.setColor(Color.WHITE);
        g2d.drawString(playerPokemon.getName().toUpperCase(), x + 40, y + 45);
        g2d.setFont(pokemonFont.deriveFont(12f));
        g2d.drawString("Lv " + playerPokemon.getLevel(), x + 280, y + 45);

        // HP BAR
        g2d.setFont(pokemonFont.deriveFont(10f));
        g2d.setColor(new Color(255, 225, 0)); 
        g2d.drawString("HP", x + 40, y + 72);

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(x + 80, y + 60, 250, 14);

        double hpPercent = (double) playerPokemon.getCurrentHp() / playerPokemon.getMaxHp();
        int barWidth = (int) (hpPercent * 250);
        if (hpPercent > 0.5) g2d.setColor(new Color(65, 225, 65));
        else if (hpPercent > 0.2) g2d.setColor(Color.YELLOW);
        else g2d.setColor(new Color(255, 60, 60));
        g2d.fillRect(x + 80, y + 60, barWidth, 14);

        // ANGKA HP
        g2d.setFont(pokemonFont.deriveFont(14f));
        g2d.setColor(Color.WHITE);
        String hpText = playerPokemon.getCurrentHp() + "/" + playerPokemon.getMaxHp();
        int textW = g2d.getFontMetrics().stringWidth(hpText);
        g2d.drawString(hpText, x + 330 - textW, y + 95);
    }
}