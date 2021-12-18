package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.Image;

import com.mieze.hexbattle.fields.*;

public abstract class Building {
	protected static Image img;

	Field field;

	public Building(Field f) {
		this.field = f;
	}

	protected static void scaleImage(Image img, double w, double h) {
		img = img.getScaledInstance((int) w, (int) h, Image.SCALE_DEFAULT);
	}

	public abstract void onClick();

	public abstract void render(Graphics g, double zoom);

}
