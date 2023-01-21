package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Point;

public class Forest extends Building {
	private static final double SIZE = 50;
	private static BufferedImage[] img = new BufferedImage[4];
	private int trees = 3;

	static {
		try {
			img[0] = ImageIO.read(new File("assets/forest_1.png"));
			img[1] = ImageIO.read(new File("assets/forest_2.png"));
			img[2] = ImageIO.read(new File("assets/forest_3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Forest(Field f) {
		super(f);
	}

	public boolean chop() {
		trees--;
		if (trees == 0) return false;
		return true;
	}

	@Override
	public void onClick() {
		//nothing
	}
	
	@Override
	public void render(Graphics g, double zoom) {
//		throw new RuntimeException("TODO: here!!");
		ClientMap map = field.map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));

		g.drawImage(img[trees-1], (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
					(int) (SIZE * zoom), null);
	}

	@Override
	public String getID() {
		return "forest";
	}
}
