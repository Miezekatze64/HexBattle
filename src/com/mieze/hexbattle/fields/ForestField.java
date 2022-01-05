package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.fields.building.Forest;

public class ForestField extends Field {

	public ForestField(Hex hex, Map map) {
		super(hex, map);
		setBuilding(new Forest(this));
	}

	public void chop() {
		if (!((Forest)getBuilding()).chop()) {
			map.overrideFieldType(hex, Field.EMPTY);
		}
	}

	@Override
	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#007f00"));
	}

}
