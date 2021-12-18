package com.mieze.hexbattle.fields.building;

import com.mieze.hexbattle.fields.*;

public abstract class Building {
	Field field;
	public Building(Field f) {
		this.field = f;
	}
	
	public abstract void onClick();
	
	public abstract void render();
	
}
