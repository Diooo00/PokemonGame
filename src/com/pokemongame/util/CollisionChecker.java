/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pokemongame.util;

import com.pokemongame.entity.Entity;
import com.pokemongame.main.GamePanel;

/**
 *
 * @author user
 */
public class CollisionChecker {
    
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.hitbox.x;
        int entityRightWorldX = entity.worldX + entity.hitbox.x + entity.hitbox.width;
        int entityTopWorldY = entity.worldY + entity.hitbox.y;
        int entityBottomWorldY = entity.worldY + entity.hitbox.y + entity.hitbox.height;

        int entityLeftCol = entityLeftWorldX / GamePanel.TILE_SIZE;
        int entityRightCol = entityRightWorldX / GamePanel.TILE_SIZE;
        int entityTopRow = entityTopWorldY / GamePanel.TILE_SIZE;
        int entityBottomRow = entityBottomWorldY / GamePanel.TILE_SIZE;

        // Kita ganti logika pengecekannya pakai fungsi isSolid() dari TileMap baru
        switch (entity.direction) {
            case "UP":
                entityTopRow = (entityTopWorldY - entity.speed) / GamePanel.TILE_SIZE;
                if (gp.getTileMap().isSolid(entityTopRow, entityLeftCol) || 
                    gp.getTileMap().isSolid(entityTopRow, entityRightCol)) {
                    entity.collisionOn = true;
                }
                break;
            case "DOWN":
                entityBottomRow = (entityBottomWorldY + entity.speed) / GamePanel.TILE_SIZE;
                if (gp.getTileMap().isSolid(entityBottomRow, entityLeftCol) || 
                    gp.getTileMap().isSolid(entityBottomRow, entityRightCol)) {
                    entity.collisionOn = true;
                }
                break;
            case "LEFT":
                entityLeftCol = (entityLeftWorldX - entity.speed) / GamePanel.TILE_SIZE;
                if (gp.getTileMap().isSolid(entityTopRow, entityLeftCol) || 
                    gp.getTileMap().isSolid(entityBottomRow, entityLeftCol)) {
                    entity.collisionOn = true;
                }
                break;
            case "RIGHT":
                entityRightCol = (entityRightWorldX + entity.speed) / GamePanel.TILE_SIZE;
                if (gp.getTileMap().isSolid(entityTopRow, entityRightCol) || 
                    gp.getTileMap().isSolid(entityBottomRow, entityRightCol)) {
                    entity.collisionOn = true;
                }
                break;
        }
    }
    
    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999; // 999 artinya tidak ada entity yang kena

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {
                // Dapatkan posisi hitbox entity (player)
                entity.hitbox.x = entity.worldX + entity.hitbox.x;
                entity.hitbox.y = entity.worldY + entity.hitbox.y;

                // Dapatkan posisi hitbox target (NPC)
                target[i].hitbox.x = target[i].worldX + target[i].hitbox.x;
                target[i].hitbox.y = target[i].worldY + target[i].hitbox.y;

                // Prediksi pergerakan
                switch (entity.direction) {
                    case "UP":    entity.hitbox.y -= entity.speed; break;
                    case "DOWN":  entity.hitbox.y += entity.speed; break;
                    case "LEFT":  entity.hitbox.x -= entity.speed; break;
                    case "RIGHT": entity.hitbox.x += entity.speed; break;
                }

                // Cek apakah hitbox bersentuhan
                if (entity.hitbox.intersects(target[i].hitbox)) {
                    if (target[i] != entity) { // Jangan tabrak diri sendiri
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                // Reset hitbox ke posisi awal (penting!)
                entity.hitbox.x = entity.hitboxDefaultX;
                entity.hitbox.y = entity.hitboxDefaultY;
                target[i].hitbox.x = target[i].hitboxDefaultX;
                target[i].hitbox.y = target[i].hitboxDefaultY;
            }
        }
        return index;
    }
}