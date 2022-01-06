package com.mieze.hexbattle;

import com.mieze.hexbattle.server.*;

import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.awt.Toolkit;
import javax.swing.*;

public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	public static int FPS = 40;

	public static final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

	private long lastTime;
	private long frameCount = 0;
	private static HexPanel panel;

	private static boolean error = false;

	private boolean isServer = false;
	private Server server = null;
	private Client client;

	public static void main(String[] args) {
		try {
			new Main();
		} catch (Throwable t) {
			handleException(t);
		}
	}

	public static void handleException(Throwable t) {
		if (t != null && !error) {
			error = true;
			javax.swing.JOptionPane.showMessageDialog(null, t.toString() + "\n\nMore infos in err.log", "ERROR", JOptionPane.ERROR_MESSAGE);
			
			try {
				java.io.FileWriter fstream = new java.io.FileWriter("log/err.log", true);
				java.io.BufferedWriter out = new java.io.BufferedWriter(fstream);
				out.write(t.toString() + '\n');
				out.close();

				java.io.PrintStream stream = new java.io.PrintStream(new java.io.File("err.log"));
				t.printStackTrace(stream);
				stream.close();

			} catch (Exception e) {
				javax.swing.JOptionPane.showMessageDialog(null, "An error occured during the save process of an error message:\n"+e.getClass() + ":\n" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			}

			System.exit(1);
		}
	}

	private void initServer() {
		System.out.print("Start as host? (y/n): ");
		Scanner scanner = new Scanner(System.in);
		String answer = "";

		if (scanner.hasNextLine()) answer = scanner.nextLine();
		
		if (answer.equalsIgnoreCase("y")) {
			isServer = true;
			server = new Server();
			server.start();
			client = new Client(server.getIp(), Server.PORT);
			try {
				server.getIp();
				System.out.println("IP: " + InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} else {
			isServer = false;
			System.out.print("Enter IP: ");
			String ip = "";
			if (scanner.hasNextLine()) ip = scanner.nextLine();
			client = new Client(ip, Server.PORT);
		}

		scanner.close();
	}

	public Main() {
		super("Hexbattle");
		initServer();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (client != null) client.close();
				if (server != null) server.close();
			}
		});

		if (!isServer) while (true)
			client.setEventListener(new Client.EventListener() {
				public void newEvent(Client.Event e) {
					System.out.println("Event: " + e);	
				}
			});

		if (isServer) while(true) {
			client.sendEvent(new Client.Event(Client.Event.EVENT_START, "Ping"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		System.exit(0);

		panel = new HexPanel();
		add(panel);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	       	setUndecorated(true);

		pack();
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

