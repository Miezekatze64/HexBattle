package com.mieze.hexbattle;

import java.awt.Graphics2D;
import java.awt.Color;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.UnexploredField;
import com.mieze.hexbattle.fields.ForestField;
import com.mieze.hexbattle.fields.WaterField;

import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Mine;
import com.mieze.hexbattle.fields.building.Port;
import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.characters.Boat;
import com.mieze.hexbattle.characters.BuilderCharacter;
import com.mieze.hexbattle.characters.WorkerCharacter;
import com.mieze.hexbattle.characters.SwordsmanCharacter;
import com.mieze.hexbattle.characters.RiderCharacter;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Layout;

import com.mieze.hexbattle.hex.Point;

import com.mieze.hexbattle.server.Client.Event;

import com.mieze.hexbattle.toolbars.Toolbar;
import com.mieze.hexbattle.toolbars.Inventory;

public class Player {
	public Map map;

	protected ArrayList<GameCharacter> characters;
	protected ArrayList<Hex> fields;
	protected ArrayList<UnexploredField> unexplored;
	public ArrayList<Hex> active;
	private ArrayList<Hex> empire;

	private Toolbar toolbar;
	private Inventory inventory;

	private Hex start_pos;
	private Layout hexLayout;

	public static final int STATE_START = 0;
	public static final int STATE_CHARACTER_CLICKED = 1;
	public static final int STATE_OTHER_PLAYER = 2;
	public static final int STATE_NOT_IMPLEMENTED = 3;

	public int state = STATE_OTHER_PLAYER;
	private int city_count = 0;
	private Color playerColor;
	private boolean isMain = false;
	private boolean alive = true;
	public boolean boat_leave = false;

	public GameCharacter clickedCharacter;

	public Player(Map map, Layout layout, Color color) {
		this(map, layout, color, false);
	}

	public Player(Map map, Layout layout, Color color, boolean isMain) {
		this(map, layout, color, isMain, null);
	}

	public Player(Map map, Layout layout, Color color, boolean isMain, Hex position) {
		this.fields = new ArrayList<Hex>();
		this.unexplored = new ArrayList<UnexploredField>();
		this.active = new ArrayList<Hex>();
		this.characters = new ArrayList<GameCharacter>();
		this.empire = new ArrayList<Hex>();

		this.toolbar = new Toolbar(this);
		this.inventory = new Inventory();
		this.map = map;
		this.isMain = isMain;
		this.playerColor = color;
		this.hexLayout = layout;

		setStartFields(position);
		//First character (builder)
		addCharacter(new BuilderCharacter(map.getField(start_pos), hexLayout, this));
	}
	
	public void addCharacter(GameCharacter c) {
		characters.add(c);
	}

	public void checkKill() {
		if (city_count == 0 && alive) {
			alive = false;
			JOptionPane.showMessageDialog(null, (!isMain)?("Player " + colorName(playerColor) + " died"):("You died..."), "DEAD", JOptionPane.INFORMATION_MESSAGE);
			for (int i = 0; i < characters.size(); i++) {
				map.getField(characters.get(i).getPosition()).removeCharacter();
			}
			characters.removeAll(characters);
		}
	}

	public static String colorName(Color c) {
		for (java.lang.reflect.Field f : Color.class.getDeclaredFields()) {
			//we want to test only fields of type Color
			if (f.getType().equals(Color.class))
				try {
					if (f.get(null).equals(c))
						return f.getName().toLowerCase();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// shouldn't not be thrown, but just in case print its stacktrace
					e.printStackTrace();
				}
		}
		return "<unknown>";
	}

	public void setStartFields(Hex position) {
		boolean found = false;

		if (position != null) {
			found = true;
			this.start_pos = position;
		}

		while (!found) {
			int q = (int) (Math.random() * 10);
			int r = (int) (Math.random() * 10);
			int s = -q - r;

			this.start_pos = new Hex(q, r, s);

			if (map.getType(start_pos) != Field.EMPTY) {
				continue;
			}

			found = true;
			for (int i = 0; i < 6; i++) {
				if (map.getField(start_pos.neighbor(i)) != null && map.getField(start_pos.neighbor(i)).hasOwner()) {
					found = false;
				}
			}
		}

		if (isMain) {
			Point start_point = hexLayout.hexToPixel(start_pos);
			map.addOffset(-(int)start_point.x, -(int)start_point.y);
		}

		addField(start_pos, true);

		conquerCity(start_pos);

	}

