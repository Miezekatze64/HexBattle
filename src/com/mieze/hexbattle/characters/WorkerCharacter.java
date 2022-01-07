package com.mieze.hexbattle.characters;

import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.Main;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;

import com.mieze.hexbattle.server.Client.Event;

import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;

import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.ForestField;
import com.mieze.hexbattle.fields.MountainField;

import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Mine;

public class WorkerCharacter extends GameCharacter {
	private static Image chop_wood;
	public static final int PRICE = 2;
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/worker.png");
		chop_wood = toolkit.getImage("assets/item_wood.png");
	}

	public WorkerCharacter(Field field, Layout hexLayout, Player player) {
		super(field, hexLayout, player);
	}

	@Override
	public int getMovementLength() {
		return 1;
	}

	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		Field field = map.getField(position);
		if (field instanceof ForestField) {
			if (!toolbar.hasButton("Chop wood") && field.getOwner() == player)
			toolbar.add(new ToolbarButton("Chop wood", chop_wood, "Chop one tree of this forest.") {
				@Override
				public void onClick() {
					((ForestField)field).chop();
					Hex h = field.getHex();
					Main.client.sendEvent(new Event(Event.EVENT_GAME_CHOP_WOOD, h.q+","+h.r+","+h.s));

					player.addResourses(new int[]{1, 0, 0, 0});
					player.reset();
				}
			});
		}
		if (field.hasBuilding() && field instanceof MountainField && field.getOwner() == player) {
			final Mine mine = ((Mine)field.getBuilding());
			if (!toolbar.hasButton("Mine!"))
			toolbar.add(new ToolbarButton("Mine!", mine.getImage(), "You have a chance of " + ((int)(mine.getChance()*1000))/10.0 + "% to get " + mine.getAmount() + " items of " + mine.getTypeString() + ".") {
				@Override
				public void onClick() {
					player.addResourses(mine.mine());
					player.reset();
				}
			});
		}
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
