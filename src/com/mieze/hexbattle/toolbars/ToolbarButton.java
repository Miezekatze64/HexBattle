package com.mieze.hexbattle.toolbars;

import com.mieze.hexbattle.Map;

import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class ToolbarButton {
	private boolean mouseOver = false;

	public static final int WIDTH = 60;
	public static final int HEIGHT = 60;

	protected String name;
	private Image image;
	private Tooltip tooltip;
	
	public ToolbarButton(String name, Image img, String tooltipText) {
		this.name = name;
		this.image = img;
		this.tooltip = new Tooltip(name, tooltipText);
	}

	public ToolbarButton(String name, Image img) {
		this(name, img, "No description provided");
	}
	
	public abstract void onClick();
	
	public void render(Graphics g, Map m, int x, int y) {
		g.drawImage(image, x-WIDTH/2, y-HEIGHT/2, WIDTH, HEIGHT, null);
		
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.setColor(Color.RED);
		
		g.drawString(name, x-g.getFontMetrics(g.getFont()).stringWidth(name)/2, y+HEIGHT/2+6);

		if (mouseOver) tooltip.render(g, m, x, y);
	}
	
	public void hover(boolean isHovering) {
		this.mouseOver = isHovering;
	}

	public boolean mouseOver() {
		return mouseOver;
	}

	protected class Tooltip {
		private String title, text;

		public Tooltip(String title, String text) {
			this.title = title;
			this.text = text;
		}

		public void render(Graphics g, Map m, int x, int y) {
			int w = 125;
			int h = 150;
			
			int rx = x-w/2;
			int ry = y-h-45;

			g.setColor(Color.RED);
			g.drawRect(rx, ry, w, h);

			g.setColor(Color.BLACK);


			Font save = g.getFont();
			
			Font f = new Font("Helvetia", Font.BOLD, 14);

			g.setFont(f);
			g.drawString(title, rx+7, ry+30);

			f = new Font("Helvetia", Font.PLAIN, 12);
			g.setFont(f);

			List<String> strings = wrap(text, g.getFontMetrics(f), w-10);

			for (int i = 0; i < strings.size(); i++) {
				g.drawString(strings.get(i), rx+5, ry+(i+4)*15);
			}
			g.setColor(Color.RED);

			g.setFont(save);
			
		}

		private List<String> wrap(String txt, FontMetrics fm, int maxWidth){
			//StringTokenizer st =  new  StringTokenizer(txt);

			List<String> list = new ArrayList<String>();
			String line = "";
			String lineBeforeAppend = "";

			int pos = 0;

			while (pos < txt.length()) {
			
				char next = txt.charAt(pos);
				lineBeforeAppend = line;
				line += next;

				if (next == '\n') {
					list.add(line.substring(0, line.length()-1));
					line = "";
					pos++;
					continue;
				}

				int width = fm.stringWidth(line);


				if (width  < maxWidth) {
					pos++;
					continue;
				} else {
					//new Line.
					boolean space = false;
					for (int i = line.length()-1; i >= 0; i--) {
						if (line.charAt(i) == ' ') {
							list.add(line.substring(0, i));
							line = line.substring(i+1, line.length());
							space = true;
							break;
						}
					}
					if (!space) {
						list.add(lineBeforeAppend);
						line = next+"";
					}
				}
				pos++;
			}

			//the remaining part.
			if(line.length() > 0){
				list.add(line);
			}
			return list;
		}
	}

}