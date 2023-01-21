package com.mieze.hexbattle;

import java.util.HashSet;
import java.util.Set;

import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Point;

public interface Map {
	public Field getField(Hex hex, boolean createNew);

    public default Field getField(Point p) {
        return getField(HexPanel.hexLayout.pixelToHex(p).hexRound());
    }

    public default Field getField(Hex hex) {
        return getField(hex, true);
    }

    public default Set<Hex> getActive(GameCharacter character) {
        return getActive(character, character.getData().getMovementLength());
    }

    private Set<Hex> getActive(GameCharacter character, int depth) {
        return getActive(character, character.getPosition(), depth);
    }

    private Set<Hex> getActive(GameCharacter character, Hex center, int depth) {
        var list = new HashSet<Hex>();
        for (int i = 0; i < 6; i++) {
            Hex pos = center.neighbor(i);
            if (!character.canWalkOn(this.getField(pos))) continue;
            if (depth > 1) {
                list.addAll(getActive(character, pos, depth - 1));
            } else if (depth == 0) {
                return list;
            } else {
                list.add(pos);
            }
        }
        return list;
    }

    public default boolean canMoveTo(GameCharacter character, Hex hex) {
        return getActive(character).contains(hex);
    }
}
