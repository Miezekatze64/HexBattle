package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.client.ClientMap;

public class UnexploredField extends Field {
	public UnexploredField(Hex hex, ClientMap map) {
		super(hex, map);
	}
	
	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#dddddd"));
	}

	@Override
	public String getID() {
		return "unexplored";
	}
}
