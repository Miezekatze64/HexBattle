package com.mieze.hexbattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionListener;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;

import com.mieze.hexbattle.hex.Point;

import com.mieze.hexbattle.server.Client;
import com.mieze.hexbattle.server.Client.Event;

import javax.swing.*;

public class HexPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Map map;
	private Player player;
	private ArrayList<Player> opponents;

	private int last_x = 0, last_y = 0;
	private boolean drag = false;
	public static Layout hexLayout;
	
	private static final float ZOOM_CONSTANT = 1.2f;
	private double fps = Main.FPS;
	
	private float off_x;
	private float off_y;
	private int player_index;
	private int current_index = 0;
	
	private static final Color[] COLORS = {Color.RED, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GRAY};
	private int colorIndex = 0;
	private boolean first = true;
	private boolean started = false;
	private boolean gameStarted = false;

	private Player currentPlayer = null;
	protected ArrayList<String> connected = new ArrayList<>();
	private String name = "";
	
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

	}

	public void init(String name) {
		this.name = name;

		opponents = new ArrayList<Player>();
		
		off_x = -Main.WIDTH/2f;
		off_y = -Main.HEIGHT/2f;

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (map != null) map.addZoom(Math.pow(ZOOM_CONSTANT, -e.getPreciseWheelRotation()));
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				try {
					if (map != null && player != null) player.mouseMoved(new Point(e.getX(), e.getY()));
				} catch (Throwable t) {
					Main.handleException(t);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (drag && map != null) {
		
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
				try {
					if (map != null) {
						if (e.getButton() == MouseEvent.BUTTON1)
							player.onClick(new Point(e.getX(), e.getY()));
						if (e.getButton() != MouseEvent.BUTTON1) {
							last_x = e.getX();
							last_y = e.getY();
							drag = true;
						}
					}
				} catch (Throwable t) {
					Main.handleException(t);
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
		
		if (!Main.isHost)
		Main.client.setEventListener(new Client.EventListener() {

			@Override
			public void newEvent(Event e) {
				try {
					if (currentPlayer != null) currentPlayer.newEvent(e);
					if (e.getType().startsWith("start") && started) {
						return;
					}
					switch(e.getType()) {
					case Event.EVENT_END_TURN:
						nextTurn(true);
					break;
					case Event.EVENT_START_PLAYER:
						{
							String[] split = e.getValue().split(",");
							newPlayer(new Hex(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
							
							if (currentPlayer == null) {
								currentPlayer = opponents.get(opponents.size()-1);
							}
						}
					break;
					case Event.EVENT_START_SEED:
						createMap(Long.parseLong(e.getValue()));
					break;
					case Event.EVENT_START_PLAYER_END:
						started = true;
						player = new Player(map, hexLayout, getNextColor(), true);
							player_index = opponents.size();
							Main.client.sendEvent(new Event(Event.EVENT_ADD_PLAYER, player.getPosition().q + "," + player.getPosition().r+","+player.getPosition().s));
							currentPlayer.yourTurn(true);
					break;
					case Event.EVENT_ADD_PLAYER: 
						{
							String[] split = e.getValue().split(",");
							newPlayer(new Hex(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
						}
					break;
					case Event.EVENT_CONNECTED_CHANGED:
						String[] split = e.getValue().split(",");
						connected = new ArrayList<>(Arrays.asList(split));
						Main.getInstance().updateConnectedList(connected);
					break;
					case Event.EVENT_GET_CONNECTED:
						System.out.println("here3");
						Main.client.sendEvent(new Event(Event.EVENT_SEND_NAME, name));
					break;
					case Event.EVENT_SERVER_CLOSE:
						JOptionPane.showMessageDialog(null, "Room closed by host.");
						Main.getInstance().leaveGame();
					break;
					case Event.EVENT_GAME_START:
						Main.getInstance().showGame();
					break;
					case Event.EVENT_ALREADY_STARTED:
						if (!gameStarted) {
							JOptionPane.showMessageDialog(null, "Game already started");
							Main.getInstance().leaveGame();
						}
					break;
					default:
					break;
				}
				} catch(Exception e1) {
					Main.handleException(e1);
				}
			}
		});
		else
		Main.client.setEventListener(new Client.EventListener() {
			@Override
			public void newEvent(Event e) {
				try {
					if (currentPlayer != null) currentPlayer.newEvent(e);
					switch(e.getType()) {
					case Event.EVENT_END_TURN:
						nextTurn(true);
					break;
					case Event.EVENT_JOIN:
						{
							if (gameStarted) {
								Main.client.sendEvent(new Event(Event.EVENT_ALREADY_STARTED, ""));
								break;
							}

							String name = e.getValue();
							connected.add(name);

							Main.getInstance().updateConnectedList(connected);
							Main.client.sendEvent(new Event(Event.EVENT_CONNECTED_CHANGED, String.join(",", connected)));
							Main.client.sendEvent(new Event(Event.EVENT_START_SEED, map.getSeed()+""));
							Main.client.sendEvent(new Event(Event.EVENT_START_PLAYER, player.getPosition().q + "," + player.getPosition().r+","+player.getPosition().s));

							for (int i = 0; i < opponents.size(); i++) {
								Main.client.sendEvent(new Event(Event.EVENT_START_PLAYER, opponents.get(i).getPosition().q + "," + opponents.get(i).getPosition().r+","+opponents.get(i).getPosition().s));
							}

							Main.client.sendEvent(new Event(Event.EVENT_START_PLAYER_END, ""));
						}
					break;
					case Event.EVENT_ADD_PLAYER:
						String[] split = e.getValue().split(",");
						newPlayer(new Hex(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
					break;
					case Event.EVENT_SEND_NAME:
						{
							System.out.println("here4");
							String name = e.getValue();
							connected.add(name);
							Main.getInstance().updateConnectedList(connected);
							Main.client.sendEvent(new Event(Event.EVENT_CONNECTED_CHANGED, String.join(",", connected)));
							validate();
						}
					break;
					default:
					break;
					}
				} catch(Exception e1) {
					Main.handleException(e1);
				}
			}
		});
		
		if (Main.isHost) {
			createMap();
			player = new Player(map, hexLayout, getNextColor(), true);
			currentPlayer = player;
			currentPlayer.yourTurn(true);
			player_index = 0;
			connected.add(name + " [HOST]");
			Main.getInstance().updateConnectedList(connected);
		} else {
			Main.client.sendEvent(new Event(Client.Event.EVENT_JOIN, name));
		}
	}

	public void newPlayer(Hex hex) {
		opponents.add(new Player(map, hexLayout, getNextColor(), false, hex));
	}

	public void createMap() {
		map = new Map(-Main.WIDTH/2, -Main.HEIGHT/2, this);
	}

	public void createMap(long seed) {
		map = new Map(-Main.WIDTH/2, -Main.HEIGHT/2, this, seed);
	}
	
	public void currentFPS(double fps) {
		this.fps = fps;
	}

	public void nextTurn() {
		nextTurn(false);
	}

	public void disconnected() {
		if (connected == null) connected = new ArrayList<>();
		connected.removeAll(connected);
		connected.add(name);
		Main.client.sendEvent(new Event(Event.EVENT_GET_CONNECTED, ""));
		Main.getInstance().updateConnectedList(connected);
		validate();
	}
	
	public void nextTurn(boolean fromEvent) {
		currentPlayer.state = Player.STATE_OTHER_PLAYER;
		if (!fromEvent) Main.client.sendEvent(new Event(Event.EVENT_END_TURN, ""));

		current_index = (current_index + 1)%(opponents.size()+1);
		
		if (current_index == player_index) {
			currentPlayer = player;
		} else if (current_index < player_index) {
			currentPlayer = opponents.get(current_index);
		} else {
			currentPlayer = opponents.get(current_index-1);
		}

		player.checkKill();
		for (int i = 0; i < opponents.size(); i++) {
			opponents.get(i).checkKill();
		}

		if (currentPlayer == player) player.yourTurn(false);
	}

	public void started() {
		gameStarted = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (map != null && player != null) {
			try {
				if (first) {
					((Graphics2D)getGraphics()).setComposite(AlphaComposite.Clear);
					first = false;
				}
				player.render((Graphics2D)g);
				renderFPS(g);
			} catch (Exception e) {
				Main.handleException(e);
			}
		}
	}
	
	public void renderFPS(Graphics g) {
		((Graphics2D)g).drawString("FPS: " + fps, 10, 20);
	}
}
