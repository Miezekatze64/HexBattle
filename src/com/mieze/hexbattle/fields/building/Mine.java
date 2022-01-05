package com.mieze.hexbattle.fields.building;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.toolbars.*;
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

	public Mine(Field f) {
		super(f);

		double rand = Math.random();
		this.type = rand < .5?MINE_COAL:(rand < .8?MINE_IRON:MINE_DIAMOND);

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
}
