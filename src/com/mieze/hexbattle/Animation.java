package com.mieze.hexbattle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;
import com.mieze.hexbattle.hex.*;

public abstract class Animation {
	private Map map;
	private Hex start, end;
	private float rel_pos = 0;
	private Point startPoint;
	private Point endPoint;
	private double length;

	public Animation(Hex start, Hex end, long duration, Map map) {
		this.start = start;
		this.end = end;
		this.map = map;

		startPoint = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(start));
		endPoint = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(end));
		length = Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));
		
		final long time = System.currentTimeMillis();
		
		new Timer(10, new ActionListener() {
			long lastTime = System.currentTimeMillis();
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rel_pos < 1) {
					rel_pos += 10.0 / duration;
				} else {
					animationFinished();
					return;
				}
				System.out.println(System.currentTimeMillis()-lastTime);
				lastTime = System.currentTimeMillis();
			}
		}).start();
	}

	public Point getPosition() {

		double angle = Math.atan((endPoint.x - startPoint.x) / (endPoint.y - startPoint.y));

		double newLength = length * rel_pos;

		int x = (int) (startPoint.x + Math.cos(angle) * newLength);
		int y = (int) (startPoint.y + Math.sin(angle) * newLength);

		return new Point(x, y);
	}

	public abstract void animationFinished();

}
