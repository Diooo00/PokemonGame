package com.pokemongame.main;

import com.pokemongame.pokemon.Pokemon;
import com.pokemongame.state.OverworldState;
import com.pokemongame.util.SaveManager;

import java.awt.Dimension; // <-- PENTING: Import ini buat ngatur batas minimal jendela
import java.util.List; 
import javax.swing.JFrame;

/**
 *
 * @author thety
 */
public class Main {
    
    public static void main(String[] args) {
        JFrame window = new JFrame();
        
        // 1. Tahan jendela biar nggak langsung nutup pas diklik X
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        
        // --- 2. INI KUNCINYA COO: Buka gembok resize! ---
        window.setResizable(true); 
        window.setTitle("Pokemon Game");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        
        // --- 3. Kunci ukuran terkecil biar jendela ga bisa dikecilin sampai ilang ---
        window.setMinimumSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT + 40));

        // 4. Sistem tangkap tombol X buat Auto-Save
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.out.println("Mendeteksi penutupan game... Menyimpan data...");
                
                // Gunakan fungsi getOverworldState() bawaan GamePanel-mu!
                OverworldState overworld = gamePanel.getOverworldState();
                
                // Kalau lagi di map (bukan lagi battle), save koordinat & duit
                if (overworld != null) {
                    SaveManager.saveGame(overworld.getPlayer());
                    
                    // Save HP dan EXP Pokemon di tas juga biar gak kereset!
                    List<Pokemon> party = SaveManager.loadPlayerParty();
                    for(Pokemon p : party) {
                        SaveManager.savePokemonStatus(p);
                    }
                }
                
                // Setelah selesai nge-save, baru matikan programnya
                System.exit(0);
            }
        });

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // 5. Jalankan game loop-nya
        gamePanel.startGameLoop(); 
    }
}