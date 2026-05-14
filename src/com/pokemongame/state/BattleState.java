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
import com.pokemongame.util.SpriteLoader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
/**
 *
 * @author thety
 */
public class BattleState extends GameState {

    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;

    private KeyHandler keyHandler;
    private HUD hud;

    // Menu state
    private String[] menuOptions = {"Fight", "Run"};
    private int selectedOption   = 0;

    // Battle phase
    public enum Phase { PLAYER_TURN, ENEMY_TURN, BATTLE_END }
    private Phase phase = Phase.PLAYER_TURN;

    // Pesan battle
    private String battleMessage = "";
    private int messageTimer     = 0;
    private static final int MESSAGE_DURATION = 90; // ~1.5 detik

    // State sebelumnya untuk kembali setelah battle
    private GameState previousState;

    private boolean upWasPressed, downWasPressed, actionWasPressed;

    public BattleState(GamePanel gp, Pokemon playerP, Pokemon enemyP,
                       GameState previousState) {
        super(gp);
        this.playerPokemon = playerP;
        this.enemyPokemon = enemyP;
        this.previousState = previousState; // Simpan state map
        this.keyHandler = gp.getKeyHandler();
        this.hud = new HUD();

        this.battleMessage = "A wild " + enemyP.getName() + " appeared!";
        this.messageTimer = MESSAGE_DURATION;
    }

    private void endBattle() {
        // Balik ke map (OverworldState) yang tadi kita simpan
        gamePanel.setCurrentState(previousState);
    }
    
    @Override
    public void update() {
        // Hitung mundur pesan
        if (messageTimer > 0) {
            messageTimer--;
            return; // tunggu pesan selesai sebelum terima input
        }

        switch (phase) {
            case PLAYER_TURN -> handlePlayerInput();
            case ENEMY_TURN  -> handleEnemyTurn();
            case BATTLE_END  -> {
                // Kembali ke overworld setelah battle selesai
                gamePanel.setCurrentState(previousState);
            }
        }
    }

    private void handlePlayerInput() {
        // Navigasi menu atas/bawah
        if (keyHandler.upPressed && !upWasPressed) {
            selectedOption = (selectedOption - 1 + menuOptions.length)
                             % menuOptions.length;
        }
        if (keyHandler.downPressed && !downWasPressed) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
        }

        upWasPressed   = keyHandler.upPressed;
        downWasPressed = keyHandler.downPressed;

        // Konfirmasi pilihan
        if (keyHandler.actionPressed && !actionWasPressed) {
            actionWasPressed = true;

            switch (selectedOption) {
                case 0 -> executePlayerAttack();  // Fight
                case 1 -> flee();                 // Run
            }
        }
        if (!keyHandler.actionPressed) actionWasPressed = false;
    }

    private void executePlayerAttack() {
        Move move = playerPokemon.getMove(0); // pakai move pertama
        if (move == null) return;

        if (move.doesHit()) {
            int damage = playerPokemon.calculateDamage(move);
            enemyPokemon.takeDamage(damage, playerPokemon.getType());
            move.applyEffect(playerPokemon, enemyPokemon);

            battleMessage = playerPokemon.getName() + " used " + move.getName() + "!";
        } else {
            battleMessage = playerPokemon.getName() + "'s attack missed!";
        }

        messageTimer = MESSAGE_DURATION;

        if (enemyPokemon.isFainted()) {
            battleMessage = "Wild " + enemyPokemon.getName() + " fainted! You win!";
            phase = Phase.BATTLE_END;
        } else {
            phase = Phase.ENEMY_TURN;
        }
    }

    private void handleEnemyTurn() {
        Move move = enemyPokemon.getMove(0);
        if (move == null) {
            phase = Phase.PLAYER_TURN;
            return;
        }

        if (move.doesHit()) {
            int damage = enemyPokemon.calculateDamage(move);
            playerPokemon.takeDamage(damage, enemyPokemon.getType());
            battleMessage = "Wild " + enemyPokemon.getName() +
                            " used " + move.getName() + "!";
        } else {
            battleMessage = "Wild " + enemyPokemon.getName() + "'s attack missed!";
        }

        messageTimer = MESSAGE_DURATION;

        if (playerPokemon.isFainted()) {
            battleMessage = playerPokemon.getName() + " fainted! You lose...";
            phase = Phase.BATTLE_END;
        } else {
            phase = Phase.PLAYER_TURN;
        }
    }

    private void flee() {
        battleMessage = "Got away safely!";
        messageTimer  = MESSAGE_DURATION;
        phase = Phase.BATTLE_END;
    }

    @Override
    public void render(Graphics2D g2d) {
        // Render Background Hijau
        g2d.setColor(new Color(100, 180, 100));
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // Render HUD
        hud.renderPokemonHUD(g2d, playerPokemon, 20, GamePanel.SCREEN_HEIGHT - 140, true);
        hud.renderPokemonHUD(g2d, enemyPokemon, GamePanel.SCREEN_WIDTH - 220, 20, false);

        // RENDER SPRITE POKEMON
        // Player Pokemon (Kiri Bawah)
        java.awt.image.BufferedImage pImg = SpriteLoader.loadPokemon(playerPokemon.getName());
        if (pImg != null) {
            g2d.drawImage(pImg, 100, GamePanel.SCREEN_HEIGHT / 2 - 40, 120, 120, null);
        }

        // Enemy Pokemon (Kanan Atas)
        java.awt.image.BufferedImage eImg = SpriteLoader.loadPokemon(enemyPokemon.getName());
        if (eImg != null) {
            g2d.drawImage(eImg, GamePanel.SCREEN_WIDTH - 220, 80, 120, 120, null);
        }

        // Menu Aksi
        if (phase == Phase.PLAYER_TURN && battleMessage.isEmpty()) {
            hud.renderBattleMenu(g2d, menuOptions, selectedOption);
        }

        // Pesan Battle
        if (!battleMessage.isEmpty()) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRoundRect(20, GamePanel.SCREEN_HEIGHT - 70, GamePanel.SCREEN_WIDTH - 40, 50, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            g2d.drawString(battleMessage, 40, GamePanel.SCREEN_HEIGHT - 40);
        }
    }
}
