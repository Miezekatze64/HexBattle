package com.mieze.hexbattle.toolbars;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.Map;

public class Inventory {
	public static final int WOOD = 0;
	public static final int COAL = 1;
	public static final int IRON = 2;
	public static final int DIAMONDS = 3;
	public static final int CHARPOINTS = 4;
	
	private long wood = 0;
	private long coal= 0;
	private long iron = 0;
	private long diamonds = 0;
	
	private double characterPoints = 0.0;
	
	private static Image imgWood;
	private static Image imgCoal;
	private static Image imgIron;
	private static Image imgDiamonds;
	private static Image imgCharPoints;
	
    static {
    	int w = 32, h = 32;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        imgWood = toolkit.getImage("assets/item_wood.png");
        imgWood = imgWood.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
        
        imgCoal= toolkit.getImage("assets/img_coal.png");
        imgCoal = imgCoal.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
        
        imgIron = toolkit.getImage("assets/item_iron.png");
        imgIron = imgIron.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
        
        imgDiamonds = toolkit.getImage("assets/item_diamond.png");
        imgDiamonds = imgDiamonds.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
        
        imgCharPoints = toolkit.getImage("assets/item_character.png");
        imgCharPoints = imgCharPoints.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
    }
	
	public Inventory() {
		
	}
	
	public void addResources(int resource, double count) {
		switch(resource) {
		case WOOD:
			wood += count;
			break;
		case COAL:
			coal += count;
			break;
		case IRON:
			iron += count;
			break;
		case DIAMONDS:
			diamonds += count;
			break;
		case CHARPOINTS:
			characterPoints += count;
			break;
		}
	}
	
	public void render(Graphics g, Map m) {
		g.drawImage(imgWood, (int)(m.getWidth()*0.25), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(wood).toString(), (int)(m.getWidth()*0.25)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(wood).toString())/2, 70);
	}
}
