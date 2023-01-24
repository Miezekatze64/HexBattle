package com.mieze.hexbattle.characters;

import java.awt.Image;

import com.mieze.hexbattle.fields.Field;

public interface CharacterData {
    public Image getImage();
    public int getMovementLength();
    public int getInitialLife();
    public int getAttackScore();
    public int getDefenceScore();

    public default boolean canWalkOn(Field field) {
        return field.isWalkable();
    }

    public String getID();
}
