package com.mieze.hexbattle.toolbars;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;

import javax.swing.GrayFilter;

import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.client.Client;
import com.mieze.hexbattle.client.ClientMap;

public abstract class ToolbarButton {
	
	private boolean mouseOver = false;

	public static final int WIDTH = 60;
	public static final int HEIGHT = 60;

	protected String name;
	private Image image, grayImage;
	private Tooltip tooltip;
	
	public ToolbarButton(String name, Image img, String tooltipText) {
		this.name = name;
		this.image = img;
		this.tooltip = new Tooltip(name, tooltipText, true);

		ImageFilter filter = new GrayFilter(true, 50);
		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);  
		this.grayImage = Toolkit.getDefaultToolkit().createImage(producer);
	}

	public ToolbarButton(String name, Image img) {
		this(name, img, "No description provided");
	}
	
	public abstract void onClick();
	
	public void render(Graphics g, ClientMap m, int x, int y, Client client) {
		g.drawImage((!client.isTurn())? grayImage : image, x-WIDTH/2, y-HEIGHT/2, WIDTH, HEIGHT, null);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Color.RED);
		g.drawString(name, x-g.getFontMetrics(g.getFont()).stringWidth(name)/2, y+HEIGHT/2+20);
		
		if (mouseOver) tooltip.render(g, m, x, y);
	}
	
	public void hover(boolean isHovering) {
		this.mouseOver = isHovering;
	}

	public boolean mouseOver() {
		return mouseOver;
	}

}