	public void newEvent(Event e) {
		switch (e.getType()) {
		case Event.EVENT_GAME_MOVE:
		{
			String[] hexarr = e.getValue().split(";");
			String[] h1 = hexarr[0].split(",");
			String[] h2 = hexarr[1].split(",");

			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));
			Hex hex2 = new Hex(Integer.parseInt(h2[0]), Integer.parseInt(h2[1]), Integer.parseInt(h2[2]));

			Field f1 = map.getField(hex1, true);
			Field f2 = map.getField(hex2, true);
			GameCharacter character = f1.getCharacter();

			f1.removeCharacter();
			character.moveTo(f2);

			if (f2.hasCharacter() && f2.getCharacter() instanceof Boat && f2.getCharacter().isFromPlayer(character.getPlayer())) {
				((Boat)f2.getCharacter()).setCharacter(character);
			} else {
				f2.setCharacter(character);
			}

			if (character instanceof Boat) {
				if (((Boat)character).hasCharacter()) {
					GameCharacter char2 = ((Boat)character).getCharacter();

					char2.moveTo(f2);
					char2.setMoved(true);
				}
			}
		}
		break;
		case Event.EVENT_GAME_ATTACK:
		{
			String[] hexarr = e.getValue().split(";");
			String[] h1 = hexarr[0].split(",");
			String[] h2 = hexarr[1].split(",");

			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));
			Hex hex2 = new Hex(Integer.parseInt(h2[0]), Integer.parseInt(h2[1]), Integer.parseInt(h2[2]));

			Field f1 = map.getField(hex1, true);
			Field f2 = map.getField(hex2, true);
			GameCharacter character1 = f1.getCharacter();
			GameCharacter character2 = f2.getCharacter();
			
			attack(character1, character2, true);
		}
		break;
		case Event.EVENT_GAME_NEW_CHARACTER:
		{
			String[] hexarr = e.getValue().split(";");
			String[] h1 = hexarr[0].split(",");
			String character = hexarr[1];

			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));
			int charInt = Integer.parseInt(character);

			Field f1 = map.getField(hex1, true);
			
			buyCharacter(f1, charInt);
		}
		break;
		case Event.EVENT_GAME_CONQUER_CITY:
		{
			String[] h1 = e.getValue().split(",");
			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));

			conquerCity(hex1, true);
		}
		break;
		case Event.EVENT_GAME_NEW_PORT:
		{
			String[] h1 = e.getValue().split(",");
			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));

			map.getField(hex1, true).setBuilding(new Port(map.getField(hex1)));
		}
		break;
		case Event.EVENT_GAME_LEAVE_BOAT:
		{
			String[] hexarr = e.getValue().split(";");
			String[] h1 = hexarr[0].split(",");
			String[] h2 = hexarr[1].split(",");

			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));
			Hex hex2 = new Hex(Integer.parseInt(h2[0]), Integer.parseInt(h2[1]), Integer.parseInt(h2[2]));

			Field f1 = map.getField(hex1, true);
			Field f2 = map.getField(hex2, true);
			Boat boat = (Boat)f1.getCharacter();
			GameCharacter character = boat.getCharacter();

			character.moveTo(f2);
			System.out.println("Moved to " + f2.getHex());
			boat.removeCharacter();
			f2.setCharacter(character);
			boat.setMoved(true);
			
			character.setMoved(true);
		}
		break;
		case Event.EVENT_GAME_BUILD_MINE:
		{
			String[] hexarr = e.getValue().split(";");
			String[] h1 = hexarr[0].split(",");
			Hex hex1 = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));
			int type = Integer.parseInt(hexarr[1]);

			map.getField(hex1, true).setBuilding(new Mine(map.getField(hex1), type));
		}
		break;
		case Event.EVENT_GAME_CHOP_WOOD:
		{
			String[] h1 = e.getValue().split(",");
			Hex hex = new Hex(Integer.parseInt(h1[0]), Integer.parseInt(h1[1]), Integer.parseInt(h1[2]));

			((ForestField)map.getField(hex, true)).chop();
		}
		break;
		}
	}
	
	public Hex getPosition() {
		return this.start_pos;
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	private void addToEmpire(Hex hex) {
		if (map.getField(hex).getOwner() != this) {
			empire.add(hex);
			map.getField(hex).setOwner(this);
		}
	}

	public Color getColor() {
		return this.playerColor;
	}

	public void buyCharacter(Field field, int type) {
		GameCharacter newCharacter;
		switch (type) {
			case GameCharacter.BUILDER:
				newCharacter = new BuilderCharacter(field, HexPanel.hexLayout, field.getOwner());
				break;
			case GameCharacter.WORKER:
				newCharacter = new WorkerCharacter(field, HexPanel.hexLayout, field.getOwner());
				break;
			case GameCharacter.SWORDSMAN:
				newCharacter = new SwordsmanCharacter(field, HexPanel.hexLayout, field.getOwner());
				break;
			case GameCharacter.RIDER:
				newCharacter = new RiderCharacter(field, HexPanel.hexLayout, field.getOwner());
				break;
			case GameCharacter.BOAT:
				newCharacter = new Boat(field, HexPanel.hexLayout, field.getOwner());
				break;
			default:
			throw new IllegalStateException("Constant not implemented!");
		}
		newCharacter.setMoved(true);
		field.setCharacter(newCharacter);
		field.getOwner().addCharacter(newCharacter);
	}

	public void render(Graphics2D g) {
		
		for (int i = 0; i < map.fields.size(); i++) {
			if (fields.contains(map.fields.get(i).getHex()) && isOnScreen(map.fields.get(i))) {
				map.fields.get(i).render(g);
			}
		}

		for (int i = 0; i < unexplored.size(); i++) {
			unexplored.get(i).render(g);
		}

		for (int i = 0; i < map.fields.size(); i++) {
			if (fields.contains(map.fields.get(i).getHex()) && isOnScreen(map.fields.get(i))) {
				if (map.fields.get(i).getCharacter() != null)
					map.fields.get(i).getCharacter().render(g, map.zoom);
			}
		}

		for (int i = 0; i < active.size(); i++) {

			Hex hex = active.get(i);

			Point p = map.hexToDisplay(hexLayout.hexToPixel(hex));
			int point_x = (int) p.x;
			int point_y = (int) p.y;
			
			double w = (hexLayout.size.x * map.zoom) / 2;
			double h = (hexLayout.size.x * map.zoom) / 2;

			int left = (int) (point_x - w / 2);
			int top = (int) (point_y - h / 2);

			g.setColor(Color.ORANGE);
			((Graphics2D) g).fillOval(left, top, (int) w, (int) h);
			g.setColor(Color.BLACK);
		}
		toolbar.render(g, map);
		inventory.render(g, map);
	}

	protected boolean isOnScreen(Field f) {
		return f.isOnScreen(map.offset_x, map.offset_y, map.zoom);
	}
	
	public void removeCharacter(GameCharacter character) {
		characters.remove(character);
	}

	public void reset() {
		if (clickedCharacter != null) {
			clickedCharacter.setMoved(true);
		}
		clickedCharacter = null;
		state = STATE_START;
		active.removeAll(active);
	}

	public void leaveBoat(Field f) {
		active.removeAll(active);

		GameCharacter character = ((Boat)f.getCharacter()).getCharacter();
		character.setPossibleFields();
		clickedCharacter = character;
		
		boat_leave = true;

		state = STATE_CHARACTER_CLICKED;
	}

	public void onClick(Point p) {
		if (state == STATE_OTHER_PLAYER) return;

		if (toolbar.onClick((int) p.x, (int) p.y, map)) {
			toolbar.reset();
			return;
		}
		toolbar.reset();

		Point realPoint = map.displayToHex(p);

		Hex hex = hexLayout.pixelToHex(realPoint).hexRound();
		Field f = map.getField(hex);

		if (f != null) {
			if (f.hasBuilding()) {
				if (!f.hasCharacter()) {
					f.getBuilding().onClick();
				}
			} else {
				if (f instanceof WaterField) {
					if (!f.hasCharacter() && f.hasOwner() && f.getOwner() == this) {
						((WaterField)f).onClick();
					}
				}
			}
		}

		switch (state) {
		case STATE_START:
			boat_leave = false;
			if (f != null) {
				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter() && f.getCharacter().isFromPlayer(this) && !f.getCharacter().isMoved()) {
						GameCharacter character = f.getCharacter();
						character.setPossibleFields();
						clickedCharacter = character;
						
						clickedCharacter.checkAndAddTools(toolbar);
						
						state = STATE_CHARACTER_CLICKED;
						break;
					}
				}
			}
			clickedCharacter = null;
			state = STATE_START;
			break;
		case STATE_CHARACTER_CLICKED:
			if (clickedCharacter == null) {
				throw new IllegalStateException("A player connot be click and unexisting at the same time (unreaachable)!!");
			} else if (f == null) {
				// TODO: handle empty click (maybe...)
				boat_leave = false;
			} else {
				// check if field is in range
				if (!active.contains(f.getHex())) {
					boat_leave = false;
					clickedCharacter = null;
					state = STATE_START;
					active.removeAll(active);
					break;
				}

				if (!(f instanceof UnexploredField)) {
					if (f.hasCharacter()) {
						if (f.getCharacter().isFromPlayer(this)) {
							if (f.getCharacter() instanceof Boat) {
								boat_leave = false;
								Hex before = clickedCharacter.getPosition();

								clickedCharacter.moveTo(f);
								active.removeAll(active);
								map.getField(clickedCharacter.getPosition()).removeCharacter();
								((Boat)f.getCharacter()).setCharacter(clickedCharacter);
								clickedCharacter.setMoved(true);

								Hex after = f.getHex();
								Main.client.sendEvent(new Event(Event.EVENT_GAME_MOVE, before.q+","+before.r+","+before.s+";"+after.q+","+after.r+","+after.s));
								break;
							}

							boat_leave = false;
							active.removeAll(active);
							GameCharacter character = f.getCharacter();
							character.setPossibleFields();
							clickedCharacter = character;
							state = STATE_CHARACTER_CLICKED;
							break;
						} else {
							boat_leave = false;
							attack(clickedCharacter, f.getCharacter(), false);
							active.removeAll(active);
							clickedCharacter.setMoved(true);
							state = STATE_START;
							break;
						}
					} else {
						if (f instanceof WaterField) {
/*							if (f.getCharacter() instanceof Boat) {
								()
							}
							break;
*/						}

						Hex before = clickedCharacter.getPosition();

						if (boat_leave) {
							clickedCharacter.moveTo(f);
							((Boat)map.getField(before).getCharacter()).removeCharacter();
							f.setCharacter(clickedCharacter);
							clickedCharacter.setMoved(true);

							map.getField(before).getCharacter().setMoved(true);
							active.removeAll(active);

							Hex h = f.getHex();
							Main.client.sendEvent(new Event(Event.EVENT_GAME_LEAVE_BOAT, before.q+","+before.r+","+before.s+";"+h.q+","+h.r+","+h.s));
							break;
						}

						// move to next field
						clickedCharacter.moveTo(f);
						active.removeAll(active);
						map.getField(clickedCharacter.getPosition()).removeCharacter();
						f.setCharacter(clickedCharacter);
						clickedCharacter.setMoved(true);

						if (clickedCharacter instanceof Boat) {
							if (((Boat)clickedCharacter).hasCharacter()) {
								GameCharacter char2 = ((Boat)clickedCharacter).getCharacter();

								char2.moveTo(f);
								char2.setMoved(true);
							}
						}

						openSurroundedFields(f.getHex());

						Hex after = f.getHex();
						Main.client.sendEvent(new Event(Event.EVENT_GAME_MOVE, before.q+","+before.r+","+before.s+";"+after.q+","+after.r+","+after.s));

						state = STATE_START;
						break;
					}
				} else {
					// TODO: handle UnexploredField click
				}
			}
			clickedCharacter = null;
			state = STATE_START;
			active.removeAll(active);
			break;
		}
	}
	
	public void attack(GameCharacter attacker, GameCharacter target, boolean fromEvent) {
		if (!fromEvent) Main.client.sendEvent(new Event(Event.EVENT_GAME_ATTACK, attacker.getPosition().q+","+attacker.getPosition().r+","+attacker.getPosition().s+";"+target.getPosition().q+","+target.getPosition().r+","+target.getPosition().s));

		double attack = attacker.getAttackScore() * (attacker.getHealth() / attacker.getInitialLife());
		double defense = target.getDefenceScore() * (target.getHealth() / target.getInitialLife());
		double damage = attack + defense;

		double nextHealth = target.getHealth()-
			(attack / damage) * attacker.getAttackScore()*0.8;
		
		target.setHealth(nextHealth);
		if (nextHealth <= 0) {
			Field f = map.getField(target.getPosition());
			map.getField(attacker.getPosition()).removeCharacter();
			f.setCharacter(attacker);
			attacker.moveTo(f);

			attacker.setMoved(true);
			openSurroundedFields(f.getHex());
		}
	}

	public void yourTurn(boolean first) {
		state = STATE_START;
		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).setMoved(false);
		}

		if (!first) inventory.addResources(Inventory.CHARPOINTS, 0.5 * getCityCount());
	}
	
	// only character points
	public boolean buyCharacter(int amount) {
		if (inventory.getCharacterPoints() < amount) {
			return false;
		} else {
			inventory.setCharacterPoints(inventory.getCharacterPoints()-amount);
			return true;
		}
	}

	//character points + other resources
	public boolean buyCharacter(int amount, int[] resources) {
		if (resources.length != 4) throw new IllegalArgumentException("Array must have length of 4!");

		
		if (inventory.getCharacterPoints() < amount) {
			return false;
		} else {
			inventory.setCharacterPoints(inventory.getCharacterPoints()-amount);
		}
		if (!payResourses(resources)) return false;
		
		return true;
	}

	public boolean payResourses(int[] amount) {
		int resource = 0;
		boolean rt = true;
		for (int num : amount) {
			if (inventory.getResources(resource) < num) {
				rt = false;
			}
		}
		if (rt == false) return false;
		for (int num : amount) {
			inventory.subResources(resource, num);
			resource++;
		}
		return true;
	}

	public void addResourses(int[] amount) {
		int resource = 0;
		for (int num : amount) {
			inventory.addResources(resource, num);
			resource++;
		}
	}

	public int getCityCount() {
		return city_count;
	}

	public void setCityCount(int cc) {
		this.city_count = cc;
	}

	public void openSurroundedFields(Hex h) {
		for (int i = 0; i < 6; i++) {
			addField(h.neighbor(i), true);
		}
	}

	public void openAndConquerSurroundedFields(Hex h) {
		Player ownerOfCity = map.getField(h).getOwner();
		for (int i = 0; i < 6; i++) {
			addField(h.neighbor(i), true);
			if (ownerOfCity == map.getField(h.neighbor(i)).getOwner()) addToEmpire(h.neighbor(i));
		}
		addToEmpire(h);
	}

	public void addField(Point p) {
		addField(p, false);
	}

	public void addField(Point p, boolean force) {
		Hex hex = hexLayout.pixelToHex(p).hexRound();
		addField(hex, force);
	}

	public void addField(Hex hex, boolean force) {
		if ((force || isUnexplored(hex)) && !fields.contains(hex)) {
			this.fields.add(hex);
			removeUnexploredField(hex);
			map.addField(hex);

			for (int i = 0; i < 6; i++) {
				if (!fields.contains(hex.neighbor(i))) {
					addUnexploredField(hex.neighbor(i));
				}
			}
		}
	}

	public void conquerCity(Hex h) {
		conquerCity(h, false);
	}

	public void conquerCity(Hex h, boolean fromEvent) {
		Player player;
		if (map.getField(h) == null) return;

		GameCharacter c = map.getField(h).getCharacter();
		
		if (c == null)
			player = this;
		else
			player = c.getPlayer();

		if (player == map.getField(h).getOwner()) return;
		
		if (!fromEvent) Main.client.sendEvent(new Event(Event.EVENT_GAME_CONQUER_CITY, h.q+","+h.r+","+h.s));

		player.city_count++;
		if (map.getField(h).getOwner() != null) map.getField(h).getOwner().city_count--;
		map.getField(h).setBuilding(new City(map.getField(h)));
		player.openAndConquerSurroundedFields(h);
	}

	private boolean isUnexplored(Hex hex) {
		for (int i = 0; i < unexplored.size(); i++) {
			if (unexplored.get(i).getHex().equals(hex)) {
				return true;
			}
		}
		return false;
	}

	public void activate(Hex h) {
		if (!active.contains(h) && !isUnexplored(h) && map.getField(h) != null && (!map.getField(h).hasCharacter() || !map.getField(h).getCharacter().isFromPlayer(this) || map.getField(h).getCharacter() instanceof Boat)) {
			active.add(h);
		}
	}

	public void deactivate(Hex h) {
		if (active.contains(h)) {
			active.remove(h);
		}
	}

	public void deactivateAll() {
		active.removeAll(active);
	}

	private void removeUnexploredField(Hex hex) {
		for (int i = 0; i < unexplored.size(); i++) {
			if (unexplored.get(i).getHex().equals(hex)) {
				unexplored.remove(i);
			}
		}
	}

	private void addUnexploredField(Hex hex) {
		if (!isUnexplored(hex)) {
			unexplored.add(new UnexploredField(hex, map));
		}
	}

    public void mouseMoved(Point point) {
		toolbar.mouseMoved(point, map);
		inventory.mouseMoved(point, map);
    }

	public boolean isDead() {
		return !alive;
	}
}
