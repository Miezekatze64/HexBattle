package com.mieze.hexbattle.characters;

import java.awt.Image;
import java.awt.Toolkit;

public class BuilderCharacter implements CharacterData {
	private static Image build_city;
	private static Image build_mine;
	public static final int PRICE = 2;
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/builder.png");

		build_city = toolkit.getImage("assets/city_1.png");
		build_mine = toolkit.getImage("assets/mine.png");
	}

	@Override
	public Image getImage() {
		return BuilderCharacter.img;
	}

/*	public BuilderCharacter(Field field, Layout hexLayout, Player player) {
		super(field, hexLayout, player);
	}*/

	@Override
	public int getMovementLength() {
		return 1;
	}
/*
	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		Field field = map.getField(position);
		if (field.hasBuilding()) {
			if (field.getBuilding() instanceof Village) {
				if (!toolbar.hasButton("Build city"))
				toolbar.add(new ToolbarButton("Build city", build_city, "Turn this lonely village into a city of your empire!") {
					@Override
					public void onClick() {
						player.conquerCity(position);
						player.reset();
					}
				});
			} else if (field.getBuilding() instanceof City && field.getOwner() != player) {
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
		if (!field.hasBuilding() && field instanceof MountainField && field.getOwner() == player) {
			if (!toolbar.hasButton("Build mine"))
			toolbar.add(new ToolbarButton("Build mine", build_mine, "This mine can contain coal (50%), iron (30%) or diamonds (20%).\nEach mine has a unique amount of items and a unique chance (0 -> 100%) to get the items at each time mining.") {
				@Override
				public void onClick() {
					final int[] res = new int[4];
					res[Inventory.WOOD] = 3;
					if (player.payResourses(res)) {
						field.setBuilding(new Mine(field));

						Hex h = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_BUILD_MINE, h.q+","+h.r+","+h.s+";"+((Mine)field.getBuilding()).getType()));

						player.reset();
					} else {
						JOptionPane.showMessageDialog(null, "You need at least 3 Wood to buy this", "Not enough resources", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
	}
*/
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

	@Override
	public String getID() {
		return "builder";
	}
}
