package com.mieze.hexbattle.fields.building;

import java.awt.*;

import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.fields.Field;

public class City extends Building {
	private static final double SIZE = 60;
	private static Image lvl1, lvl2, lvl3, lvl4;
	private int lvl = 1;
	
    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        lvl1 = toolkit.getImage("assets/city_1.png");
        scaleImage(lvl1, 32, 32);
        lvl2 = toolkit.getImage("assets/city_2.png");
        scaleImage(lvl2, 32, 32);
        lvl3 = toolkit.getImage("assets/city_3.png");
        scaleImage(lvl3, 32, 32);
        lvl4 = toolkit.getImage("assets/city_4.png");
        scaleImage(lvl4, 32, 32);
    }
	
	public City(Field f) {
		super(f);
	}

	@Override
	public void onClick() {
		// TODO Implement city options
	}

	@Override
	public void render(Graphics g, double zoom) {
		Map map = field.getOwner().map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));
		g.drawImage(lvl1, (int)(pos.x - (SIZE*zoom)/2), (int)(pos.y - (SIZE*zoom)/2), (int)(SIZE*zoom), (int)(SIZE*zoom), null);
	}
}
