package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;

public class UnexploredField extends Field {

	public UnexploredField(Hex hex, Map map) {
		super(hex, map);
	}
	
	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#dddddd"));
	}
}
