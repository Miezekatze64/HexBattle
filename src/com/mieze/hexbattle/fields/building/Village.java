package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Point;

public class Village extends Building {
	public static int SIZE = 50;
	
	private static Image img;
	
	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/village.png");
	}

	public Village(Field f) {
		super(f);
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Graphics g, double zoom) {
		ClientMap map = field.map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));

		g.drawImage(img, (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
				(int) (SIZE * zoom), null);
	}

	@Override
	public String getID() {
		return "village";
	}
}
