package com.mieze.hexbattle;

import java.util.ArrayList;
import java.util.Random;

import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Village;
import com.mieze.hexbattle.hex.*;

public class Map {
	protected ArrayList<Field> fields;
	protected int offset_x = 0;
	protected int offset_y = 0;
	public double zoom = 1;
	private long seed;
	public HexPanel panel;

	private static Layout hexLayout = HexPanel.hexLayout;
	private static final double SMOOTH_FACTOR = 0.3;

	// private static long MAX_SIZE = 10000;
	private OpenSimplexNoise noise;

	public static Layout getLayout() {
		return hexLayout;
	}

	public Map(int offset_x, int offset_y, HexPanel panel) {
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.seed = new Random(System.currentTimeMillis()).nextLong();
		this.fields = new ArrayList<Field>();
		this.panel = panel;

		noise = new OpenSimplexNoise(seed);

		// addField(new Point(0, 0), true);

	}

	public Point hexToDisplay(Point p) {
		int x = (int) ((p.x - offset_x - panel.getWidth() / 2) * zoom + panel.getWidth() / 2);
		int y = (int) ((p.y - offset_y - panel.getHeight() / 2) * zoom + panel.getHeight() / 2);
		return new Point(x, y);
	}

	public Point displayToHex(Point p) {

		int x = (int) ((p.x - panel.getWidth() / 2) / zoom + panel.getWidth() / 2 + offset_x);
		int y = (int) ((p.y - panel.getHeight() / 2) / zoom + panel.getHeight() / 2 + offset_y);

		return new Point(x, y);
	}

	/*
	 * public Point offsetPoint(Point p) { return new Point((p.x + offset_x) / zoom,
	 * (p.y + offset_y) / zoom); }
	 */

	public Field getField(Point p) {
		return getField(hexLayout.pixelToHex(p).hexRound());
	}

	public Field getField(Hex hex) {
		// System.out.println(hex);

		for (int i = fields.size()-1; i >= 0; i--) {
			if (fields.get(i).getHex().equals(hex)) {
				return fields.get(i);
			}
		}
		return null;
	}

	public void addOffset(int off_x, int off_y) {
		offset_x -= off_x;
		offset_y -= off_y;
	}

	public void setOffset(int off_x, int off_y) {
		offset_x = off_x;
		offset_y = off_y;
	}

	public void addZoom(double factor) {
		zoom *= factor;
	}

	protected boolean contains(Hex hex) {
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getHex().equals(hex)) {
				return true;
			}
		}
		return false;
	}

	public void addField(Hex hex) {
		addField(hex, getType(hex));
	}
	
	public void addField(Hex hex, int type) {
		Field add;
		if (!contains(hex)) {
			switch(type) {
			default:
			case Field.EMPTY:
				add = new EmptyField(hex, this);
				if (Math.random()*100 < 40) {
					boolean village = true;
					for (int i = 0; i < 6; i++) {
						if (getField(hex.neighbor(i)) != null && getField(hex.neighbor(i)).hasBuilding() && (getField(hex.neighbor(i)).getBuilding() instanceof City || getField(hex.neighbor(i)).getBuilding() instanceof Village)) {
							village = false;
						}
					}
					if (village) {
						add.setBuilding(new Village(add));
					}
				}
				break;
			case Field.WATER:
				add = new WaterField(hex, this);
				break;
			case Field.MOUNTAIN:
				add = new MountainField(hex, this);
				break;
			case Field.FOREST:
				add = new ForestField(hex, this);
				break;
			}

			this.fields.add(add);
		}
	}

	public void overrideFieldType(Hex hex, int type) {
		Field field = getField(hex);
		Player owner = field.getOwner();
		GameCharacter character = field.getCharacter();

		fields.remove(field);
		addField(hex, type);

		getField(hex).setOwner(owner);
		getField(hex).setCharacter(character);
	}

	int getType(Hex hex) {
		OffsetCoord oc = OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, hex);
		double val = noise.eval(oc.row * SMOOTH_FACTOR, oc.col * SMOOTH_FACTOR);
		int type_val = Math.round((float) val * 10);

		if (type_val < -4) {
			// lake / oceans / rivers / ...
			return Field.WATER;
		} else if (type_val < 3) {
			// grass
			return Field.EMPTY;
		} else if (type_val < 5) {
			// forests
			return Field.FOREST;
		} else if (type_val < 9) {
			// mountains
			return Field.MOUNTAIN;
		} else {
			//grass
			return Field.EMPTY;
		}
	}

	public int getWidth() {
		return panel.getWidth();
	}

	public int getHeight() {
		return panel.getHeight();
	}

}
