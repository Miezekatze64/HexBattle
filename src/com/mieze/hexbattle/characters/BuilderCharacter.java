package com.mieze.hexbattle.characters;

import java.awt.*;
import javax.swing.JOptionPane;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.*;
import com.mieze.hexbattle.toolbars.*;


public class BuilderCharacter extends GameCharacter {
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

	public BuilderCharacter(Field field, Layout hexLayout, Player player) {
		super(field, hexLayout, player);
	}

	@Override
	public int getMovementLength() {
		return 1;
	}

	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		//System.out.println("clicked");
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
						player.reset();
					} else {
						JOptionPane.showMessageDialog(null, "You need at least 3 Wood to buy this", "Not enough resources", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
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
