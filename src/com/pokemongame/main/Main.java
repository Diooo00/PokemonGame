package com.pokemongame.main;

import java.awt.Dimension; 
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

        // 4. Sistem tangkap tombol X buat Auto-Save (SUDAH DIHANCURKAN)
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // HANYA nutup game, ga ada nge-save lagi!
                System.out.println("Mendeteksi penutupan game... Goodbye!");
                System.exit(0);
            }
        });

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // 5. Jalankan game loop-nya
        gamePanel.startGameLoop(); 
    }
}