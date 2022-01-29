package com.mieze.hexbattle.fields;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.server.Client.Event;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;
import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.fields.building.Port;

public class WaterField extends Field {
	public WaterField(Hex hex, Map map) {
		super(hex, map);
	}

	public void render(Graphics2D g) {
		renderHex(g, map.zoom, Color.decode("#0000ff"));
	}

    public void onClick() {
		Toolbar toolbar = getOwner().getToolbar();
		final Field field = this;

		if (!(getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("create port") && !hasCharacter()) {
			toolbar.add(new ToolbarButton("create port", Port.img, "A port to start boats...\n\nCosts: " + 4 +" wood and " + 1 + " iron.") {
				@Override
				public void onClick() {
					Player player = getOwner();
					if (player.buyCharacter(0, new int[]{4, 0, 1, 0})) {
						setBuilding(new Port(field));

						Hex hex = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_NEW_PORT, hex.q+","+hex.r+","+hex.s));
					} else {
						JOptionPane.showMessageDialog(null, "You need at least 4 wood and 1 iron to buy this.", "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
    }
}
