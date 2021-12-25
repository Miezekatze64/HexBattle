package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.fields.*;

public abstract class GameCharacter {
	protected Hex position;
	protected Layout hexLayout;
	protected Field field;
	protected Player player;
	protected Map map;

	protected boolean isMoved = false;
	
    public static Image img;
	protected boolean animating;

	public static int SIZE = 48;

	protected Animation animation;

	public GameCharacter(Field field, Layout hexLayout, Player player) {
		this.map = player.map;
		if (field == null) {
			throw new RuntimeException("Player is on unexisting field!");
		} else {
			field.setCharacter(this);
		}

		this.field = field;
		this.position = field.getHex();
		this.hexLayout = hexLayout;
		this.player = player;
	}

	public Hex getPosition() {
		return position;
	}
	
    protected static void scaleImage(double w, double h) {
        img = img.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
    }

	abstract public int getMovementLength();

	abstract public int getInitialLife();

	public void setPossibleFields() {
		setPossibleFields(position, 0);
	}

	public boolean isFromPlayer(Player player) {
		return player == this.player;
	}

	public void setMoved(boolean bool) {
		isMoved = bool;
	}

	public boolean isMoved() {
		return isMoved;
	}

	public void setPossibleFields(Hex h, int count) {
		if (count < getMovementLength()) {
			for (int n = 0; n < 6; n++) {
				if (!h.neighbor(n).equals(position)) {
					if (map.getField(h.neighbor(n)) instanceof WaterField) {
						/*
						 * TODO implement boats ↓
						if (!(this instanceof BoatCharacter)) {*/
							continue;
						/*}*/
					}
					player.activate(h.neighbor(n));
				}
			}
			count++;
			for (int n = 0; n < 6; n++) {
				setPossibleFields(h.neighbor(n), count);
			}
		}
	}

    public void render(Graphics g, double zoom) {
    	Point pos;
    	if (!animating) {
    		pos = map.hexToDisplay(hexLayout.hexToPixel(position));
    	} else {
    		pos = animation.getPosition();
    	}
        g.drawImage(img, (int)(pos.x - (SIZE*zoom)/2), (int)(pos.y - (SIZE*zoom)/2), (int)(SIZE*zoom), (int)(SIZE*zoom), null);
    }

	public void moveTo(Field f) {
		this.animating = true;
		animation = new Animation(position, f.getHex(), 200, map) {
			@Override
			public void animationFinished() {
				animating = false;
				animation = null;
				position = f.getHex();
			}
		};
	}

	public abstract void checkAndAddTools(Toolbar toolbar);
}