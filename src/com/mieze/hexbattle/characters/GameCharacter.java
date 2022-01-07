package com.mieze.hexbattle.characters;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Image;

import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.Animation;
import com.mieze.hexbattle.Map;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.hex.Point;

import com.mieze.hexbattle.toolbars.Toolbar;

import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.WaterField;

public abstract class GameCharacter {
	public static final int BUILDER = 0;
	public static final int WORKER = 1;
	public static final int SWORDSMAN = 2;

	protected Hex position;
	protected Layout hexLayout;
	protected Field field;
	protected Player player;
	protected Map map;

	protected boolean isMoved = false;
	
	protected static Image conquer_city;
	protected boolean animating;
	protected double health = 0;

	public static int SIZE = 48;

	protected Animation animation;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		conquer_city = toolkit.getImage("assets/city_1.png");
	}

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
		this.health = getInitialLife();
	}

	public Hex getPosition() {
		return position;
	}
	
    public void setHealth(double health) {
		this.health = health;
		if (health <= 0) {
			die();
		}
	}
	
	private void die() {
		field.setCharacter(null);
		player.removeCharacter(this);
	}
	
	public double getHealth()  {
		return this.health;
	}

	abstract public int getMovementLength();

	abstract public int getInitialLife();
	
	abstract public int getAttackScore();

	abstract public int getDefenceScore();

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
		Image img;
		if (this instanceof BuilderCharacter)
			img = BuilderCharacter.img;
		else if (this instanceof WorkerCharacter)
			img = WorkerCharacter.img;
		else if (this instanceof SwordsmanCharacter)
			img = SwordsmanCharacter.img;
		else
			throw new IllegalStateException("Character class not implemented: " + this.getClass().getCanonicalName());

        g.drawImage(img, (int)(pos.x - (SIZE*zoom)/2), (int)(pos.y - (SIZE*zoom)/2), (int)(SIZE*zoom), (int)(SIZE*zoom), null);

		g.setColor(player.getColor());
		g.fillRect((int)(pos.x - (SIZE*zoom)/3), (int)(pos.y - (SIZE*zoom)/2-(10*zoom)), (int)(SIZE*zoom*(2.0/3.0)*(health/getInitialLife())), (int)(5*zoom));
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

	public Player getPlayer() {
		return player;
	}
}
