package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.Image;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mieze.hexbattle.hex.Point;

import com.mieze.hexbattle.toolbars.Inventory;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Map;

import com.mieze.hexbattle.fields.Field;

public class Mine extends Building {
	public static final int MINE_COAL = 0;
	public static final int MINE_IRON = 1;
	public static final int MINE_DIAMOND = 2;

	private int type;
	private double chance;
	private int amount;

	private static final double SIZE = 50;
	private static BufferedImage[] img = new BufferedImage[3];

	static {
		try {
			img[MINE_COAL] = ImageIO.read(new File("assets/mine_coal.png"));
			img[MINE_IRON] = ImageIO.read(new File("assets/mine_iron.png"));
			img[MINE_DIAMOND] = ImageIO.read(new File("assets/mine_diamond.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getType() {
		return type;
	}

	public Image getImage() {
		return img[type];
	}

	public int[] mine() {
		if (Math.random() <= chance) {
			int[] arr = new int[]{0, 0, 0, 0};
			switch(type) {
			case MINE_COAL:
				arr[Inventory.COAL] = amount;
				break;
			case MINE_IRON:
				arr[Inventory.IRON] = amount;
				break;
			case MINE_DIAMOND:
				arr[Inventory.DIAMONDS] = amount;
				break;
			}
			return arr;
		} else {
			return new int[]{0, 0, 0, 0};
		}
	}

	public Mine(Field f) {
		this(f, -1);
	}
	
	public Mine(Field f, int type) {
		super(f);
		if (type == -1) {
			double rand = Math.random();
			this.type = rand < .5?MINE_COAL:(rand < .8?MINE_IRON:MINE_DIAMOND);
		} else {
			this.type = type;
		}

		this.chance = Math.random();
		this.amount = (int)(Math.random()*9+1);
	}

	@Override
	public void render(Graphics g, double zoom) {
		Map map = field.getOwner().map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));

		g.drawImage(img[type], (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
				(int) (SIZE * zoom), null);
	}

	@Override
	public void onClick() {
		
	}

	public int getAmount() {
		return amount;
	}

    public double getChance() {
        return chance;
    }

	public String getTypeString() {
		return type == MINE_COAL?"coal":type==MINE_DIAMOND?"diamonds":"iron";
	}
}
