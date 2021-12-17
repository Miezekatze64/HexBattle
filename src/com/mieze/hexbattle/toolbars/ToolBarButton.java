package com.mieze.hexbattle.toolbars;

import com.mieze.hexbattle.*;

import java.awt.*;

public abstract class ToolbarButton {
	private String name;
	private Image image;
	
	public ToolbarButton(String name, Image img) {
		this.name = name;
		this.image = img;
	}
	
	public abstract void onClick();
	
	public void render(Graphics g, Map m, int x, int y) {
		int w = 60;
		int h = 60;
		g.drawImage(image, x-w/2, y-h/2, w, h, null);
		
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Color.RED);
		
		g.drawString(name, x-g.getFontMetrics(g.getFont()).stringWidth(name)/2, y+h/2+6);
	}
}