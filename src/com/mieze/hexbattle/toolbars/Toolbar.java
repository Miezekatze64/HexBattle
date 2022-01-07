package com.mieze.hexbattle.toolbars;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.Point;


public class Toolbar {
	
	private ArrayList<ToolbarButton> buttons;
	private boolean isStandard = true;
	
	private static Image nextTurnImage;
	private Player player;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		nextTurnImage = toolkit.getImage("assets/next_turn.png");
		scaleImage(nextTurnImage, 32, 32);
	}

	private static void scaleImage(Image img, double w, double h) {
		img = img.getScaledInstance((int) w, (int) h, Image.SCALE_DEFAULT);
	}

	public Toolbar(Player player) {
		this.player = player;
		initToolbar();
	}
	
	public void add(ToolbarButton btn) {
		buttons.add(btn);
		isStandard = false;
	}
	
	private void initToolbar() {
		buttons = new ArrayList<>();
		buttons.add(new ToolbarButton("Next Turn", nextTurnImage, "Click to end your turn.") {
			@Override
			public void onClick() {
				if (player.state != Player.STATE_OTHER_PLAYER) Main.getPanel().nextTurn();
			}
		});
	}
	
	public boolean isStandard() {
		return isStandard;
	}
	
	public void reset() {
		initToolbar();
	}
	
	public boolean onClick(int x, int y, Map m) {
		boolean rt = false;
		
		int w = 60;
		int h = 60;
		
		for (int i = 0; i < buttons.size(); i++) {
			int bx = (int)((((double)(i+1)/((double)(buttons.size()+1))*.75)+.125)*m.getWidth())-w/2;
			int by = m.getHeight()-50-h/2;
			boolean tmp = bx < x && bx + w > x && by < y && by + h > y;
			if (tmp) {
				rt = true;
				buttons.get(i).onClick();
			}
		}
		return rt;
	}
	
	public void render(Graphics g, Map m) {
		for (int i = 0; i < buttons.size(); i++) {
			int x = (int)((((double)(i+1)/((double)(buttons.size()+1))*.75)+.125)*m.getWidth());
			int y = m.getHeight()-60;
			buttons.get(i).render(g, m, x, y, player);
		}
	}

	public boolean hasButton(String string) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).name.equals(string)) {
				return true;
			}
		}
		return false;
	}

    public void mouseMoved(Point point, Map m) {
		
		for (int i = 0; i < buttons.size(); i++) {
			int x = (int)((((double)(i+1)/((double)(buttons.size()+1))*.75)+.125)*m.getWidth())-ToolbarButton.WIDTH/2;
			int y = m.getHeight()-50-ToolbarButton.HEIGHT/2;

			if (buttons.get(i).mouseOver()) {
				if (point.x >= ToolbarButton.WIDTH+x || point.x <= x && point.y >= ToolbarButton.HEIGHT+y || point.y <= y) {
					buttons.get(i).hover(false);
				}
			} else {
				if (point.x < ToolbarButton.WIDTH+x && point.x > x && point.y < ToolbarButton.HEIGHT+y && point.y > y) {
					buttons.get(i).hover(true);
				}
			}
		}
    }
}