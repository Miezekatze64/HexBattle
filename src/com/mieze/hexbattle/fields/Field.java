package com.mieze.hexbattle.fields;

import java.awt.*;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.characters.*;

public abstract class Field {
	protected Hex hex;
	protected Layout hexLayout;
	
	public static final int EMPTY = 0;
	public static final int WATER = 1;
	public static final int MOUNTAIN = 2;
	public static final int FOREST = 3;

	private Map map;
	private GameCharacter character = null;
	
	public Field(Hex hex, Map map) {
		this.hexLayout = Map.getLayout();
		this.hex = hex;
		this.map = map;
	}
	
	public Hex toHex(Point p) {
		return hexLayout.pixelToHex(p).hexRound();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Field)) {
			return false;
		} else {
			Field f = (Field)o;
			return f.getHex().equals(getHex());
		}
	}
	
	public boolean hasCharacter() {
		return character != null;
	}
	
	public GameCharacter getCharacter() {
		return character;
	}
	
	public void setCharacter(GameCharacter character) {
		this.character = character;
	}
	
	public void removeCharacter() {
		this.character = null;
	}
	
	public abstract void render(Graphics g, int offset_x, int offset_y, double zoom);
	
	public void renderHex(Graphics g, double zoom, Color bg) {
		int[] point_x = new int[hexLayout.polygonCorners(hex).size()];
		int[] point_y = new int[hexLayout.polygonCorners(hex).size()];
		for (int i = 0; i < hexLayout.polygonCorners(hex).size(); i++) {
			Point screenPoint = map.hexToDisplay(hexLayout.polygonCorners(hex).get(i));
			point_x[i] = (int)screenPoint.x;
			point_y[i] = (int)screenPoint.y;
		}

		g.setColor(bg);
		g.fillPolygon(point_x, point_y, point_x.length);

		((Graphics2D) g).setStroke(new BasicStroke(1));
		g.setColor(Color.black);
		g.drawPolygon(point_x, point_y, point_x.length);

	}

	public boolean isOnScreen(int offset_x, int offset_y, double zoom) {
		for (int i = 0; i < hexLayout.polygonCorners(hex).size(); i++) {
			Point screenPoint = map.hexToDisplay(hexLayout.polygonCorners(hex).get(i));
			
			int x = (int)screenPoint.x;
			int y = (int)screenPoint.y;

			if (x > 0 && x < map.getWidth() && y > 0 && y < map.getHeight()) {
				return true;
			}
		}
		return false;
	}
	
	public Hex getHex() {
		return hex;
	}
}
