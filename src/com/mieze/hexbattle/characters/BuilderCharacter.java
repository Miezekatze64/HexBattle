package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;
import com.mieze.hexbattle.fields.building.Village;

public class BuilderCharacter extends GameCharacter {
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
						player.reset();
					}
				});
			}
		}
	}

	@Override
	public int getInitialLife() {
		return 5;
	}
}
