package com.mieze.hexbattle.characters;

import java.awt.*;
import com.mieze.hexbattle.*;
import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.fields.*;

public class WorkerCharacter extends GameCharacter {
	private static Image chop_wood;
	private static Image mine;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/worker.png");
		scaleImage(32, 32);
		
		chop_wood = toolkit.getImage("assets/city_1.png");
		scaleImage(32, 32);
		
		mine = toolkit.getImage("assets/city_1.png");
		scaleImage(32, 32);
    }

	public WorkerCharacter(Field field, Layout hexLayout, Player player) {
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
			if (field instanceof ForestField) {
				if (!toolbar.hasButton("Chop wood"))
				toolbar.add(new ToolbarButton("Chop wood", chop_wood) {
					@Override
					public void onClick() {
/*						player.conquerCity(position);
						player.reset();
*/					}
				});
			} else if (field.hasBuilding() && field instanceof MountainField) {
				if (!toolbar.hasButton("Mine"))
				toolbar.add(new ToolbarButton("Mine", mine) {
					@Override
					public void onClick() {
/*						player.conquerCity(position);
						player.reset();
*/					}
				});
			}
		}
	}

	@Override
	public int getInitialLife() {
		return 5;
	}
	
	
	@Override
	public int getAttackScore() {
		return 3;
	}
		
	@Override
	public int getDefenceScore() {
		return 1;
	}
}
