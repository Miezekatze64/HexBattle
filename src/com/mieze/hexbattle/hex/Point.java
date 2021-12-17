package com.mieze.hexbattle.hex;

public class Point {
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public final double x;
	public final double y;
	
	public boolean equals(Point p) {
		return p.x == this.x && p.y == this.y;
	}
	
	public String toString() {
		return new StringBuilder().append("Point: [X: ").append(x).append(", Y: ").append(y).append("]").toString();
	}
}