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
import com.pokemongame.util.BattleMechanics;
import com.pokemongame.item.Item;
import com.pokemongame.util.TypeChart;
import java.awt.AlphaComposite;

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
    private enum SubState { MAIN_MENU, MOVE_SELECTION, BAG, POKEMON, MESSAGE_ONLY, CATCH_ANIMATION }
    private SubState subState = SubState.MESSAGE_ONLY;
    
    private int catchPhase = 0; 
    private int catchTimer = 0;
    private int ballX, ballY;
    private int ballStartX, ballStartY;
    private int ballTargetX, ballTargetY;
    private boolean catchSuccess = false;
    
    private java.awt.image.BufferedImage pokeballImg, greatballImg, ultraballImg, currentBallImg;
    private double ballAngle = 0;

    public enum Phase { PLAYER_TURN, ENEMY_TURN, BATTLE_END }
    private Phase phase = Phase.PLAYER_TURN;

    private int shakeTimer = 0; 
    
    private String lastBattleMessage = "";
    private String lastEffectMessage = "";
    private int charIndex = 0;
    
    private Pokemon playerPokemon;
    private Pokemon enemyPokemon;
    private KeyHandler keyHandler;
    private HUD hud;
    private GameState previousState;
    private Move playerChosenMove = null;
    private int turnStep = 0;
    private boolean playerGoesFirst = true;
    private boolean isExiting = false;
    private int exitCounter = 0;
    private final int MAX_EXIT_TIME = 60;
    
    // --- VARIABEL ANIMASI SLIDE MASUK ---
    private int introCounter = 0;
    private final int MAX_INTRO_TIME = 45;

    // --- DATA TIM & TAS DARI DATABASE ---
    private String[] menuOptions = {"Fight", "Bag", "Pokemon", "Run"};
    private int selectedOption = 0;
    private int selectedMove = 0;
    
    private List<Item> inventory; 
    private int selectedItem = 0;
    
    private List<Pokemon> playerParty; 
    private int selectedPartyIndex = 0;

    // --- PENGAMAN LOGIKA GANTI POKEMON & BATAS KEMATIAN ---
    private boolean waitingForFaintSwitch = false; 
    private boolean justSwitched = false;          
    private boolean blockSwitching = false;        
    private boolean isFaintHandled = false;
    private int faintsInThisBattle = 0; // Penghitung 3 kali mati

    private String battleMessage = "";
    private int messageTimer = 0;
    private static final int MESSAGE_DURATION = 90;
    
    public String effectMessage = "";

    public BattleState(GamePanel gp, Pokemon playerP, Pokemon enemyP, GameState previousState) {
        super(gp);
        this.enemyPokemon = enemyP;
        this.previousState = previousState;
        this.keyHandler = gp.getKeyHandler();
        this.hud = new HUD();
        this.pokeballImg = SpriteLoader.loadSprite("/sprites/pokeball.png");
        this.greatballImg = SpriteLoader.loadSprite("/sprites/greatball.png");
        this.ultraballImg = SpriteLoader.loadSprite("/sprites/ultraball.png");
        gp.playMusic("res/sound/battle.wav");

        this.inventory = SaveManager.loadPlayerItems();
        this.playerParty = SaveManager.loadPlayerParty(); 
        
        if(this.playerParty.isEmpty()){
            this.playerParty.add(playerP);
        }

        // --- FIX BUG KAGEBUNSHIN / IMMORTAL ---
        this.playerPokemon = null;
        for (Pokemon p : this.playerParty) {
            if (p.getCurrentHp() > 0) {
                this.playerPokemon = p; // Ambil pokemon pertama yang masih hidup
                break;
            }
        }
        if (this.playerPokemon == null) {
            this.playerPokemon = this.playerParty.get(0);
        }

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
        
        this.introCounter = 0; 
    }

    private void savePartyToDatabase() {
        for(Pokemon p : playerParty) {
            SaveManager.savePokemonStatus(p);
        }
    }

    @Override
    public void update() {
        if (introCounter < MAX_INTRO_TIME) {
            introCounter++;
            return; 
        }

        if (isExiting) {
            exitCounter++;
            if (exitCounter >= MAX_EXIT_TIME) {
                gamePanel.setCurrentState(previousState);
                gamePanel.playMusic("res/sound/overworld.wav");
            }
            return; 
        }
        
        if (shakeTimer > 0) shakeTimer--;

        // --- FIX: KUNCI LAYAR DI AKHIR BATTLE ---
        if (phase == Phase.BATTLE_END && messageTimer == 0) {
            if (keyHandler.actionPressed) {
                keyHandler.actionPressed = false;
                isExiting = true; 
            }
            return; 
        }

        if (!battleMessage.equals(lastBattleMessage) || !effectMessage.equals(lastEffectMessage)) {
            lastBattleMessage = battleMessage;
            lastEffectMessage = effectMessage;
            charIndex = 0; 
        }

        int totalLen = battleMessage.length() + effectMessage.length();
        if (charIndex < totalLen) {
            charIndex++; 
        }

        if (messageTimer > 0) {
            if (keyHandler.actionPressed) {
                keyHandler.actionPressed = false;
                
                if (charIndex < totalLen) {
                    charIndex = totalLen; 
                } else {
                    messageTimer = 1; 
                }
            }

            messageTimer--;
            if (messageTimer == 0) processMessageEnd();
            return;
        } 
        // --- FITUR BARU: NUNGGU ENTER MANUAL BUAT HASIL TANGKAPAN (-1) ---
        else if (messageTimer == -1) {
            if (keyHandler.actionPressed) {
                keyHandler.actionPressed = false;
                if (charIndex < totalLen) {
                    charIndex = totalLen; 
                } else {
                    messageTimer = 0; 
                    processMessageEnd(); 
                }
            }
            return; 
        }

        if (turnStep == 0 && phase != Phase.BATTLE_END) {
            switch (subState) {
                case MAIN_MENU: handleMainMenu(); break;
                case MOVE_SELECTION: handleMoveSelection(); break;
                case BAG: handleBagInput(); break;
                case POKEMON: handlePartyInput(); break;
                case CATCH_ANIMATION: handleCatchAnimation(); break;
            }
        }
    }

    private void processMessageEnd() {
        battleMessage = ""; 

        if (subState == SubState.CATCH_ANIMATION) {
            return; 
        }

        if (phase == Phase.BATTLE_END) {
            isExiting = true;
            return; 
        }

        if (waitingForFaintSwitch) {
            waitingForFaintSwitch = false;
            turnStep = 0; 
            phase = Phase.PLAYER_TURN; 
            changeSubState(SubState.POKEMON);
            return;
        }

        if (justSwitched) {
            justSwitched = false;
            changeSubState(SubState.MAIN_MENU);
            return;
        }

        if (enemyPokemon.isFainted()) {
            handleVictory(); 
            return;
        } 
        
        if (playerPokemon.isFainted() && !isFaintHandled) {
            isFaintHandled = true; 
            faintsInThisBattle++;

            boolean hasAlivePokemon = false;
            for(Pokemon p : playerParty) {
                if(!p.isFainted()) {
                    hasAlivePokemon = true;
                    break;
                }
            }
            
            if (faintsInThisBattle >= 3) {
                battleMessage = "3 of your Pokémon fainted! You blacked out...";
                effectMessage = ""; 
                messageTimer = 0;   
                phase = Phase.BATTLE_END; 
                savePartyToDatabase(); 
                return;
            } else if (hasAlivePokemon) {
                battleMessage = playerPokemon.getName() + " fainted! Choose another Pokémon!";
                messageTimer = 60;
                waitingForFaintSwitch = true; 
                return;
            } else {
                battleMessage = playerPokemon.getName() + " fainted! You blacked out...";
                messageTimer = 90; 
                phase = Phase.BATTLE_END; 
                savePartyToDatabase(); 
                return;
            }
        }

        if (turnStep == 1) {
            turnStep = 2; 
            executeNextAttack();
        } else {
            
            // --- FIX ANTI JEBOL: Hanya reset gembok kalau player beneran habis nyerang! ---
            if (playerChosenMove != null) {
                blockSwitching = false; 
            }
            
            turnStep = 0;
            playerChosenMove = null;
            changeSubState(SubState.MAIN_MENU);
        }
    }

    private void handleMainMenu() {
        if (keyHandler.upPressed && selectedOption >= 2) { 
            selectedOption -= 2; 
            keyHandler.upPressed = false; 
            gamePanel.playSoundEffect("res/sound/select.wav");
        }
        if (keyHandler.downPressed && selectedOption <= 1) { 
            selectedOption += 2; 
            keyHandler.downPressed = false; 
            gamePanel.playSoundEffect("res/sound/select.wav");
        }
        if (keyHandler.leftPressed && selectedOption % 2 != 0) { 
            selectedOption -= 1; 
            keyHandler.leftPressed = false; 
            gamePanel.playSoundEffect("res/sound/select.wav");
        }
        if (keyHandler.rightPressed && selectedOption % 2 == 0) { 
            selectedOption += 1; 
            keyHandler.rightPressed = false; 
            gamePanel.playSoundEffect("res/sound/select.wav");
        }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            gamePanel.playSoundEffect("res/sound/select.wav"); 
            if (selectedOption == 0) changeSubState(SubState.MOVE_SELECTION);
            else if (selectedOption == 1) changeSubState(SubState.BAG);
            else if (selectedOption == 2) {
                if (blockSwitching) {
                    battleMessage = "You already switched this turn!";
                    messageTimer = 60;
                    subState = SubState.MESSAGE_ONLY;
                } else {
                    changeSubState(SubState.POKEMON);
                }
            }
            else if (selectedOption == 3) flee();
        }
    }

    private void handleMoveSelection() {
        int moveCount = playerPokemon.getMoves().size();
        if (moveCount == 0) {
            if (keyHandler.backPressed) changeSubState(SubState.MAIN_MENU);
            return;
        }

        if (keyHandler.upPressed) {
            keyHandler.upPressed = false;
            if (selectedMove >= 2) {
                selectedMove -= 2;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.downPressed) {
            keyHandler.downPressed = false;
            if (selectedMove + 2 < moveCount) {
                selectedMove += 2;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.leftPressed) {
            keyHandler.leftPressed = false;
            if (selectedMove % 2 != 0) {
                selectedMove -= 1;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.rightPressed) {
            keyHandler.rightPressed = false;
            if (selectedMove % 2 == 0 && selectedMove + 1 < moveCount) {
                selectedMove += 1;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            gamePanel.playSoundEffect("res/sound/select.wav"); 
            playerChosenMove = playerPokemon.getMove(selectedMove);

            if (playerPokemon.getSpeed() >= enemyPokemon.getSpeed()) {
                playerGoesFirst = true;
            } else {
                playerGoesFirst = false;
            }

            turnStep = 1; 
            executeNextAttack();
        }

        if (keyHandler.backPressed) { 
            keyHandler.backPressed = false;
            if (!blockSwitching) {
                changeSubState(SubState.MAIN_MENU); 
            }
        }
    }

    private void executeNextAttack() {
        if (turnStep == 1) {
            if (playerGoesFirst) playerAttackAction();
            else enemyAttackAction();
        } else if (turnStep == 2) {
            if (playerGoesFirst) enemyAttackAction();
            else playerAttackAction();
        }
    }

    private void playerAttackAction() {
        subState = SubState.MESSAGE_ONLY;

        int atkStat = 0;
        int defStat = 0;
        
        if (playerChosenMove.getCategory() == Move.Category.PHYSICAL) {
            atkStat = playerPokemon.getAttack();
            defStat = enemyPokemon.getDefense();
        } else {
            atkStat = playerPokemon.getSpAtk();
            defStat = enemyPokemon.getSpDef();
        }

        double abilityMod = 1.0;
        if (playerPokemon.getCurrentHp() <= playerPokemon.getMaxHp() / 3) {
            String pType = playerPokemon.getType1();
            String mType = playerChosenMove.getType();
            if (pType.equals("FIRE") && mType.equals("FIRE")) abilityMod = 1.5;
            else if (pType.equals("WATER") && mType.equals("WATER")) abilityMod = 1.5;
            else if (pType.equals("GRASS") && mType.equals("GRASS")) abilityMod = 1.5;
        }

        double stab = TypeChart.getStabMultiplier(playerPokemon, playerChosenMove);
        double typeMod = TypeChart.getEffectiveness(playerChosenMove.getType(), enemyPokemon.getType1());
        if (enemyPokemon.getType2() != null) {
            typeMod *= TypeChart.getEffectiveness(playerChosenMove.getType(), enemyPokemon.getType2());
        }

        int damage = BattleMechanics.calculateDamage(
            playerPokemon.getLevel(), atkStat, defStat, playerChosenMove.getPower(), stab, typeMod, abilityMod
        );

        enemyPokemon.setCurrentHp(enemyPokemon.getCurrentHp() - damage);
        if (enemyPokemon.getCurrentHp() < 0) enemyPokemon.setCurrentHp(0);
        
        battleMessage = playerPokemon.getName() + " used " + playerChosenMove.getName() + "!";
        if (typeMod > 1.0) effectMessage = "It's super effective!";
        else if (typeMod < 1.0 && typeMod > 0) effectMessage = "It's not very effective...";
        else if (typeMod == 0.0) effectMessage = "It had no effect...";
        else effectMessage = "";
        
        messageTimer = MESSAGE_DURATION;
        shakeTimer = 20; 
        gamePanel.playSoundEffect("res/sound/hit.wav");
    }

    private void enemyAttackAction() {
        List<Move> enemyMoves = enemyPokemon.getMoves();
        if (enemyMoves.isEmpty()) return;

        subState = SubState.MESSAGE_ONLY;
        Move move = enemyMoves.get((int) (Math.random() * enemyMoves.size()));

        int atkStat = 0;
        int defStat = 0;
        
        if (move.getCategory() == Move.Category.PHYSICAL) {
            atkStat = enemyPokemon.getAttack();
            defStat = playerPokemon.getDefense();
        } else {
            atkStat = enemyPokemon.getSpAtk();
            defStat = playerPokemon.getSpDef();
        }

        double abilityMod = 1.0;
        if (enemyPokemon.getCurrentHp() <= enemyPokemon.getMaxHp() / 3) {
            String pType = enemyPokemon.getType1();
            String mType = move.getType();
            if (pType.equals("FIRE") && mType.equals("FIRE")) abilityMod = 1.5;
            else if (pType.equals("WATER") && mType.equals("WATER")) abilityMod = 1.5;
            else if (pType.equals("GRASS") && mType.equals("GRASS")) abilityMod = 1.5;
        }

        double stab = TypeChart.getStabMultiplier(enemyPokemon, move);
        double typeMod = TypeChart.getEffectiveness(move.getType(), playerPokemon.getType1());
        if (playerPokemon.getType2() != null) {
            typeMod *= TypeChart.getEffectiveness(move.getType(), playerPokemon.getType2());
        }

        int damage = BattleMechanics.calculateDamage(
            enemyPokemon.getLevel(), atkStat, defStat, move.getPower(), stab, typeMod, abilityMod
        );

        playerPokemon.setCurrentHp(playerPokemon.getCurrentHp() - damage);
        if (playerPokemon.getCurrentHp() < 0) playerPokemon.setCurrentHp(0);

        battleMessage = "Wild " + enemyPokemon.getName() + " used " + move.getName() + "!";        
        if (typeMod > 1.0) effectMessage = "It's super effective!";
        else if (typeMod < 1.0 && typeMod > 0) effectMessage = "It's not very effective...";
        else if (typeMod == 0.0) effectMessage = "It had no effect...";
        else effectMessage = "";
        
        messageTimer = MESSAGE_DURATION;
        gamePanel.playSoundEffect("res/sound/hit.wav");
    }
    
    private void handleBagInput() {
        int itemCount = inventory.size();
        if (itemCount == 0) {
            if (keyHandler.backPressed) changeSubState(SubState.MAIN_MENU);
            return;
        }

        if (keyHandler.upPressed) {
            keyHandler.upPressed = false;
            if (selectedItem > 0) {
                selectedItem--; 
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.downPressed) {
            keyHandler.downPressed = false;
            if (selectedItem < itemCount - 1) {
                selectedItem++;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            gamePanel.playSoundEffect("res/sound/select.wav"); 
            applyItemEffect(inventory.get(selectedItem));
        }
        if (keyHandler.backPressed) {
            keyHandler.backPressed = false;
            changeSubState(SubState.MAIN_MENU);
        }
    }
    
    private void applyItemEffect(Item item) {
        if (item.getQuantity() <= 0) return;

        item.use(); 
        
        SaveManager.consumeItem(item.getName());
        
        if (item.getQuantity() <= 0) {
            inventory.remove(item);
            if (selectedItem > 0) {
                selectedItem--; 
            }
        }

        subState = SubState.MESSAGE_ONLY;
        messageTimer = MESSAGE_DURATION;
        
        if (item.getName().toLowerCase().contains("potion")) {
            battleMessage = "Used " + item.getName().toUpperCase() + "!";
            playerPokemon.setCurrentHp(playerPokemon.getCurrentHp() + item.getEffectValue());
            if (playerPokemon.getCurrentHp() > playerPokemon.getMaxHp()) 
                playerPokemon.setCurrentHp(playerPokemon.getMaxHp());
            
            turnStep = 0; 
            justSwitched = false; 
        }
        else if (item.getName().toLowerCase().contains("ball")) {
            battleMessage = "You threw a " + item.getName().toUpperCase() + "!";
            messageTimer = 45; 
            
            String ballName = item.getName().toLowerCase();
            if (ballName.contains("ultra")) {
                currentBallImg = ultraballImg;
                catchSuccess = Math.random() < 0.8; 
            } else if (ballName.contains("great")) {
                currentBallImg = greatballImg;
                catchSuccess = Math.random() < 0.5;
            } else {
                currentBallImg = pokeballImg;
                catchSuccess = Math.random() < 0.3;
            }
            
            double catchChance = 1.0 - ((double) enemyPokemon.getCurrentHp() / enemyPokemon.getMaxHp());
            if (ballName.contains("great")) catchChance *= 1.5; 
            else if (ballName.contains("ultra")) catchChance *= 2.0; 
            if (catchChance < 0.2) catchChance = 0.2; 
            
            catchSuccess = Math.random() < catchChance; 
            
            ballStartX = 250;  
            ballStartY = GamePanel.SCREEN_HEIGHT - 450; 
            ballTargetX = GamePanel.SCREEN_WIDTH - 350; 
            ballTargetY = 250;
            
            ballX = ballStartX;
            ballY = ballStartY;
            
            subState = SubState.CATCH_ANIMATION;
            catchPhase = -1; 
            catchTimer = 0;
            ballAngle = 0;
        }
        else if (item.getName().toLowerCase().contains("speed")) {
            playerPokemon.setSpeed(playerPokemon.getSpeed() + item.getEffectValue());
            battleMessage = playerPokemon.getName() + "'s SPEED rose sharply!";
            
            turnStep = 0;
            justSwitched = false;
        }
    }

    private void handlePartyInput() {
        int partySize = playerParty.size();
        int maxRows = 9; 
        
        if (keyHandler.upPressed) { 
            keyHandler.upPressed = false; 
            if (selectedPartyIndex % maxRows != 0) {
                selectedPartyIndex--; 
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.downPressed) { 
            keyHandler.downPressed = false; 
            if (selectedPartyIndex % maxRows != (maxRows - 1) && selectedPartyIndex + 1 < partySize) {
                selectedPartyIndex++; 
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.leftPressed) {
            keyHandler.leftPressed = false;
            if (selectedPartyIndex - maxRows >= 0) {
                selectedPartyIndex -= maxRows;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }
        if (keyHandler.rightPressed) {
            keyHandler.rightPressed = false;
            if (selectedPartyIndex + maxRows < partySize) {
                selectedPartyIndex += maxRows;
                gamePanel.playSoundEffect("res/sound/select.wav");
            } else if (selectedPartyIndex != partySize - 1) {
                selectedPartyIndex = partySize - 1;
                gamePanel.playSoundEffect("res/sound/select.wav");
            }
        }

        if (keyHandler.actionPressed) {
            keyHandler.actionPressed = false;
            gamePanel.playSoundEffect("res/sound/select.wav"); 
            Pokemon chosenPokemon = playerParty.get(selectedPartyIndex);
            
            if (chosenPokemon.isFainted()) {
                battleMessage = chosenPokemon.getName() + " has no energy left!";
                messageTimer = 60;
                subState = SubState.MESSAGE_ONLY;
            } 
            else if (chosenPokemon.getPokeId() == playerPokemon.getPokeId()) {
                battleMessage = chosenPokemon.getName() + " is already in battle!";
                messageTimer = 60;
                subState = SubState.MESSAGE_ONLY;
            } 
            else {
                battleMessage = "Come back, " + playerPokemon.getName() + "! Go, " + chosenPokemon.getName() + "!";
                messageTimer = 90;
                
                boolean wasFainted = playerPokemon.isFainted();
                playerPokemon = chosenPokemon;
                isFaintHandled = false; 
                introCounter = 0; 
                subState = SubState.MESSAGE_ONLY;
                
                if (wasFainted) {
                    justSwitched = false;
                    blockSwitching = false;
                    turnStep = 0;
                } else {
                    justSwitched = true;
                    blockSwitching = true; 
                    turnStep = 0;
                }
            }
        }
        
        if (keyHandler.backPressed) { 
            keyHandler.backPressed = false;
            if(!playerPokemon.isFainted()) {
                changeSubState(SubState.MAIN_MENU); 
            }
        }
    }

    private void handleVictory() {
        int expGained = enemyPokemon.getLevel() * 20;
        int currentExp = playerPokemon.getExp() + expGained;
        playerPokemon.gainExp(expGained); 
        int expNeeded = playerPokemon.getLevel() * 100;
        
        int moneyGained = enemyPokemon.getLevel() * 100; 
        SaveManager.playerMoney += moneyGained; 
        
        battleMessage = enemyPokemon.getName() + " fainted! Gained " + expGained + " EXP.";
        effectMessage = "Gained $" + moneyGained + "!";
        
        if (currentExp >= expNeeded) {
            int newLevel = playerPokemon.getLevel() + 1;
            playerPokemon.setLevel(newLevel); 
            playerPokemon.setExp(currentExp - expNeeded); 
            
            playerPokemon.setMaxHp(playerPokemon.getMaxHp() + 5);
            playerPokemon.setAttack(playerPokemon.getAttack() + 2);
            playerPokemon.setDefense(playerPokemon.getDefense() + 2);
            playerPokemon.setSpeed(playerPokemon.getSpeed() + 2);
            
            playerPokemon.setCurrentHp(playerPokemon.getMaxHp()); 
            
            battleMessage = playerPokemon.getName() + " grew to Level " + newLevel + "!";
            effectMessage = "Gained $" + moneyGained + " & HP Fully Restored!";
        }
        
        messageTimer = 0; 
        phase = Phase.BATTLE_END;
        
        savePartyToDatabase(); 
    }
    
    private void handleCatchAnimation() {
        if (messageTimer > 0) return; 
        
        if (catchPhase == -1) {
            catchPhase = 0; 
            gamePanel.playSoundEffect("res/sound/throw.wav"); 
        }
        
        catchTimer++;
        
        if (catchPhase == 0) { // MELAYANG
            int duration = 60; // 1 detik terbang
            double t = (double) catchTimer / duration;
            if (t >= 1.0) { t = 1.0; catchPhase = 1; catchTimer = 0; }
            ballX = (int) (ballStartX + (ballTargetX - ballStartX) * t);
            int arcHeight = (int) (Math.sin(t * Math.PI) * 200); 
            ballY = (int) (ballStartY + (ballTargetY - ballStartY) * t) - arcHeight;
            ballAngle += 0.3; 
        } 
        else if (catchPhase == 1) { // KESEDOT
            if (catchTimer == 1) gamePanel.playSoundEffect("res/sound/poof.wav"); 
            // Tunggu 1 detik (60 frame) biar suara poof selesai
            if (catchTimer > 60) { catchPhase = 2; catchTimer = 0; }
        } 
        else if (catchPhase == 2) { // JATUH
            ballY += 12;
            if (ballY >= ballTargetY + 150) { 
                ballY = ballTargetY + 150; 
                catchPhase = 3; 
                catchTimer = 0;
                ballAngle = 0;
            }
        } 
        else if (catchPhase == 3) { // GOYANG
            // Kasih jeda antar goyangan lumayan lama (60 frame) biar suara shake selesai
            if (catchTimer == 30 || catchTimer == 90 || catchTimer == 150) {
                ballX += (catchTimer % 60 == 0) ? -15 : 15; 
                gamePanel.playSoundEffect("res/sound/shake.wav"); // PASTIKAN GAK ADA hit.wav DI SINI!
            } else if (catchTimer == 40 || catchTimer == 100 || catchTimer == 160) {
                ballX = ballTargetX; 
            }
            
            // Tunggu 1 detik lagi abis goyang terakhir biar menegangkan!
            if (catchTimer > 220) {
                catchPhase = 4;
                catchTimer = 0;
            }
        } 
        else if (catchPhase == 4) { // HASIL
            if (catchSuccess) {
                gamePanel.playSoundEffect("res/sound/caught.wav"); 
                boolean success = SaveManager.catchPokemon(enemyPokemon.getPokeId(), enemyPokemon.getLevel(), enemyPokemon.getCurrentHp());
                if (success) battleMessage = "Gotcha! " + enemyPokemon.getName().toUpperCase() + " was caught!";
                else battleMessage = "The Ball broke due to a database error!";
                phase = Phase.BATTLE_END; 
            } else {
                gamePanel.playSoundEffect("res/sound/break.wav"); 
                battleMessage = "Oh no! The wild " + enemyPokemon.getName() + " broke free!";
                turnStep = 0;
                justSwitched = false;
            }
            
            subState = SubState.MESSAGE_ONLY;
            // --- INI KUNCINYA COO! Set timer jadi -1 biar game nunggu lu nekan ENTER ---
            messageTimer = -1; 
        }
    }
    
    private void flee() {
        battleMessage = "Got away safely!";
        messageTimer = MESSAGE_DURATION;
        phase = Phase.BATTLE_END;
        savePartyToDatabase(); 
    }

    private void changeSubState(SubState newState) {
        this.subState = newState;
        keyHandler.actionPressed = false;
        keyHandler.backPressed = false;
        keyHandler.upPressed = false;
        keyHandler.downPressed = false;
        
        this.selectedMove = 0; 
        
        this.effectMessage = "";
    }
    
    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        if (introCounter >= MAX_INTRO_TIME) {
            int enemyCardX = GamePanel.SCREEN_WIDTH - 440;
            renderStatusCard(g2d, enemyCardX, 40, enemyPokemon, true);
        }
        
        renderSprites(g2d);

        int bottomY = GamePanel.SCREEN_HEIGHT - 130;
        int boxX = 20;
        int boxW = GamePanel.SCREEN_WIDTH - 40; 

        if (!battleMessage.isEmpty()) {
            
            String renderMsg1 = "";
            String renderMsg2 = "";
            int len1 = battleMessage.length();
            
            if (charIndex <= len1) {
                renderMsg1 = battleMessage.substring(0, charIndex);
            } else {
                renderMsg1 = battleMessage;
                int effectProgress = charIndex - len1;
                renderMsg2 = effectMessage.substring(0, Math.min(effectMessage.length(), effectProgress));
            }

            hud.renderDialogBox(g2d, renderMsg1, renderMsg2, pokemonFont);
            
            // --- FIX: PANAH KEDIP MUNCUL JUGA PAS NUNGGU ENTER BUAT TANGKAPAN ---
            if ((phase == Phase.BATTLE_END && messageTimer == 0) || messageTimer == -1) {
                if (System.currentTimeMillis() % 1000 < 500) {
                    g2d.setColor(Color.WHITE); 
                    int arrowX = GamePanel.SCREEN_WIDTH - 60;
                    int arrowY = GamePanel.SCREEN_HEIGHT - 45;
                    int[] xPoints = {arrowX, arrowX + 16, arrowX + 8}; 
                    int[] yPoints = {arrowY, arrowY, arrowY + 12};     
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            }
            
        } else {
            boxW = 400; 
            
            g2d.setColor(new Color(30, 30, 30, 250));
            g2d.fillRoundRect(boxX, bottomY, boxW, 110, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new java.awt.BasicStroke(3));
            g2d.drawRoundRect(boxX, bottomY, boxW, 110, 15, 15);
            
            renderStatusCard(g2d, boxX, bottomY, playerPokemon, false);
        }

        if (messageTimer <= 0 && introCounter >= MAX_INTRO_TIME) {
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
                hud.renderPartyMenu(g2d, playerParty, selectedPartyIndex, pokemonFont);
            }
        } 
        
        if (isExiting) {
            float finalAlpha = (float) exitCounter / MAX_EXIT_TIME;
            if (finalAlpha > 1f) finalAlpha = 1f; 

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, finalAlpha));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void renderSprites(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int playerSpriteSize = 600; 
        int enemySpriteSize = (int) (playerSpriteSize * 0.675); 

        int pTargetX = 60;
        int pTargetY = GamePanel.SCREEN_HEIGHT - playerSpriteSize - 130; 
        int eTargetX = GamePanel.SCREEN_WIDTH - enemySpriteSize - 200; 
        int eTargetY = 120; 

        if (subState == SubState.MESSAGE_ONLY && phase == Phase.PLAYER_TURN && !enemyPokemon.isFainted() && introCounter >= MAX_INTRO_TIME) {
            pTargetX += 40; 
        }

        if (shakeTimer > 0 && phase == Phase.PLAYER_TURN && introCounter >= MAX_INTRO_TIME) {
            eTargetX += (int)(Math.random() * 10) - 5;
            eTargetY += (int)(Math.random() * 10) - 5;
        }

        int pCurrentX = pTargetX;
        int eCurrentX = eTargetX;

        if (introCounter < MAX_INTRO_TIME) {
            double progress = (double) introCounter / MAX_INTRO_TIME;
            double easeOut = 1 - Math.pow(1 - progress, 3); 

            int pStartX = -playerSpriteSize; 
            int eStartX = GamePanel.SCREEN_WIDTH + enemySpriteSize; 

            pCurrentX = (int) (pStartX + (pTargetX - pStartX) * easeOut);
            eCurrentX = (int) (eStartX + (eTargetX - eStartX) * easeOut);
        }

        g2d.setColor(new Color(0, 0, 0, 40)); 
        g2d.fillOval(pCurrentX + 150, pTargetY + playerSpriteSize - 120, 300, 60); 
        g2d.fillOval(eCurrentX + 100, eTargetY + enemySpriteSize - 50, 220, 45); 

        java.awt.image.BufferedImage pImg = SpriteLoader.loadPokemon(playerPokemon.getPokeId()); 
        if (pImg != null) {
            g2d.drawImage(pImg, pCurrentX, pTargetY, playerSpriteSize, playerSpriteSize, null);
        }

        java.awt.image.BufferedImage eImg = SpriteLoader.loadPokemon(enemyPokemon.getPokeId());
        
        boolean showEnemy = true;
        
        if (subState == SubState.CATCH_ANIMATION && catchPhase >= 1) {
            showEnemy = false; 
        }
        else if (phase == Phase.BATTLE_END && catchSuccess) {
            showEnemy = false; 
        }
        
        if (eImg != null && showEnemy) { 
            g2d.drawImage(eImg, eCurrentX, eTargetY, enemySpriteSize, enemySpriteSize, null);
        }

        if (subState == SubState.CATCH_ANIMATION && catchPhase >= 0 && catchPhase < 4) {
            if (currentBallImg != null) {
                int bSize = 50; 

                java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

                if (catchPhase == 0) {
                    g2d.rotate(ballAngle, ballX + (bSize / 2), ballY + (bSize / 2));
                } 
                else if (catchPhase == 3) {
                    double wobbleAngle = 0;
                    if (catchTimer >= 30 && catchTimer <= 40) wobbleAngle = -0.3; 
                    if (catchTimer >= 90 && catchTimer <= 100) wobbleAngle = 0.3;  
                    if (catchTimer >= 150 && catchTimer <= 160) wobbleAngle = -0.3; 
                    
                    g2d.rotate(wobbleAngle, ballX + (bSize / 2), ballY + bSize);
                }

                g2d.drawImage(currentBallImg, ballX, ballY, bSize, bSize, null);
                
                g2d.setTransform(oldTransform);
            }
        }
    }
    
    private void renderStatusCard(Graphics2D g2d, int x, int y, Pokemon pokemon, boolean isEnemy) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (isEnemy) {
            g2d.setColor(new Color(30, 30, 30, 250)); 
            g2d.fillRoundRect(x, y, 400, 110, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new java.awt.BasicStroke(3));
            g2d.drawRoundRect(x, y, 400, 110, 15, 15);
        }

        String pName = pokemon.getName().toUpperCase();
        if (pName.length() > 10) {
            pName = pName.substring(0, 9) + "..";
        }

        g2d.setFont(pokemonFont.deriveFont(14f));
        g2d.setColor(Color.WHITE);
        g2d.drawString(pName, x + 40, y + 45);
        g2d.setFont(pokemonFont.deriveFont(12f));
        g2d.drawString("Lv " + pokemon.getLevel(), x + 280, y + 45);

        g2d.setFont(pokemonFont.deriveFont(10f));
        g2d.setColor(new Color(255, 225, 0)); 
        g2d.drawString("HP", x + 40, y + 72);

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(x + 80, y + 60, 250, 14);

        double hpPercent = (double) pokemon.getCurrentHp() / pokemon.getMaxHp();
        if (hpPercent < 0) hpPercent = 0; 
        int barWidth = (int) (hpPercent * 250);
        
        if (hpPercent > 0.5) g2d.setColor(new Color(65, 225, 65));
        else if (hpPercent > 0.2) g2d.setColor(Color.YELLOW);
        else g2d.setColor(new Color(255, 60, 60));
        
        g2d.fillRect(x + 80, y + 60, barWidth, 14);

        g2d.setFont(pokemonFont.deriveFont(14f));
        g2d.setColor(Color.WHITE);
        String hpText = pokemon.getCurrentHp() + "/" + pokemon.getMaxHp();
        int textW = g2d.getFontMetrics().stringWidth(hpText);
        g2d.drawString(hpText, x + 330 - textW, y + 95);
    }
}