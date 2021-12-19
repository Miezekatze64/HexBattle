package com.mieze.hexbattle;

import java.awt.*;
import java.util.*;

import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.characters.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.toolbars.*;

public class Player {
	public Map map;

	protected ArrayList<GameCharacter> characters;
	protected ArrayList<Hex> fields;
	protected ArrayList<UnexploredField> unexplored;
	private ArrayList<Hex> active;
	private ArrayList<Hex> empire;

	private Toolbar toolbar;
	private Inventory inventory;

	private Hex start_pos;
	private Layout hexLayout;

	public static final int STATE_START = 0;
	public static final int STATE_CHARACTER_CLICKED = 1;
	public static final int STATE_NOT_IMPLEMENTED = 2;

	private int state = STATE_START;
	private Color playerColor;

	private GameCharacter clickedCharacter;

	public Player(Map map, Layout layout, Color color) {
		this.fields = new ArrayList<Hex>();
		this.unexplored = new ArrayList<UnexploredField>();
		this.active = new ArrayList<Hex>();
		this.characters = new ArrayList<GameCharacter>();
		this.empire = new ArrayList<Hex>();

		this.toolbar = new Toolbar();
		this.inventory = new Inventory();
		this.map = map;
		this.playerColor = color;
		this.hexLayout = layout;

		setStartFields();

		characters.add(new BuilderCharacter(map.getField(start_pos), hexLayout, this));
	}

	public void setStartFields() {
		boolean found = false;

		while (!found) {
			int q = (int) (Math.random() * 10);
			int r = (int) (Math.random() * 10);
			int s = -q - r;

			this.start_pos = new Hex(q, r, s);

			if (map.getType(start_pos) == Field.WATER) {
				continue;
			}
			found = true;
			for (int i = 0; i < 6; i++) {
				if (map.getField(start_pos.neighbor(i)) != null && map.getField(start_pos.neighbor(i)).hasOwner()) {
					found = false;
				}
			}
		}

		addField(start_pos, true);
		conquerCity(start_pos);
	}

	private void addToEmpire(Hex hex) {
		empire.add(hex);
		map.getField(hex).setOwner(this);
	}

	public Color getColor() {
		return this.playerColor;
	}

	public void render(Graphics2D g) {

		for (int i = 0; i < map.fields.size(); i++) {
			if (fields.contains(map.fields.get(i).getHex()) && isOnScreen(map.fields.get(i))) {
				map.fields.get(i).render(g);
			}
		}

		for (int i = 0; i < map.fields.size(); i++) {
			if (fields.contains(map.fields.get(i).getHex()) && isOnScreen(map.fields.get(i))) {
				if (map.fields.get(i).getCharacter() != null)
					map.fields.get(i).getCharacter().render(g, map.zoom);
			}
		}

		for (int i = 0; i < unexplored.size(); i++) {
			unexplored.get(i).render(g);
		}

		for (int i = 0; i < active.size(); i++) {

			Hex hex = active.get(i);

			Point p = map.hexToDisplay(hexLayout.hexToPixel(hex));
			int point_x = (int) p.x;
			int point_y = (int) p.y;

			double w = (hexLayout.size.x * map.zoom) / 2;
			double h = (hexLayout.size.x * map.zoom) / 2;

			int left = (int) (point_x - w / 2);
			int top = (int) (point_y - h / 2);

			g.setColor(Color.ORANGE);
			((Graphics2D) g).fillOval(left, top, (int) w, (int) h);
			g.setColor(Color.BLACK);
		}

		if (clickedCharacter == null)
			toolbar.reset();
		else if (!clickedCharacter.isMoved() && state == STATE_CHARACTER_CLICKED) {
			clickedCharacter.checkAndAddTools(toolbar);
			clickedCharacter.setMoved(true);
		}
		toolbar.render(g, map);
		inventory.render(g, map);
	}

	protected boolean isOnScreen(Field f) {
		return f.isOnScreen(map.offset_x, map.offset_y, map.zoom);
	}

