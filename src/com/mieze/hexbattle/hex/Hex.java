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
		return (Math.abs(q) + Math.abs(r) + Math.abs(s)) / 2;
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

    public int hashCode() {
        int prime1 = 31;
        int prime2 = 19;
        int prime3 = 37;
        int hash = prime1 * q + prime2 * r + prime3 * s;

        return hash;
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
		int r = (row - col) / 2;
		int s = -q - r;
		return new Hex(q, r, s);
	}

	static public DoubledCoord rdoubledFromCube(Hex h) {
		int col = 2 * h.q + h.r;
		int row = h.r;
		return new DoubledCoord(col, row);
	}

	public Hex rdoubledToCube() {
		int q = (col - row) / 2;
		int r = row;
		int s = -q - r;
		return new Hex(q, r, s);
	}

}
