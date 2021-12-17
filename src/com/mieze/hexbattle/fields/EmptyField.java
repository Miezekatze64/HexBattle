package com.mieze.hexbattle.fields;

import java.awt.*;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;

public class EmptyField extends Field {
	
	public EmptyField(Hex hex, Map map) {
		super(hex, map);
	}

	public void render(Graphics g) {
		renderHex(g, map.zoom, Color.decode("#00ff00"));
	}
}
