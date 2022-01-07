package com.mieze.hexbattle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.hex.Hex;

public abstract class Animation {

	private Map map;
	private float rel_pos = 0;
	private Point startPoint;
	private Point endPoint;
	private double length;

	public Animation(Hex start, Hex end, long duration, Map map) {
		this.map = map;

		startPoint = HexPanel.hexLayout.hexToPixel(start);
		endPoint = HexPanel.hexLayout.hexToPixel(end);
		
		length = Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
		
		new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rel_pos < 1) {
					rel_pos += 20.0 / duration;
				} else {
					animationFinished();
					((Timer)e.getSource()).stop();
					return;
				}
			}
		}).start();
	}

	public Point getPosition() {

		double angle = Math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x);
		
		double newLength = length * rel_pos;

		int x = (int) (startPoint.x + Math.cos(angle) * newLength);
		int y = (int) (startPoint.y + Math.sin(angle) * newLength);

		return map.hexToDisplay(new Point(x, y));
	}

	public abstract void animationFinished();

}
