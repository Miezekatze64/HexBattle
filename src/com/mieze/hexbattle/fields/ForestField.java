package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.fields.building.Forest;
import com.mieze.hexbattle.hex.Hex;

public class ForestField extends Field {
	public ForestField(Hex hex, ClientMap map) {
		super(hex, map);
		setBuilding(new Forest(this));
	}

	public void chop() {
		if (!((Forest)getBuilding()).chop()) {
			throw new RuntimeException("TODO: this should be done server-side automatically");
//			map.overrideFieldType(hex, Field.EMPTY);
		}
	}

	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#007f00"));
	}

	@Override
	public String getID() {
		return "forest";
	}
}
