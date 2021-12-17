package com.mieze.hexbattle.fields;

import java.awt.*;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;

public class EmptyField extends Field {
	
	public EmptyField(Hex hex, Map map) {
		super(hex, map);
	}

	public void render(Graphics g, int offset_x, int offset_y, double zoom) {
		renderHex(g, offset_x, offset_y, zoom, Color.decode("#00ff00"));
	}
}
