package com.mieze.hexbattle.fields.building;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javax.swing.JOptionPane;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Point;

import com.mieze.hexbattle.server.Client.Event;

import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.ToolbarButton;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.Player;

import com.mieze.hexbattle.characters.BuilderCharacter;
import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.characters.WorkerCharacter;
import com.mieze.hexbattle.characters.SwordsmanCharacter;
import com.mieze.hexbattle.characters.RiderCharacter;

import com.mieze.hexbattle.fields.Field;

public class City extends Building {
	private static final double SIZE = 50;
	private static BufferedImage[] img = new BufferedImage[4];
	private int lvl = 1;
	private Color color = null;
	private BufferedImage temp_img;

	static {
		try {
			img[0] = ImageIO.read(new File("assets/city_1.png"));
			scaleImage(img[0], 32, 32);
			img[1] = ImageIO.read(new File("assets/city_2.png"));
			scaleImage(img[1], 32, 32);
			img[2] = ImageIO.read(new File("assets/city_3.png"));
			scaleImage(img[2], 32, 32);
			img[3] = ImageIO.read(new File("assets/city_4.png"));
			scaleImage(img[3], 32, 32);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public City(Field f) {
		super(f);
	}

	private BufferedImage dye(BufferedImage image, Color color) {
		if (!color.equals(this.color)) {
			int w = image.getWidth();
			int h = image.getHeight();

			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					Color c = new Color(image.getRGB(x, y), true);
					int alpha = c.getAlpha();
					int gray = c.getRed();
					g.setColor(new Color(gray | color.getRed(), gray | color.getGreen(), gray | color.getBlue(), alpha));
					g.fillRect(x, y, 1, 1);
				}
			}

			g.dispose();

			this.color = field.getOwner().getColor();

			this.temp_img = img;
			return img;
		} else {
			return temp_img;
		}
	}

	@Override
	public void onClick() {
		Toolbar toolbar = field.getOwner().getToolbar();
		if (!(field.getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("New builder")) {
			toolbar.add(new ToolbarButton("New builder", BuilderCharacter.img, "Used to build cities, ports, mine, and lots of other things...\n\nCosts: "+BuilderCharacter.PRICE+" character points.") {
				@Override
				public void onClick() {
					Player player = field.getOwner();
					if (player.buyCharacter(BuilderCharacter.PRICE)) {
						player.buyCharacter(field, GameCharacter.BUILDER);
						
						Hex hex = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_NEW_CHARACTER, hex.q+","+hex.r+","+hex.s+";"+GameCharacter.BUILDER));
					} else {
						JOptionPane.showInternalMessageDialog(null, String.format("You need at least %d character points to buy this.", BuilderCharacter.PRICE), "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		if (!(field.getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("New worker")) {
			toolbar.add(new ToolbarButton("New worker", WorkerCharacter.img, "Used to work at mines, \n\nCosts: "+WorkerCharacter.PRICE+" charactr points.") {
				@Override
				public void onClick() {
					Player player = field.getOwner();
					if (player.buyCharacter(WorkerCharacter.PRICE)) {
						player.buyCharacter(field, GameCharacter.WORKER);
						
						Hex hex = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_NEW_CHARACTER, hex.q+","+hex.r+","+hex.s+";"+GameCharacter.WORKER));
					} else {
						JOptionPane.showInternalMessageDialog(null, String.format("You need at least %d character points to buy this.", WorkerCharacter.PRICE), "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		if (!(field.getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("New swordsman")) {
			toolbar.add(new ToolbarButton("New swordsman", SwordsmanCharacter.img, "A character made to fight...\n\nCosts: "+SwordsmanCharacter.RESOURCES[2]+" iron and "+SwordsmanCharacter.PRICE+" character points.") {
				@Override
				public void onClick() {
					Player player = field.getOwner();
					if (player.buyCharacter(SwordsmanCharacter.PRICE, SwordsmanCharacter.RESOURCES)) {
						player.buyCharacter(field, GameCharacter.SWORDSMAN);

						Hex hex = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_NEW_CHARACTER, hex.q+","+hex.r+","+hex.s+";"+GameCharacter.SWORDSMAN));
					} else {
						JOptionPane.showInternalMessageDialog(null, resourceMessage(SwordsmanCharacter.PRICE, SwordsmanCharacter.RESOURCES), "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
		if (!(field.getOwner().state == Player.STATE_CHARACTER_CLICKED) && !toolbar.hasButton("New rider")) {
			toolbar.add(new ToolbarButton("New rider", RiderCharacter.img, "A character on a horse...\n\nCosts: "+RiderCharacter.RESOURCES[0]+" coal and "+RiderCharacter.PRICE+" character points.") {
				@Override
				public void onClick() {
					Player player = field.getOwner();
					if (player.buyCharacter(RiderCharacter.PRICE, RiderCharacter.RESOURCES)) {
						player.buyCharacter(field, GameCharacter.RIDER);

						Hex hex = field.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_NEW_CHARACTER, hex.q+","+hex.r+","+hex.s+";"+GameCharacter.RIDER));
					} else {
						JOptionPane.showInternalMessageDialog(null, resourceMessage(RiderCharacter.PRICE, RiderCharacter.RESOURCES), "Not enough resourses...", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
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
		Map map = field.getOwner().map;
		Point pos = map.hexToDisplay(HexPanel.hexLayout.hexToPixel(field.getHex()));
		BufferedImage dyed = dye(img[lvl - 1], field.getOwner().getColor());

		g.drawImage(dyed, (int) (pos.x - (SIZE * zoom) / 2), (int) (pos.y - (SIZE * zoom) / 2), (int) (SIZE * zoom),
				(int) (SIZE * zoom), null);
	}
}
