package com.mieze.hexbattle.characters;

import java.awt.*;
import java.awt.image.*;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.fields.*;

public class BuilderCharacter extends GameCharacter {
    private static Image img;
    public static int SIZE = 48;
    private ImageObserver observer;

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        img = toolkit.getImage("assets/builder.png");
        scaleImage(32, 32);
    }

    private static void scaleImage(double w, double h) {
        img = img.getScaledInstance((int)w, (int)h, Image.SCALE_DEFAULT);
    }

    public BuilderCharacter(Field field, Layout hexLayout, Player player) {
        super(field, hexLayout, player);
    }

    @Override
    public void render(Graphics g, int offset_x, int offset_y, double zoom) {
    	Point pos;
    	if (!animating) {
    		pos = map.hexToDisplay(hexLayout.hexToPixel(position));
    	} else {
    		pos = animation.getPosition();
    	}
        g.drawImage(img, (int)(pos.x - (SIZE*zoom)/2), (int)(pos.y - (SIZE*zoom)/2), (int)(SIZE*zoom), (int)(SIZE*zoom), observer);
    }

	@Override
	public int getMovementLength() {
		return 1;
	}
	
}
