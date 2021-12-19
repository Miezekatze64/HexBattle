package com.mieze.hexbattle.characters;

import java.awt.*;
import java.awt.image.*;

import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.Village;

public class BuilderCharacter extends GameCharacter {
    public static int SIZE = 48;
    private ImageObserver observer;
    private static Image build_city;

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        img = toolkit.getImage("assets/builder.png");
        scaleImage(32, 32);
        
        build_city = toolkit.getImage("assets/city_1.png");
        scaleImage(32, 32);
    }

    public BuilderCharacter(Field field, Layout hexLayout, Player player) {
        super(field, hexLayout, player);
    }

    @Override
    public void render(Graphics g, double zoom) {
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

	@Override
	public void checkAndAddTools(Toolbar toolbar) {
		Field field = map.getField(position);
		if (field.hasBuilding()) {
			if (field.getBuilding() instanceof Village) {
				if (!toolbar.hasButton("Build village"))
				toolbar.add(new ToolbarButton("Build village", build_city) {
					@Override
					public void onClick() {
						player.conquerCity(position);
					}
				});
				return;
			}
		}
		toolbar.reset();
	}
}
