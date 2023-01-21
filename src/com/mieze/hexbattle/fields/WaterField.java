package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.toolbars.Toolbar;

public class WaterField extends Field {
	public WaterField(Hex hex, ClientMap map) {
		super(hex, map);
	}

	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#0000ff"));
	}

	public void onClick() {
		Toolbar toolbar = Main.getClient().getWorldData().getToolbar();
		final Field field = this;

		throw new RuntimeException("TODO: rewrite this completely");

/*		if (!(getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("create port") && !hasCharacter()) {
			toolbar.add(new ToolbarButton("create port", Port.img, "A port to start boats...\n\nCosts: " + 4 +" wood and " + 1 + " iron.") {
				@Override
				public void onClick() {
					Player player = getOwner();
					if (player.buyCharacter(0, new int[]{4, 0, 1, 0})) {
						setBuilding(new Port(field));

						Hex hex = field.getHex();
						Main.getClient().getConnection().sendEvent(new Event(Event.S_GAME_NEW_PORT, hex.q+","+hex.r+","+hex.s));
					} else {
						JOptionPane.showMessageDialog(null, "You need at least 4 wood and 1 iron to buy this.", "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
*/
    }

	@Override
	public boolean isWalkable() {
		return false;
	}

	@Override
	public String getID() {
		return "water";
	}
}
