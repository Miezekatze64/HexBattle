package com.mieze.hexbattle.fields;

import java.awt.*;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.characters.*;
import com.mieze.hexbattle.fields.building.*;

public abstract class Field {
	protected Hex hex;
	protected Layout hexLayout;

	public static final int EMPTY = 0;
	public static final int WATER = 1;
	public static final int MOUNTAIN = 2;
	public static final int FOREST = 3;

	private Building building = null;

	protected Map map;
	private GameCharacter character = null;

	private Player owner = null;
	private Color color = null;

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
			Field f = (Field) o;
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

	public void removeBuilding() {
		this.building = null;
	}

	public boolean hasBuilding() {
		return building != null;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public void removeOwner() {
		this.owner = null;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
		Color c = owner.getColor();
		this.color = c;
	}

	public abstract void render(Graphics2D g);

	public void renderHex(Graphics2D g, double zoom, Color bg) {
		int[] point_x = new int[hexLayout.polygonCorners(hex).size()];
		int[] point_y = new int[hexLayout.polygonCorners(hex).size()];
		for (int i = 0; i < hexLayout.polygonCorners(hex).size(); i++) {
			Point screenPoint = map.hexToDisplay(hexLayout.polygonCorners(hex).get(i));
			point_x[i] = (int) screenPoint.x;
			point_y[i] = (int) screenPoint.y;
		}

		g.setColor(bg);
		g.fillPolygon(point_x, point_y, point_x.length);

		if (hasOwner()) {
			g.setColor(color);
			g.setStroke(new BasicStroke(5));
			
			Stroke dashed = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{9}, 0);
			Stroke dashed2 = new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                    0, new float[]{9}, 9);
			
			for (int i = 0; i < 6; i++) {
				if (map.getField(hex.neighbor(i)) != null && map.getField(hex.neighbor(i)).getOwner() != getOwner()) {
					if (map.getField(hex.neighbor(i)).getOwner() != null) {
						g.setStroke(dashed);
						g.setColor(map.getField(hex.neighbor(i)).getOwner().getColor());
						g.drawLine(point_x[i], point_y[i], point_x[(i+1) % 6], point_y[(i+1) % 6]);
						g.setStroke(dashed2);
						g.setColor(color);
						g.drawLine(point_x[i], point_y[i], point_x[(i+1) % 6], point_y[(i+1) % 6]);
						g.setStroke(new BasicStroke(5));
					} else {
						g.drawLine(point_x[i], point_y[i], point_x[(i+1) % 6], point_y[(i+1) % 6]);
					}
				}
			}
		}
		
		((Graphics2D) g).setStroke(new BasicStroke(1));
		g.setColor(Color.black);
		g.drawPolygon(point_x, point_y, point_x.length);
		
		if (building != null) building.render(g, zoom);
	}

	public boolean isOnScreen(int offset_x, int offset_y, double zoom) {
		for (int i = 0; i < hexLayout.polygonCorners(hex).size(); i++) {
			Point screenPoint = map.hexToDisplay(hexLayout.polygonCorners(hex).get(i));

			int x = (int) screenPoint.x;
			int y = (int) screenPoint.y;

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
