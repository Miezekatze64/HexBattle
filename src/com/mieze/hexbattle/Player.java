package com.mieze.hexbattle;

import java.awt.*;
import java.util.*;

import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.characters.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;

public class Player {
	public Map map;
	private ArrayList<GameCharacter> characters;
	private ArrayList<Hex> fields;
	private ArrayList<Field> unexplored;
	private ArrayList<Hex> active;

	private Hex start_pos;
	private Layout hexLayout;

	public static final int STATE_START = 0;
	public static final int STATE_CHARACTER_CLICKED = 1;
	public static final int STATE_NOT_IMPLEMENTED = 2;

	private int state = STATE_START;
	private GameCharacter clickedCharacter;

	public Player(Map map, Layout layout) {
		this.fields = new ArrayList<Hex>();
		this.unexplored = new ArrayList<Field>();
		this.active = new ArrayList<Hex>();
		this.characters = new ArrayList<GameCharacter>();
		this.map = map;
		this.hexLayout = layout;
		this.start_pos = new Hex(0, 0, 0);

		setStartFields();

		characters.add(new BuilderCharacter(map.getField(start_pos), hexLayout, this));
	}

	public void setStartFields() {
		openSurroundedFields(start_pos);
		addField(start_pos, true);
	}

	public void render(Graphics g) {
		for (int i = 0; i < unexplored.size(); i++) {
			unexplored.get(i).render(g, map.offset_x, map.offset_y, map.zoom);
		}

		for (int i = 0; i < map.fields.size(); i++) {
			if (fields.contains(map.fields.get(i).getHex()) && isOnScreen(map.fields.get(i))) {
				map.fields.get(i).render(g, map.offset_x, map.offset_y, map.zoom);
			}
		}

		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).render(g, map.offset_x, map.offset_y, map.zoom);
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
	}

	private boolean isOnScreen(Field f) {
		return f.isOnScreen(map.offset_x, map.offset_y, map.zoom);
	}

	public void onClick(Point p) {
		Point realPoint = map.displayToHex(p);

		Hex hex = hexLayout.pixelToHex(realPoint).hexRound();
		Field f = map.getField(hex);

		
		switch (state) {
		case STATE_START:
			if (f == null) {
				// TODO: handle empty click (maybe...)
			} else {
				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter()) {
						GameCharacter character = f.getCharacter();
						character.setPossibleFields();
						System.out.println("clicked");
						clickedCharacter = character;
						state = STATE_CHARACTER_CLICKED;
						break;
					}
				} else {
					// TODO: handle UnexploredField click
				}
			}
			break;
		case STATE_CHARACTER_CLICKED:
			if (clickedCharacter == null) {
				throw new IllegalStateException(
						"A player connot be click and unexisting at the same time (unreaachable)!!");
			} else if (f == null) {
				// TODO: handle empty click (maybe...)
			} else {
				//check if field is in range
				if (!active.contains(f.getHex())) {
					//wrong field
					break;
				}

				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter()) {
						GameCharacter character = f.getCharacter();
						character.setPossibleFields();
						state = STATE_CHARACTER_CLICKED;
						break;
					} else {
						// move to next field
						clickedCharacter.moveTo(f);
						active.removeAll(active);
						map.getField(clickedCharacter.getPosition()).removeCharacter();
						f.setCharacter(clickedCharacter);
						
						openSurroundedFields(f.getHex());
						state = STATE_START;
						break;
					}
				} else {
					// TODO: handle UnexploredField click
				}
			}
			break;
		}
	}

	public void openSurroundedFields(Hex h) {
		for (int i = 0; i < 6; i++) {
			addField(h.neighbor(i), true);
		}
	}
	
	public void addField(Point p) {
		addField(p, false);
	}

	public void addField(Point p, boolean force) {
		Hex hex = hexLayout.pixelToHex(p).hexRound();
		addField(hex, force);
	}

	public void addField(Hex hex, boolean force) {
		if (force || isUnexplored(hex)) {
			this.fields.add(hex);
			removeUnexploredField(hex);
			map.addField(hex);

			for (int i = 0; i < 6; i++) {
				if (!map.contains(hex.neighbor(i))) {
					addUnexploredField(hex.neighbor(i));
				}
			}
		}
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
		if (!active.contains(h)) {
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

	private void addUnexploredField(Hex h) {
		Hex hex = h;
		if (!map.contains(hex)) {
			unexplored.add(new UnexploredField(hex, map));
		}
	}
}
