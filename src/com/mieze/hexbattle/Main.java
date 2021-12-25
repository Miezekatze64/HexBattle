package com.mieze.hexbattle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	public static int FPS = 60;

	private long lastTime;
	private long frameCount = 0;
	private static HexPanel panel;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		super("Hexbattle");

		panel = new HexPanel();
		add(panel);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setSize(WIDTH, HEIGHT);
		setVisible(true);

		Timer gameLoop = new Timer(1000 / FPS, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				
				frameCount++;
				if (frameCount % 50 == 0)
					calcFPS(System.currentTimeMillis());
			}
		});

		gameLoop.start();
	}
	
	public static HexPanel getPanel() {
		return panel;
	}

	private void calcFPS(long time) {
		double fps = 1000.0 / (System.currentTimeMillis() - lastTime)*50;
		lastTime = System.currentTimeMillis();
		
		panel.currentFPS((int)fps);
	}
}

