package com.mieze.hexbattle.fields.building;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Map;
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
					g.setColor(
							new Color(gray | color.getRed(), gray | color.getGreen(), gray | color.getBlue(), alpha));
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
		// TODO Implement city options
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
