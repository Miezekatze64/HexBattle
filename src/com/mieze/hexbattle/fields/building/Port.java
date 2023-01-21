package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JOptionPane;

import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.characters.Boat;
import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.net.Event;
import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;

public class Port extends Building {
	private static final double SIZE = 50;
	public static Image img;

	static {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		img = toolkit.getImage("assets/port.png");
	}

	public Port(Field f) {
		super(f);
	}
	
	@Override
	public void onClick() {
		Toolbar toolbar = Main.getClient().getWorldData().getToolbar();
		/*
		if (!(field.getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("New bpat")) {
			toolbar.add(new ToolbarButton("New boat", Boat.img, "A simple boat, which can be used to transport characters...\n\nCosts: "+Boat.RESOURCES[0]+" wood, "+Boat.RESOURCES[1]+" coal, "+Boat.RESOURCES[2]+" iron and "+Boat.PRICE+" character points.") {
				@Override
				public void onClick() {
					Player player = field.getOwner();
					if (player.buyCharacter(Boat.PRICE, Boat.RESOURCES)) {
						player.buyCharacter(field, GameCharacter.BOAT);

						Hex hex = field.getHex();
						player.openSurroundedFields(hex);
						Main.getClient().getConnection().sendEvent(new Event(Event.S_GAME_NEW_CHARACTER, hex.q+","+hex.r+","+hex.s+";"+GameCharacter.BOAT));
					} else {
						JOptionPane.showMessageDialog(null, resourceMessage(Boat.PRICE, Boat.RESOURCES), "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		*/
		throw new RuntimeException("TODO: rewrite this completely");
	}

	private String resourceMessage(double cp, int[] res) {
		final String pre = "You need at least ";
		String str = pre;
		for (int i = 0; i < res.length; i++) {
			if (res[i] != 0) str += res[i] + ((i == 0)?" wood, ":(i == 1)?" coal, ":(i == 2)?" iron, ":" diamonds ");
		}
		if (res[3] == 0) str = str.substring(0, str.length()-2)+ " ";

		str += "and " + cp + " character points.";

		return str;
	}

	@Override
	public void render(Graphics g, double zoom) {
		throw new RuntimeException("TODO: here!!");
		// Map map = field.getOwner().map;
		// Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));

		// g.drawImage(img, (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
		// 		(int) (SIZE * zoom), null);
	}

	@Override
	public String getID() {
		return "port";
	}
}

