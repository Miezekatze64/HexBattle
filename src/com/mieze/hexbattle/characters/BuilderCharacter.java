package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.*;

public class BuilderCharacter extends GameCharacter {
	private static Image build_city;
	private static Image build_mine;
	public static final int PRICE = 2;
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/builder.png");
				
		build_city = toolkit.getImage("assets/city_1.png");
		
		build_mine = toolkit.getImage("assets/worker.png");
	}

	public BuilderCharacter(Field field, Layout hexLayout, Player player) {
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
			if (field.getBuilding() instanceof Village) {
				if (!toolbar.hasButton("Build village"))
				toolbar.add(new ToolbarButton("Build village", build_city) {
					@Override
					public void onClick() {
						player.conquerCity(position);
						player.reset();
					}
				});
			} else if (field.getBuilding() instanceof City && field.getOwner() != player) {
				if (!toolbar.hasButton("Conquer city"))
				toolbar.add(new ToolbarButton("Conquer city", conquer_city) {
					@Override
					public void onClick() {
						player.conquerCity(position);
						player.reset();
					}
				});
			} else if (!field.hasBuilding() && field instanceof MountainField && field.getOwner() == player) {
				if (!toolbar.hasButton("Build mine"))
				toolbar.add(new ToolbarButton("Build mine", build_mine) {
					@Override
					public void onClick() {
//						player.conquerCity(position);
//						player.reset();
					}
				});
			}
		}
	}

	@Override
	public int getInitialLife() {
		return 5;
	}
	
	
	@Override
	public int getAttackScore() {
		return 3;
	}
		
	@Override
	public int getDefenceScore() {
		return 1;
	}
}
