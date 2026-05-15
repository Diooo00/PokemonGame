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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
/**
 *
 * @author thety
 */
public class BattleState extends GameState {

    private enum SubState { MAIN_MENU, MOVE_SELECTION, MESSAGE_ONLY }
    private SubState subState = SubState.MESSAGE_ONLY; // Mulai dengan pesan intro

    public enum Phase { PLAYER_TURN, ENEMY_TURN, BATTLE_END }
    private Phase phase = Phase.PLAYER_TURN;

    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private KeyHandler keyHandler;
    private HUD hud;
    private GameState previousState;

    private String[] menuOptions = {"Fight", "Run"};
    private int selectedOption = 0;
    private int selectedMove = 0;

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

        // Cek jika player sudah mati sebelum mulai
        if (this.playerPokemon.getCurrentHp() <= 0) {
            this.playerPokemon.setCurrentHp(this.playerPokemon.getMaxHp()); 
        }

        this.battleMessage = "A wild " + enemyP.getName() + " appeared!";
        this.messageTimer = MESSAGE_DURATION;
        this.subState = SubState.MESSAGE_ONLY;
    }

    @Override
    public void update() {
        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) {
                processMessageEnd();
            }
            return;
        }

        if (phase == Phase.PLAYER_TURN && subState != SubState.MESSAGE_ONLY) {
            if (subState == SubState.MAIN_MENU) handleMainMenuInput();
            else if (subState == SubState.MOVE_SELECTION) handleMoveSelectionInput();
        } else if (phase == Phase.ENEMY_TURN) {
            handleEnemyTurn();
        }
    }

    private void processMessageEnd() {
        battleMessage = ""; 

        // 1. Jika battle sudah selesai (Menang/Kabur/Kalah), balik ke Map
        if (phase == Phase.BATTLE_END) {
            gamePanel.setCurrentState(previousState);
            return; 
        }

        // 2. Cek apakah ada yang mati setelah pesan serangan muncul
        if (enemyPokemon.isFainted()) {
            handleVictory(); // Ini akan set phase ke BATTLE_END dan message baru
            return;
        } 

        if (playerPokemon.isFainted()) {
            // Pesan sudah diset di handleEnemyTurn, sekarang tinggal tutup
            phase = Phase.BATTLE_END;
            messageTimer = MESSAGE_DURATION; // Kasih waktu buat baca "Black out"
            return;
        }

        // 3. Atur perpindahan giliran (Turn Flow)
        if (phase == Phase.ENEMY_TURN) {
            // Musuh baru saja selesai menyerang -> Sekarang giliran Player pilih menu
            phase = Phase.PLAYER_TURN;
            subState = SubState.MAIN_MENU;
        } 
        else if (phase == Phase.PLAYER_TURN) {
            // Player baru saja selesai menyerang -> Sekarang giliran Musuh menyerang
            // KECUALI ini adalah pesan INTRO "A wild Pokemon appeared"
            if (subState == SubState.MESSAGE_ONLY) {
                subState = SubState.MAIN_MENU;
            } else {
                phase = Phase.ENEMY_TURN;
            }
        }
    }

    private void handleMainMenuInput() {
        if (keyHandler.leftPressed) selectedOption = 0;
        if (keyHandler.rightPressed) selectedOption = 1;
        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            if (selectedOption == 0) subState = SubState.MOVE_SELECTION;
            else flee();
        }
    }

    private void handleMoveSelectionInput() {
        if (keyHandler.upPressed && selectedMove >= 2) selectedMove -= 2;
        if (keyHandler.downPressed && selectedMove <= 1) selectedMove += 2;
        if (keyHandler.leftPressed && selectedMove % 2 != 0) selectedMove -= 1;
        if (keyHandler.rightPressed && selectedMove % 2 == 0) selectedMove += 1;

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            executePlayerTurn(playerPokemon.getMove(selectedMove));
        }
        if (keyHandler.backPressed) {
            keyHandler.backPressed = false;
            subState = SubState.MAIN_MENU;
        }
    }

    private void executePlayerTurn(Move move) {
        if (move == null) return;
        subState = SubState.MESSAGE_ONLY;
        int damage = playerPokemon.calculateDamage(move);
        enemyPokemon.takeDamage(damage, move.getType());
        battleMessage = playerPokemon.getName() + " used " + move.getName() + "!";
        messageTimer = MESSAGE_DURATION;
        
        // Ganti phase ke musuh setelah pesan ini selesai
        if (!enemyPokemon.isFainted()) {
            phase = Phase.ENEMY_TURN;
        }
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

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(new Color(100, 180, 100));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        hud.renderPokemonHUD(g2d, playerPokemon, 20, GamePanel.SCREEN_HEIGHT - 140, true);
        hud.renderPokemonHUD(g2d, enemyPokemon, GamePanel.SCREEN_WIDTH - 220, 20, false);

        java.awt.image.BufferedImage pImg = SpriteLoader.loadPokemon(playerPokemon.getName());
        if (pImg != null) g2d.drawImage(pImg, 80, GamePanel.SCREEN_HEIGHT / 2 - 20, 150, 150, null);

        java.awt.image.BufferedImage eImg = SpriteLoader.loadPokemon(enemyPokemon.getName());
        if (eImg != null) g2d.drawImage(eImg, GamePanel.SCREEN_WIDTH - 200, 60, 120, 120, null);

        if (messageTimer <= 0) {
            if (subState == SubState.MAIN_MENU) hud.renderBattleMenu(g2d, menuOptions, selectedOption);
            else if (subState == SubState.MOVE_SELECTION) hud.renderMoveMenu(g2d, playerPokemon.getMoves(), selectedMove);
        }

        if (!battleMessage.isEmpty()) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRoundRect(20, GamePanel.SCREEN_HEIGHT - 70, GamePanel.SCREEN_WIDTH - 40, 50, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(battleMessage, 40, GamePanel.SCREEN_HEIGHT - 40);
        }
    }
}