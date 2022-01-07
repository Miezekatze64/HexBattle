package com.mieze.hexbattle.hex;

import java.util.ArrayList;

public class Hex {
	
	public Hex(int q, int r, int s) {
		this.q = q;
		this.r = r;
		this.s = s;
		if (q + r + s != 0)
			throw new IllegalArgumentException("q + r + s must be 0");
	}

	public final int q;
	public final int r;
	public final int s;

	public Hex add(Hex b) {
		return new Hex(q + b.q, r + b.r, s + b.s);
	}

	public Hex subtract(Hex b) {
		return new Hex(q - b.q, r - b.r, s - b.s);
	}

	public Hex scale(int k) {
		return new Hex(q * k, r * k, s * k);
	}

	public Hex rotateLeft() {
		return new Hex(-s, -q, -r);
	}

	public Hex rotateRight() {
		return new Hex(-r, -s, -q);
	}

	static public ArrayList<Hex> directions = new ArrayList<Hex>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add(new Hex(1, 0, -1));
			add(new Hex(1, -1, 0));
			add(new Hex(0, -1, 1));
			add(new Hex(-1, 0, 1));
			add(new Hex(-1, 1, 0));
			add(new Hex(0, 1, -1));
		}
	};

	static public Hex direction(int direction) {
		return Hex.directions.get(direction);
	}

	public Hex neighbor(int direction) {
		return add(Hex.direction(direction));
	}

	static public ArrayList<Hex> diagonals = new ArrayList<Hex>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add(new Hex(2, -1, -1));
			add(new Hex(1, -2, 1));
			add(new Hex(-1, -1, 2));
			add(new Hex(-2, 1, 1));
			add(new Hex(-1, 2, -1));
			add(new Hex(1, 1, -2));
		}
	};

	public Hex diagonalNeighbor(int direction) {
		return add(Hex.diagonals.get(direction));
	}

	public int length() {
		return (int) ((Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2);
	}

	public int distance(Hex b) {
		return subtract(b).length();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Hex)) {
			return false;
		} else {
			Hex hex = (Hex)o;
			return ((this.s == hex.s) && (this.q == hex.q) && (this.r == hex.r));
		}
	
	}
	
	public String toString() {
		return new StringBuilder().append("Hex: [Q = ").append(q).append(", R = ").append(r).append(", S = ").append(s).append("]").toString();
	}

}


class DoubledCoord {
	public DoubledCoord(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public final int col;
	public final int row;

	static public DoubledCoord qdoubledFromCube(Hex h) {
		int col = h.q;
		int row = 2 * h.r + h.q;
		return new DoubledCoord(col, row);
	}

	public Hex qdoubledToCube() {
		int q = col;
		int r = (int) ((row - col) / 2);
		int s = -q - r;
		return new Hex(q, r, s);
	}

	static public DoubledCoord rdoubledFromCube(Hex h) {
		int col = 2 * h.q + h.r;
		int row = h.r;
		return new DoubledCoord(col, row);
	}

	public Hex rdoubledToCube() {
		int q = (int) ((col - row) / 2);
		int r = row;
		int s = -q - r;
		return new Hex(q, r, s);
	}

}

class Orientation {
	public Orientation(double f0, double f1, double f2, double f3, double b0, double b1, double b2, double b3,
			double start_angle) {
		this.f0 = f0;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.b0 = b0;
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
		this.start_angle = start_angle;
	}

	public final double f0;
	public final double f1;
	public final double f2;
	public final double f3;
	public final double b0;
	public final double b1;
	public final double b2;
	public final double b3;
	public final double start_angle;
}
