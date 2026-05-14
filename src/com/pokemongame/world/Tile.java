/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.world;

import java.awt.image.BufferedImage;
/**
 *
 * @author thety
 */
public class Tile {
    public BufferedImage image;
    public boolean solid; // true = tidak bisa dilewati

    public Tile(BufferedImage image, boolean solid) {
        this.image = image;
        this.solid = solid;
    }
}
