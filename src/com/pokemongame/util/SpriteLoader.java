/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
/**
 *
 * @author thety
 */
public class SpriteLoader {
    
    public static BufferedImage loadSprite(String path) {
        // NetBeans biasanya mencari dari root classloader
        InputStream is = SpriteLoader.class.getResourceAsStream(path);
        
        if (is == null) {
            // Coba alternatif path tanpa slash di depan jika gagal
            String altPath = path.startsWith("/") ? path.substring(1) : "/" + path;
            is = SpriteLoader.class.getResourceAsStream(altPath);
        }

        if (is == null) {
            // Kita hilangkan print error di sini supaya tidak spam,
            // error-nya kita pindah ke loadPokemon saja.
            return null; 
        }

        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage loadPokemon(int id) {
        // Kemungkinan 1: Folder 'res' ditaruh di dalam 'src'
        String path1 = "/res/pokemon/" + id + ".png";
        BufferedImage img = loadSprite(path1);
        
        // Kemungkinan 2: Folder 'res' sejajar dengan 'src' (Source Packages terpisah)
        if (img == null) {
            String path2 = "/pokemon/" + id + ".png";
            img = loadSprite(path2);
        }
        
        // Kemungkinan 3: Tanpa awalan slash sama sekali
        if (img == null) {
            String path3 = "res/pokemon/" + id + ".png";
            img = loadSprite(path3);
        }
        
        // Jika ketiga cara di atas masih gagal
        if (img == null) {
            System.err.println("GAGAL LOAD GAMBAR: File '" + id + ".png' tidak ditemukan di folder resources!");
        }
        
        return img;
    }
}