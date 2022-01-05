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
	private static final double SIZE = 50;
	private static BufferedImage[] img = new BufferedImage[4];

	static {
		try {
			img[0] = ImageIO.read(new File("assets/mine.png"));
			scaleImage(img[0], 32, 32);
//			img[1] = ImageIO.read(new File("assets/city_2.png"));
//			scaleImage(img[1], 32, 32);
//			img[2] = ImageIO.read(new File("assets/city_3.png"));
//			scaleImage(img[2], 32, 32);
//			img[3] = ImageIO.read(new File("assets/city_4.png"));
//			scaleImage(img[3], 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Mine(Field f) {
		super(f);
	}

	@Override
	public void render(Graphics g, double zoom) {
		Map map = field.getOwner().map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));

		g.drawImage(img[Inventory.COAL-1], (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
				(int) (SIZE * zoom), null);
	}

	@Override
	public void onClick() {
		
	}
}
