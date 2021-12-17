package com.mieze.hexbattle.toolbars;

import java.awt.Graphics;
import java.util.ArrayList;

import com.mieze.hexbattle.*;


public class Toolbar {
	
	private ArrayList<ToolbarButton> buttons = new ArrayList<>();
	
	public void add(ToolbarButton btn) {
		buttons.add(btn);
	}
	
	public boolean onClick(int x, int y, Map m) {
		boolean rt = false;
		
		int w = 60;
		int h = 60;
		
		for (int i = 0; i < buttons.size(); i++) {
			int bx = (int)((((double)(i+1)/((double)(buttons.size()+1))*.75)+.125)*m.getWidth())-w/2;
			int by = 50-h/2;
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
			int y = 50;
			buttons.get(i).render(g, m, x, y);
		}
	}
}