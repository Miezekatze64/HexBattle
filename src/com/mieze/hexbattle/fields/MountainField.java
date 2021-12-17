package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;

public class MountainField extends Field {
	public MountainField(Hex hex, Map map) {
		super(hex, map);
	}

	@Override
	public void render(Graphics g, int offset_x, int offset_y, double zoom) {
		renderHex(g, zoom, Color.decode("#7f7f7f"));
	}

}
