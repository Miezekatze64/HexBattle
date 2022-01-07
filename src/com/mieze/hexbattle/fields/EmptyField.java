package com.mieze.hexbattle.fields;

import java.awt.Graphics2D;
import java.awt.Color;

import com.mieze.hexbattle.hex.Hex;

import com.mieze.hexbattle.Map;

public class EmptyField extends Field {
	
	public EmptyField(Hex hex, Map map) {
		super(hex, map);
	}

	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#00ff00"));
	}
}
