package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.hex.Hex;

import com.mieze.hexbattle.Map;

public class MountainField extends Field {
	public MountainField(Hex hex, Map map) {
		super(hex, map);
	}

	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#7f7f7f"));
	}

}
