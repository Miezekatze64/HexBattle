package com.mieze.hexbattle.characters;

import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.Player;

import com.mieze.hexbattle.hex.Layout;

import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;

import com.mieze.hexbattle.fields.Field;

import com.mieze.hexbattle.fields.building.City;

public class SwordsmanCharacter extends GameCharacter {
	public static final int PRICE = 4;
    public static final int[] RESOURCES = new int[]{0, 0, 1, 0};
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/swordsman.png");
		toolkit.getImage("assets/city_1.png");
	}

	public SwordsmanCharacter(Field field, Layout hexLayout, Player player) {
		super(field, hexLayout, player);
	}

	@Override
	public int getMovementLength() {
		return 1;
	}

	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		Field field = map.getField(position);
		if (field.hasBuilding()) {
			if (field.getBuilding() instanceof City && field.getOwner() != player) {
				if (!toolbar.hasButton("Conquer city"))
				toolbar.add(new ToolbarButton("Conquer city", conquer_city, "Add this city to your empire.") {
					@Override
					public void onClick() {
						player.conquerCity(position);
						player.reset();
					}
				});
			}
		}
	}

	@Override
	public int getInitialLife() {
		return 10;
	}
	
	
	@Override
	public int getAttackScore() {
		return 5;
	}
		
	@Override
	public int getDefenceScore() {
		return 2;
	}
}
