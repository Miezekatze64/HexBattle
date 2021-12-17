package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;

public class UnexploredField extends Field {

	public UnexploredField(Hex hex, Map map) {
		super(hex, map);
	}
	
	@Override
	public void render(Graphics g) {
		renderHex(g, map.zoom, Color.decode("#dddddd"));
	}
}
