package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.fields.*;

public abstract class GameCharacter {
    protected Hex position;
    protected Layout hexLayout;
    protected Field field;
    protected Player player;
    protected Map map;
    
    protected boolean animating;
    
    protected Animation animation;

    public GameCharacter(Field field, Layout hexLayout, Player player) {
    	this.map = player.map;
    	if (field == null) {
        	throw new RuntimeException("Player is on unexisting field!");
        } else {
        	field.addCharacter(this);
        }
    	
    	this.field = field;
        this.position = field.getHex();
        this.hexLayout = hexLayout;
        this.player = player;
    }
    
    public Hex getPosition() {
    	return position;
    }
    
    abstract public int getMovementLength();
    
    public void setPossibleFields() {
    	setPossibleFields(position, 0);
    }
    
    public void setPossibleFields(Hex h, int count) {
    	if (count < getMovementLength()) {
    		for (int n = 0; n < 6; n++) {
    			player.activate(h.neighbor(n));
    		}
    	}
    }
    
    public abstract void render(Graphics g, int offset_x, int offset_y, double zoom);

	public void moveTo(Field f) {
		this.animating = true;
		animation = new Animation(position, f.getHex(), 500, map) {
			@Override
			public void animationFinished() {
				animating = false;
				animation = null;
			}
		};
	}
}