	public void onClick(Point p) {
		if (toolbar.onClick((int) p.x, (int) p.y, map)) {
			return;
		}

		Point realPoint = map.displayToHex(p);

		Hex hex = hexLayout.pixelToHex(realPoint).hexRound();
		Field f = map.getField(hex);

		switch (state) {
		case STATE_START:
			if (f == null) {
				// TODO: handle empty click (maybe...)
			} else {
				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter() && f.getCharacter().isFromPlayer(this) && !f.getCharacter().isMoved()) {
						GameCharacter character = f.getCharacter();
						character.setPossibleFields();
						clickedCharacter = character;
						state = STATE_CHARACTER_CLICKED;
						break;
					}
				} else {
					// TODO: handle UnexploredField click
				}
			}
			clickedCharacter = null;
			state = STATE_START;
			break;
		case STATE_CHARACTER_CLICKED:
			if (clickedCharacter == null) {
				throw new IllegalStateException(
						"A player connot be click and unexisting at the same time (unreaachable)!!");
			} else if (f == null) {
				// TODO: handle empty click (maybe...)
			} else {
				// check if field is in range
				if (!active.contains(f.getHex())) {
					clickedCharacter = null;
					state = STATE_START;
					active.removeAll(active);
					break;
				}

				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter()) {
						active.removeAll(active);
						GameCharacter character = f.getCharacter();
						character.setPossibleFields();
						clickedCharacter = character;
						state = STATE_CHARACTER_CLICKED;
						break;
					} else {
						if (f instanceof WaterField) {
							/*
							 * TODO implement this ↓ if (!(f.getCharacter() instanceof BoatCharacter)) {
							 */
							break;
							/* } */
						}

						// move to next field
						clickedCharacter.moveTo(f);
						active.removeAll(active);
						map.getField(clickedCharacter.getPosition()).removeCharacter();
						f.setCharacter(clickedCharacter);
						clickedCharacter.setMoved(true);

						openSurroundedFields(f.getHex());
						state = STATE_START;
						break;
					}
				} else {
					// TODO: handle UnexploredField click
				}
			}
			clickedCharacter = null;
			state = STATE_START;
			active.removeAll(active);
			break;
		}
	}

	public void yourTurn() {
		/* temporarly */
		state = STATE_START;
		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).setMoved(false);
		}

		inventory.addResources(Inventory.CHARPOINTS, 0.5 * getCitiyCount());
	}

	public int getCitiyCount() {
		return 1;
	}

	public void openSurroundedFields(Hex h) {
		for (int i = 0; i < 6; i++) {
			addField(h.neighbor(i), true);
		}
	}

	public void openAndConquerSurroundedFields(Hex h) {
		for (int i = 0; i < 6; i++) {
			addField(h.neighbor(i), true);
			addToEmpire(h.neighbor(i));
		}
		addToEmpire(h);
	}

	public void addField(Point p) {
		addField(p, false);
	}

	public void addField(Point p, boolean force) {
		Hex hex = hexLayout.pixelToHex(p).hexRound();
		addField(hex, force);
	}

	public void addField(Hex hex, boolean force) {
		if ((force || isUnexplored(hex)) && !fields.contains(hex)) {
			this.fields.add(hex);
			removeUnexploredField(hex);
			map.addField(hex);

			for (int i = 0; i < 6; i++) {
				if (!fields.contains(hex.neighbor(i))) {
					addUnexploredField(hex.neighbor(i));
				}
			}
		}
	}

	public void conquerCity(Hex h) {
		map.getField(h).setBuilding(new City(map.getField(h)));
		openAndConquerSurroundedFields(h);
	}

	private boolean isUnexplored(Hex hex) {
		for (int i = 0; i < unexplored.size(); i++) {
			if (unexplored.get(i).getHex().equals(hex)) {
				return true;
			}
		}
		return false;
	}

	public void activate(Hex h) {
		if (!active.contains(h) && !isUnexplored(h) && map.getField(h) != null && (!map.getField(h).hasCharacter())) {
			active.add(h);
		}
	}

	public void deactivate(Hex h) {
		if (active.contains(h)) {
			active.remove(h);
		}
	}

	public void deactivateAll() {
		active.removeAll(active);
	}

	private void removeUnexploredField(Hex hex) {
		for (int i = 0; i < unexplored.size(); i++) {
			if (unexplored.get(i).getHex().equals(hex)) {
				unexplored.remove(i);
			}
		}
	}

	private void addUnexploredField(Hex hex) {
		if (!isUnexplored(hex)) {
			unexplored.add(new UnexploredField(hex, map));
		}
	}
}
