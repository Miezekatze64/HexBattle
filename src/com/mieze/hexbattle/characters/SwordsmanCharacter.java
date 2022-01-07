package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.*;

public class SwordsmanCharacter extends GameCharacter {
	public static final int PRICE = 4;
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
