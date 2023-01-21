package com.mieze.hexbattle.client;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.OpenSimplexNoise;
import com.mieze.hexbattle.Registry;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.hex.OffsetCoord;
import com.mieze.hexbattle.hex.Point;

public class ClientMap implements Map {
	protected ArrayList<Field> fields;
	protected HashSet<Hex> unexplored;

	protected int offset_x = 0;
	protected int offset_y = 0;
	public double zoom = 1;
	private long seed;

	private static Layout hexLayout = HexPanel.hexLayout;
	private static final double SMOOTH_FACTOR = 0.3;

	// private static long MAX_SIZE = 10000;
	private OpenSimplexNoise noise;

	public static Layout getLayout() {
		return hexLayout;
	}

	public ClientMap(int offset_x, int offset_y) {
		this(offset_x, offset_y, new Random(System.currentTimeMillis()).nextLong());
	}

	public ClientMap(int offset_x, int offset_y, long seed) {
		this.offset_x = offset_x;
		this.offset_y = offset_y;
		this.fields = new ArrayList<>();
		this.unexplored = new HashSet<>();
		this.seed = seed;

		noise = new OpenSimplexNoise(seed);

		// addField(new Point(0, 0), true);
	}

	public long getSeed() {
		return seed;
	}

	public Point hexToDisplay(Point p) {
		int x = (int) ((p.x - offset_x - Main.getPanel().getWidth() / 2) * zoom + Main.getPanel().getWidth() / 2);
		int y = (int) ((p.y - offset_y - Main.getPanel().getHeight() / 2) * zoom + Main.getPanel().getHeight() / 2);
		return new Point(x, y);
	}

	public Point displayToHex(Point p) {
		int x = (int) ((p.x - Main.getPanel().getWidth() / 2) / zoom + Main.getPanel().getWidth() / 2 + offset_x);
		int y = (int) ((p.y - Main.getPanel().getHeight() / 2) / zoom + Main.getPanel().getHeight() / 2 + offset_y);

		return new Point(x, y);
	}

	/*
	 * public Point offsetPoint(Point p) { return new Point((p.x + offset_x) / zoom,
	 * (p.y + offset_y) / zoom); }
	 */

	public Field getField(Hex hex, boolean createNew) {
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

	public void addField(Hex hex, String type) {
		if (contains(hex)) {
			this.fields.remove(this.getField(hex));
		}

		Field add;
        try {
            add = Registry.FIELDS.get(type).getDeclaredConstructor(Hex.class, ClientMap.class).newInstance(hex, this);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
			Main.handleException(e);
			return;
        }

		this.unexplored.remove(hex);
		this.fields.add(add);

		for (int i = 0; i < 6; i++) {
			Hex nh = hex.neighbor(i);
			if (!contains(nh) && !this.unexplored.contains(nh)) {
				this.unexplored.add(nh);
			}
		}
	}
	/*
	public void overrideFieldType(Hex hex, int type) {
		Field field = getField(hex);
		Player owner = field.getOwner();
		GameCharacter character = field.getCharacter();

		fields.remove(field);
		addField(hex, type);

		getField(hex).setOwner(owner);
		getField(hex).setCharacter(character);
	}*/

	public int getType(Hex hex) {
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
		return Main.getPanel().getWidth();
	}

	public int getHeight() {
		return Main.getPanel().getHeight();
	}

    public List<Field> getFields() {
        return this.fields;
    }

    public int getOffsetX() {
        return this.offset_x;
    }

    public int getOffsetY() {
        return this.offset_y;
    }

    public Set<Hex> getUnexplored() {
        return this.unexplored;
    }

    public Color getColor(ClientPlayer clientPlayer) {
        return clientPlayer.getColor();
    }

}
