package com.mieze.hexbattle;

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

		new Thread() {
			public void run() {
				while (rel_pos < 1) {
					rel_pos += (duration / 10.0) * (1.0 / length);

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
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
