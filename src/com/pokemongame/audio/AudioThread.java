/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.audio;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
/**
 *
 * @author thety
 */
public class AudioThread extends Thread {

    private String filePath;
    private boolean isLooping;
    private Clip clip;

    // Constructor: Menerima jalur file dan apakah lagu ini mau diulang terus (BGM) atau sekali saja (Effect)
    public AudioThread(String filePath, boolean isLooping) {
        this.filePath = filePath;
        this.isLooping = isLooping;
    }

    // Method run() ini adalah "jantung" dari Thread. Akan berjalan saat kita memanggil .start()
    @Override
    public void run() {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("File audio tidak ditemukan: " + filePath);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Opsional: Mengecilkan volume bawaan agar tidak terlalu berisik (kaget)
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // -10 desibel lebih pelan

            if (isLooping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // BGM: Putar terus menerus
            } else {
                clip.start(); // Sound Effect: Putar sekali
            }

        } catch (Exception e) {
            System.err.println("Gagal memutar audio: " + filePath);
            e.printStackTrace();
        }
    }

    // Method untuk mematikan lagu secara paksa (Misal pas pindah dari Map ke Battle)
    public void stopAudio() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
