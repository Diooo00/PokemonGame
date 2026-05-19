package com.pokemongame.util;

import com.pokemongame.entity.Entity;
import com.pokemongame.main.GamePanel;

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
        int index = 999; 

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null) {
                entity.hitbox.x = entity.worldX + entity.hitbox.x;
                entity.hitbox.y = entity.worldY + entity.hitbox.y;

                target[i].hitbox.x = target[i].worldX + target[i].hitbox.x;
                target[i].hitbox.y = target[i].worldY + target[i].hitbox.y;

                switch (entity.direction) {
                    case "UP":    entity.hitbox.y -= entity.speed; break;
                    case "DOWN":  entity.hitbox.y += entity.speed; break;
                    case "LEFT":  entity.hitbox.x -= entity.speed; break;
                    case "RIGHT": entity.hitbox.x += entity.speed; break;
                }

                if (entity.hitbox.intersects(target[i].hitbox)) {
                    if (target[i] != entity) { 
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                entity.hitbox.x = entity.hitboxDefaultX;
                entity.hitbox.y = entity.hitboxDefaultY;
                target[i].hitbox.x = target[i].hitboxDefaultX;
                target[i].hitbox.y = target[i].hitboxDefaultY;
            }
        }
        return index;
    }
}