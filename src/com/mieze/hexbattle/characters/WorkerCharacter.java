package com.mieze.hexbattle.characters;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.ForestField;
import com.mieze.hexbattle.fields.MountainField;
import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Mine;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.net.Event;

public class WorkerCharacter implements CharacterData {
	private static Image chop_wood;
	public static final int PRICE = 2;
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/worker.png");
		chop_wood = toolkit.getImage("assets/item_wood.png");
	}

	public WorkerCharacter(Field field, Layout hexLayout, Player player) {
//		super(field, hexLayout, player);
	}

	@Override
	public int getMovementLength() {
		return 1;
	}

	public Image getImage() {
		return img;
	}

	// TODO: @Override
	//	public void checkAndAddTools(Toolbar toolbar) {
	public List<Action> getActions() {
		ArrayList<Action> list = new ArrayList<>();

//		Field field = map.getField(position);

		list.add(new Action("chop_wood",
							"Chop wood",
							(f, p) -> "Chop one tree of this forect",
							(f, p) -> chop_wood,
							(field, player) -> field.getOwner() == player && field instanceof ForestField && field.hasBuilding(),
							(field, player) -> {
								((ForestField)field).chop();
								Hex h = field.getHex();
//								Main.getServer().getConnection().sendEvent(new Event(Event.S_GAME_CHOP_WOOD, h.q+","+h.r+","+h.s));

								player.addResourses(new int[]{1, 0, 0, 0});
								player.reset();
								throw new RuntimeException("TODO: here");
							}));

		list.add(new Action("mine",
							"Mine!",
							(field, player) -> {
								final Mine mine = ((Mine)field.getBuilding());
								return "You have a chance of " + ((int)(mine.getChance()*1000))/10.0 + "% to get " + mine.getAmount() + " items of " + mine.getTypeString() + ".";
		                    },
							(field, player) -> {
								final Mine mine = ((Mine)field.getBuilding());
								return mine.getImage();
		                    },
							(field, player) ->field.getOwner() == player && field instanceof MountainField && field.hasBuilding(),
							(field, player) -> {
								final Mine mine = ((Mine)field.getBuilding());
								player.addResourses(mine.mine());
								player.reset();
							}));

		list.add(new Action("conquer_city",
							"Conquer city",
							(f, p) -> "Add this city to your empire",
							(f, p) -> null/*conquer_city*/,
							(field, player) -> field.getOwner() != player && field.getBuilding() instanceof City,
							(field, player) -> {
								player.conquerCity(field.getHex());
								player.reset();
							}));
		return list;
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

	@Override
	public String getID() {
		return "worker";
	}
}
