package com.mieze.hexbattle;

import java.util.ArrayList;
import java.util.Random;

import com.mieze.hexbattle.characters.GameCharacter;

import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.EmptyField;
import com.mieze.hexbattle.fields.WaterField;
import com.mieze.hexbattle.fields.MountainField;
import com.mieze.hexbattle.fields.ForestField;

import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Village;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.hex.OffsetCoord;
import com.mieze.hexbattle.hex.Point;

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
		this(offset_x, offset_y, panel, new Random(System.currentTimeMillis()).nextLong());
	}

	public Map(int offset_x, int offset_y, HexPanel panel, long seed) {
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.fields = new ArrayList<Field>();
		this.panel = panel;
		this.seed = seed;

		noise = new OpenSimplexNoise(seed);

		// addField(new Point(0, 0), true);
	}

	public long getSeed() {
		return seed;
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
		return getField(hex, false);
	}

	public Field getField(Hex hex, boolean createNew) {

		for (int i = fields.size()-1; i >= 0; i--) {
			if (fields.get(i).getHex().equals(hex)) {
				return fields.get(i);
			}
		}

		if (createNew) {
			addField(hex);
			return getField(hex);
		} else {
			return null;
		}
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
				add = new EmptyField(hex, this);/*
				if (random.nextFloat()*100 < 40) {
					boolean village = true;
					for (int i = 0; i < 6; i++) {
						if (getField(hex.neighbor(i)) != null && getField(hex.neighbor(i)).hasBuilding() && (getField(hex.neighbor(i)).getBuilding() instanceof City || getField(hex.neighbor(i)).getBuilding() instanceof Village)) {
							village = false;
						}
					}
					if (village) {
						add.setBuilding(new Village(add));
					}
				}*/
				break;
			case -1:
				add = new EmptyField(hex, this);
				boolean village = true;
				for (int i = 0; i < 6; i++) {
					if (getField(hex.neighbor(i)) != null && getField(hex.neighbor(i)).hasBuilding() && (getField(hex.neighbor(i)).getBuilding() instanceof City || getField(hex.neighbor(i)).getBuilding() instanceof Village)) {
						village = false;
					}
				}
				if (village) {
					add.setBuilding(new Village(add));
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
		double type_val = val * 10.0;

		if (type_val < -3) {
			// lake / oceans / rivers / ...
			return Field.WATER;
		} else if (type_val < 4) {
			// grass
			if (type_val < 0 && type_val > -1) {
				return -1;
			}
			return Field.EMPTY;
		} else if (type_val < 6) {
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
