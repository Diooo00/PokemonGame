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
            System.err.println("Gagal memuat file: " + path);
            return null;
        }

        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage loadPokemon(String name) {
        return loadSprite("/pokemon/" + name.toLowerCase() + ".png");
    }
}