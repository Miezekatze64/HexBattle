package com.mieze.hexbattle.toolbars;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.hex.Point;

public class Inventory {
	public static final int WOOD = 0;
	public static final int COAL = 1;
	public static final int IRON = 2;
	public static final int DIAMONDS = 3;
	public static final int CHARPOINTS = 4;
	
	private long wood = 4;
	private long coal= 4;
	private long iron = 2;
	private long diamonds = 1;
	
	private double characterPoints = 1.0;
	
	private static Image imgWood;
	private static Image imgCoal;
	private static Image imgIron;
	private static Image imgDiamonds;
	private static Image imgCharPoints;

	private boolean hover = true;
	private int hovering = -1;

	private static Tooltip[] tooltips = new Tooltip[5];
	
    static {
    	int w = 32, h = 32;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        imgWood = toolkit.getImage("assets/item_wood.png");
        imgWood = imgWood.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        
        imgCoal= toolkit.getImage("assets/item_coal.png");
        imgCoal = imgCoal.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        
        imgIron = toolkit.getImage("assets/item_iron.png");
        imgIron = imgIron.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        
        imgDiamonds = toolkit.getImage("assets/item_diamond.png");
        imgDiamonds = imgDiamonds.getScaledInstance(w, h, Image.SCALE_DEFAULT);
        
        imgCharPoints = toolkit.getImage("assets/item_character.png");
        imgCharPoints = imgCharPoints.getScaledInstance(w, h, Image.SCALE_DEFAULT);

		tooltips[WOOD] = new Tooltip("Wood", "Can be obtained from a worker by chopping wood.\n\nUsed to build things like mines or ports.");
		tooltips[COAL] = new Tooltip("Coal", "Not rare. Can be obtained from a worker by mining at a coal mine.\n\nUsed to upgrade cities and buy advanced characters.");
		tooltips[CHARPOINTS] = new Tooltip("Character-Points", "You get 0.5 times the count of your cities per round.\n\nUsed to buy all characters.");
		tooltips[IRON] = new Tooltip("Iron", "Medium rare. Can be obtained from a worker by mining at a iron mine.\n\nUsed to buy thing like battleships or knights.");
		tooltips[DIAMONDS] = new Tooltip("Diamonds", "Very rare. Can be obtained from a worker by mining at a diamond mine.\n\nUsed to buy advanced (and expensive) things like late city upgrades or advanced creatures.");
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
			setCharacterPoints(getCharacterPoints() + count);
			break;
		}
	}

	 //first check > 0 before calling this
	public void subResources(int resource, double count) {
		switch(resource) {
		case WOOD:
			wood -= count;
			break;
		case COAL:
			coal -= count;
			break;
		case IRON:
			iron -= count;
			break;
		case DIAMONDS:
			diamonds -= count;
			break;
		case CHARPOINTS:
			setCharacterPoints(getCharacterPoints() - count);
			break;
		}
	}

	public long getResources(int resource) {
		switch(resource) {
		case WOOD:
			return wood;
		case COAL:
			return coal;
		case IRON:
			return iron;
		case DIAMONDS:
			return diamonds;
		default:
			return 0;
		}
	}
	
	public void render(Graphics g, ClientMap m) {
		g.drawImage(imgWood, (int)(m.getWidth()*0.17), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(wood).toString(), (int)(m.getWidth()*0.17)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(wood).toString())/2, 80);
		
		g.drawImage(imgCoal, (int)(m.getWidth()*0.33), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(coal).toString(), (int)(m.getWidth()*0.33)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(coal).toString())/2, 80);
		
		g.drawImage(imgCharPoints, (int)(m.getWidth()*0.5), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(getCharacterPoints()).toString(), (int)(m.getWidth()*0.5)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(getCharacterPoints()).toString())/2, 80);
		
		g.drawImage(imgIron, (int)(m.getWidth()*0.67), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(iron).toString(), (int)(m.getWidth()*0.67)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(iron).toString())/2, 80);
		
		g.drawImage(imgDiamonds, (int)(m.getWidth()*0.83), 20, 40, 40, null);
		g.drawString(new StringBuilder().append(diamonds).toString(), (int)(m.getWidth()*0.83)+20-g.getFontMetrics(g.getFont()).stringWidth(new StringBuilder().append(diamonds).toString())/2, 80);

		if (hover) {
			switch(hovering) {
			case WOOD:
				tooltips[WOOD].render(g, m, (int)(m.getWidth()*0.17)+10, 20);
				break;
			case COAL:
				tooltips[COAL].render(g, m, (int)(m.getWidth()*0.33)+10, 20);
				break;
			case CHARPOINTS:
				tooltips[CHARPOINTS].render(g, m, (int)(m.getWidth()*0.5)+10, 20);
				break;
			case IRON:
				tooltips[IRON].render(g, m, (int)(m.getWidth()*0.67)+10, 20);
				break;
			case DIAMONDS:
				tooltips[DIAMONDS].render(g, m, (int)(m.getWidth()*0.83)+10, 20);
				break;
			}
		}
	}

	public double getCharacterPoints() {
		return characterPoints;
	}

	public void setCharacterPoints(double characterPoints) {
		this.characterPoints = characterPoints;
	}

	public void mouseMoved(Point point, ClientMap m) {
		if (point.y > 20 && point.y < 60) {
			if (point.x > m.getWidth()*0.17 && point.x < m.getWidth()*0.17+40) {
				hover = true;
				hovering = WOOD;
				return;
			} else if (point.x > m.getWidth()*0.33 && point.x < m.getWidth()*0.33+40) {
				hover = true;
				hovering = COAL;
				return;
			} else if (point.x > m.getWidth()*0.5 && point.x < m.getWidth()*0.5+40) {
				hover = true;
				hovering = CHARPOINTS;
				return;
			} else if (point.x > m.getWidth()*0.67 && point.x < m.getWidth()*0.67+40) {
				hover = true;
				hovering = IRON;
				return;
			} else if (point.x > m.getWidth()*0.83 && point.x < m.getWidth()*0.83+40) {
				hover = true;
				hovering = DIAMONDS;
				return;
			}
		}
		hover = false;
		hovering = -1;
    }
}
