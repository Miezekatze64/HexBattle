package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.hex.Hex;

public class MountainField extends Field {
	public MountainField(Hex hex, ClientMap map) {
		super(hex, map);
	}

	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#7f7f7f"));
	}

	@Override
	public String getID() {
		return "mountain";
	}
}
