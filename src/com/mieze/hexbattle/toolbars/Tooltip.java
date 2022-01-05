package com.mieze.hexbattle.toolbars;

import java.awt.*;
import java.util.List;
import java.util.*;

import com.mieze.hexbattle.Map;

public class Tooltip {
		private String title, text;
		private static final Font ftext = new Font("Helvetia", Font.PLAIN, 12);
		private static final Font ftitle = new Font("Helvetia", Font.BOLD, 14);
		private static final int w = 125;
		private boolean bottom = false;
		List<String> strings;

		public Tooltip(String title, String text, boolean alignBottom) {
			this.title = title;
			this.text = text;
			this.bottom = alignBottom;
		}

		public Tooltip(String title, String text) {
			this(title, text, false);
		}

		public void render(Graphics g, Map m, int x, int y) {
			Font save = g.getFont();

			g.setFont(ftext);
			if (this.strings == null) this.strings = wrap(text, g.getFontMetrics(ftext), w-10);
			final int h = (strings.size()+4)*15;

			int rx = x-w/2;
			int ry = bottom?y-h-45:y+45;

			g.setColor(Color.RED);
			g.drawRect(rx, ry, w, h);

			g.setColor(Color.BLACK);
			for (int i = 0; i < strings.size(); i++) {
				g.drawString(strings.get(i), rx+5, ry+(i+4)*15);
			}

			g.setFont(ftitle);
			g.drawString(title, rx+7, ry+30);

			g.setColor(Color.RED);

			g.setFont(save);
		}

		private List<String> wrap(String txt, FontMetrics fm, int maxWidth){

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