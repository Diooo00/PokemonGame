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

    public int worldX, worldY;
    public int speed;

    // Variabel untuk Collision & Gerak
    public String direction = "DOWN";
    public boolean collisionOn = false;

    // Hitbox
    public Rectangle hitbox;
    public int hitboxDefaultX, hitboxDefaultY;

    public Entity(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        hitbox = new Rectangle(8, 16, 16, 16);
        hitboxDefaultX = hitbox.x;
        hitboxDefaultY = hitbox.y;
    }

    public abstract void update();
    // Gunakan Graphics2D di sini
    public void render(Graphics2D g2d, int cameraX, int cameraY) {
    }
}
