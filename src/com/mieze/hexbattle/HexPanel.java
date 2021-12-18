package com.mieze.hexbattle;

import java.awt.*;

import java.awt.event.*;

import com.mieze.hexbattle.hex.*;
import com.mieze.hexbattle.hex.Point;
import javax.swing.*;

public class HexPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Map map;
	private Player player;

	private int last_x = 0, last_y = 0;
	private boolean drag = false;
	public static Layout hexLayout;
	
	private static final float ZOOM_CONSTANT = 1.2f;
	private double fps = Main.FPS;
	
	private float off_x;
	private float off_y;
	
	private static final Color[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GRAY};
	private int colorIndex = 0;
	
	private boolean first = true;
	
	static {
		hexLayout = new Layout(Layout.pointy, new Point(40, 40), new Point(0, 0));
	}
	
	private Color getNextColor() {
		try {
			return COLORS[colorIndex++];
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException("Too many players!");
		}
	}

	public HexPanel() {
		off_x = -Main.WIDTH/2f;
		off_y = -Main.HEIGHT/2f;
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				map.addZoom(Math.pow(ZOOM_CONSTANT, -e.getPreciseWheelRotation()));
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag) {
		
					off_x = e.getX() - last_x;
					off_y = e.getY() - last_y;
					
					last_x = e.getX();
					last_y = e.getY();
					
					map.addOffset((int)(off_x/map.zoom), (int)(off_y/map.zoom));
				}
			}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				drag = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1)
					player.onClick(new Point(e.getX(), e.getY()));
				if (e.getButton() != MouseEvent.BUTTON1) {
					last_x = e.getX();
					last_y = e.getY();
					drag = true;
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		createMap();
		player = new Player(map, hexLayout, getNextColor());
	
	}

	public void createMap() {
		map = new Map(-250, -250, this);
	}
	
	public void currentFPS(double fps) {
		this.fps = fps;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (first) {
			((Graphics2D)getGraphics()).setComposite(AlphaComposite.Clear);
			first = false;
		}
		player.render((Graphics2D)g);
		renderFPS(g);
	}
	
	public void renderFPS(Graphics g) {
		((Graphics2D)g).drawString("FPS: " + fps, 10, 20);
	}
}
