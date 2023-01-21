package com.mieze.hexbattle.characters;

import java.awt.Image;
import java.awt.Toolkit;

public class Boat implements CharacterData {
	public static final int PRICE = 2;
    public static final int[] RESOURCES = new int[]{2, 2, 1, 0};
	public static Image img;

	private GameCharacter character = null;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/boat.png");
	}
	/*
	public Boat(Field field, Layout hexLayout, Player player) {
		super(field, hexLayout, player);
	}*/

	@Override
	public Image getImage() {
		return Boat.img;
	}

	public void setCharacter(GameCharacter ch) {
		this.character = ch;
	}

	public GameCharacter getCharacter() {
		return this.character;
	}

	public boolean hasCharacter() {
		return this.character != null;
	}

	public void removeCharacter() {
		this.character = null;
	}

/*	@Override
	public void setPossibleFields(Hex h, int count) {
		if (count < getMovementLength()) {
			for (int n = 0; n < 6; n++) {
				if (!h.neighbor(n).equals(position)) {
					if (map.getField(h.neighbor(n)) instanceof WaterField) {
						player.activate(h.neighbor(n));
					} else {
						continue;
					}
				}
			}
			count++;
			for (int n = 0; n < 6; n++) {
				if (player.active.contains(h.neighbor(n))) setPossibleFields(h.neighbor(n), count);
			}
		}
	}
*/
	@Override
	public int getMovementLength() {
		return 2;
	}

/*	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		if (hasCharacter()) {
			if (!toolbar.hasButton("Leave boat"))
			toolbar.add(new ToolbarButton("Leave boat", Boat.img, "If you leave your boat it has 0 attack and 1 defence score until you enter it again. You can only enter a boat at ports.") {
				@Override
				public void onClick() {
					player.leaveBoat(map.getField(position));
				}
			});
		}
*/

	@Override
	public int getInitialLife() {
		return 5;
	}
	
	@Override
	public int getAttackScore() {
		if (hasCharacter()) return getCharacter().getData().getAttackScore();
		return 0;
	}
		
	@Override
	public int getDefenceScore() {
		if (hasCharacter()) return getCharacter().getData().getDefenceScore();
		return 1;
	}

	@Override
	public String getID() {
		return "boat";
	}
}
