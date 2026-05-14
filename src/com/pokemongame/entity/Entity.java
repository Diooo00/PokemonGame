/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.entity;

import com.pokemongame.main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
/**
 *
 * @author thety
 */
public abstract class Entity {

    protected GamePanel gamePanel;

    // Posisi di dunia (world coordinate, bukan layar)
    public int worldX, worldY;
    public int speed;

    // Sprite saat ini yang dirender
    protected BufferedImage currentSprite;

    // Hitbox untuk collision detection
    public Rectangle hitbox;
    public int hitboxDefaultX, hitboxDefaultY;

    public Entity(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        // Hitbox default — bisa di-override tiap subclass
        hitbox = new Rectangle(8, 16, 16, 16);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
    }

    // Wajib diimplementasi — tiap entity punya logika & tampilan sendiri
    public abstract void update();
    public void render(Graphics2D g2d, int cameraX, int cameraY) {
    // default kosong — subclass override sesuai kebutuhan
    }
}
