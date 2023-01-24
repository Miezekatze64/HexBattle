package com.mieze.hexbattle.characters;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.client.render.Animation;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.WaterField;
import com.mieze.hexbattle.fields.building.Port;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.hex.Point;

public class GameCharacter {
	public static final int BUILDER = 0;
	public static final int WORKER = 1;
	public static final int SWORDSMAN = 2;
	public static final int RIDER = 3;
	public static final int BOAT = 4;

	protected Layout hexLayout;
	protected Field field;
	protected Player player;
	protected Map map;

	protected boolean isMoved = false;
	
	protected static Image conquer_city;
	protected boolean animating;
	protected double health = 0;

	public static int IMAGE_SIZE = 48;
	protected Animation animation;

	private CharacterData data;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		conquer_city = toolkit.getImage("assets/city_1.png");
	}

	public GameCharacter(Field field, Layout hexLayout, Player player, CharacterData data) {
		this.data = data;
		this.map = player.getMap();
		if (field == null) {
			throw new RuntimeException("Player is on non-existend field!");
		} else {
			field.setCharacter(this);
		}

		this.field = field;
		this.hexLayout = hexLayout;
		this.player = player;
		this.health = data.getInitialLife();
	}

	public void setField(Field end_field) {
		this.field.removeCharacter();
		end_field.setCharacter(this);
		field = end_field;
    }

	public Hex getPosition() {
		return field.getHex();
	}

	public CharacterData getData() {
		return this.data;
	}
	
    public void setHealth(double health) {
		this.health = health;
		if (health <= 0) {
			die();
		}
	}
	
	private void die() {
		throw new RuntimeException("TODO: move to event");
		// map.getField(position).setCharacter(null);
		// player.removeCharacter(this);
	}
	
	public double getHealth()  {
		return this.health;
	}

	public void setPossibleFields() {
		setPossibleFields(getPosition(), 0);
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
		if (count < data.getMovementLength()) {
			for (int n = 0; n < 6; n++) {
				if (!h.neighbor(n).equals(getPosition())) {
					if (map.getField(h.neighbor(n)) instanceof WaterField) {
						if (map.getField(h.neighbor(n)).hasBuilding() && map.getField(h.neighbor(n)).getBuilding() instanceof Port && map.getField(h.neighbor(n)).hasCharacter() && map.getField(h.neighbor(n)).getCharacter().getData() instanceof Boat) {
							throw new RuntimeException("TODO: move this to ClientRenderer [+make it declarative]");
//							player.activate(h.neighbor(n));
//							continue;
						}
						continue;
					}
					throw new RuntimeException("TODO: move this to ClientRenderer [+make it declarative]");
//					player.activate(h.neighbor(n));
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
    		pos = ((ClientMap)map).hexToDisplay(hexLayout.hexToPixel(getPosition()));
    	} else {
    		pos = animation.getPosition();
			System.out.printf("Animation pos: %s\n", pos);
    	}
		Image img = data.getImage();

        g.drawImage(img, (int)(pos.x - (IMAGE_SIZE*zoom)/2), (int)(pos.y - (IMAGE_SIZE*zoom)/2), (int)(IMAGE_SIZE*zoom), (int)(IMAGE_SIZE*zoom), null);
		g.setColor(player.getColor());
		g.fillRect((int)(pos.x - (IMAGE_SIZE*zoom)/3), (int)(pos.y - (IMAGE_SIZE*zoom)/2-(10*zoom)), (int)(IMAGE_SIZE*zoom*(2.0/3.0)*(health/data.getInitialLife())), (int)(5*zoom));
    }

	public void moveTo(Field f) {
		this.animation = new Animation(getPosition(), f.getHex(), 200, (ClientMap)map) {
			@Override
			public void animationFinished() {
				animating = false;
				animation = null;
				setField(f);
			}
		};
		this.animating = true;
		System.out.println("animation active!!");
	}

	public Player getPlayer() {
		return player;
	}

    public boolean canWalkOn(Field field) {
        return data.canWalkOn(field);
    }

    public boolean canAttack(GameCharacter character) {
        return character.getPlayer() != this.getPlayer();
    }
}
