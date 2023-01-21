package com.mieze.hexbattle.client;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.client.render.ClientRenderer;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.net.ClientConnection;
import com.mieze.hexbattle.net.Event;

public class Client {
    private ClientConnection connection = null;
    private ClientWorldData worldData;
    private boolean isTurn = true;
    private ClientRenderer renderer;

    public Client() {
        this.worldData = new ClientWorldData(this);
        this.renderer  = new ClientRenderer(this);
    }

    public ClientRenderer getRenderer() {
        return this.renderer;
    }

    public void connect(String host, int port) throws IOException {
        this.connection = new ClientConnection(host, port);

        connection.setEventListener(Event.C_NAMES, e -> {
            List<String> players = new ArrayList<>();
            byte[] value = e.getValue();

            for (int i = 0; i < value.length; i++) {
                int length = (int) value[i];
                if (length == 0) break;
                String working = "";
                for (int ii = 1; ii <= length; ii++) {
                    working += (char) value[i + ii];
                }
                players.add(working);
                i += length;
            }

            System.out.println(players);
            Main.getInstance().updateConnectedList(players);

            // update players
            this.worldData.resetPlayers();
            for (String s : players) {
                this.worldData.addPlayer(s, new ClientPlayer(s, Color.RED, worldData.getMap()));
            }
        });

        connection.setEventListener(Event.C_GAME_START, e -> {
            System.out.println("[client] server started GAME!!");
            Main.getInstance().showGame();
        });

        connection.setEventListener(Event.C_EXPLORED_FIELD, e -> {
            var buffer = ByteBuffer.wrap(e.getValue());
            int q = buffer.getInt();
            int r = buffer.getInt();
            int s = buffer.getInt();

            int nameLength = buffer.getInt();
            byte[] bytes = new byte[nameLength];
            buffer.get(bytes);

            System.out.printf("Got field: (%d, %d, %d), %s\n", q, r, s, new String(bytes));
            this.worldData.getMap().addField(new Hex(q, r, s), new String(bytes));
        });

        connection.setEventListener(Event.C_OCCUPY_FIELD, e -> {
            var buffer = ByteBuffer.wrap(e.getValue());
            int q = buffer.getInt();
            int r = buffer.getInt();
            int s = buffer.getInt();

            int nameLength = buffer.getInt();
            byte[] bytes = new byte[nameLength];
            buffer.get(bytes);

            System.out.printf("Occupied field: (%d, %d, %d), %s\n", q, r, s, new String(bytes));
            this.worldData.getMap().getField(new Hex(q, r, s)).setOwner(worldData.getPlayer(new String(bytes)));
        });

        connection.setEventListener(Event.C_SPAWN_CHARACTER, e -> {
            var buffer = ByteBuffer.wrap(e.getValue());
            int q = buffer.getInt();
            int r = buffer.getInt();
            int s = buffer.getInt();

            int idLength = buffer.getInt();
            byte[] idBytes = new byte[idLength];
            buffer.get(idBytes);

            int nameLength = buffer.getInt();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);

            System.out.printf("Spawned character: (%d, %d, %d), %s\n", q, r, s, new String(idBytes));
            this.worldData.getMap().getField(new Hex(q, r, s)).spawnCharacter(new String(idBytes), worldData.getPlayer(new String(nameBytes)));
        });

        connection.setEventListener(Event.C_SPAWN_BUILDING, e -> {
            var buffer = ByteBuffer.wrap(e.getValue());
            int q = buffer.getInt();
            int r = buffer.getInt();
            int s = buffer.getInt();

            int idLength = buffer.getInt();
            byte[] idBytes = new byte[idLength];
            buffer.get(idBytes);

            System.out.printf("Spawned building: (%d, %d, %d), %s\n", q, r, s, new String(idBytes));
            this.worldData
                .getMap()
                .getField(new Hex(q, r, s))
                .spawnBuilding(new String(idBytes));
        });

        connection.setEventListener(Event.C_GAME_MOVE, e -> {
            var buffer = ByteBuffer.wrap(e.getValue());
            int aq = buffer.getInt();
            int ar = buffer.getInt();
            int as = buffer.getInt();

            int bq = buffer.getInt();
            int br = buffer.getInt();
            int bs = buffer.getInt();

            Hex a = new Hex(aq, ar, as);
            Hex b = new Hex(bq, br, bs);

            Field startField = worldData.getMap().getField(a);
            if (startField == null) {
                System.err.printf("[client, ERR] Move from unloaded field [%s]\n", startField);
                return;
            }

            Field endField = worldData.getMap().getField(b);
            if (endField == null) {
                System.err.printf("[client, ERR] Move to unloaded field [%s]\n", endField);
                return;
            }

            if (!startField.hasCharacter()) {
                System.err.printf("[client, ERR] Tried to move nonexistend character [at %s]\n", startField);
                return;
            }

            startField.getCharacter().moveTo(endField);
            renderer.setClickedCharacter(null);
        });
    }

    public boolean isTurn() {
        return isTurn;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public ClientWorldData getWorldData() {
        return this.worldData;
    }

    public void onClick(Point p) {
        if (worldData.getToolbar().onClick((int) p.x, (int) p.y, worldData.getMap())) {
            worldData.getToolbar().reset();
            return;
        }
        worldData.getToolbar().reset();

        Point realPoint = worldData.getMap().displayToHex(p);
        Hex hex = HexPanel.hexLayout.pixelToHex(realPoint).hexRound();
        Field f = worldData.getMap().getField(hex);

        if (f == null) return;
        if (renderer.getClickedCharacter() != null && worldData.getMap().canMoveTo(renderer.getClickedCharacter(), hex)) {
            Hex startPos = renderer.getClickedCharacter().getPosition();
            Hex destPos = hex;

            byte[] bytes = ByteBuffer.allocate(2 * (3 * 4))
                .putInt(startPos.q)
                .putInt(startPos.r)
                .putInt(startPos.s)
                .putInt(destPos.q)
                .putInt(destPos.r)
                .putInt(destPos.s)
                .array();

            getConnection().sendEvent(new Event(Event.S_GAME_MOVE, bytes));

            return;
        }

        if (f.hasBuilding() && !f.hasCharacter()) {
            f.getBuilding().onClick();
            return;
        }
        if (!f.hasCharacter()) {
            System.out.println("TODO: field actions");
            return;
        }

        renderer.setClickedCharacter(f.getCharacter());
/*
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
                throw new IllegalStateException(
                        "A player connot be click and unexisting at the same time (unreaachable)!!");
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
                                ((Boat) f.getCharacter()).setCharacter(clickedCharacter);
                                clickedCharacter.setMoved(true);

                                Hex after = f.getHex();
                                Main.client.sendEvent(new Event(Event.S_GAME_MOVE, before.q + "," + before.r + ","
                                        + before.s + ";" + after.q + "," + after.r + "," + after.s));
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
                            /*
                             * if (f.getCharacter() instanceof Boat) { () } break;
                             *\/ }

                        Hex before = clickedCharacter.getPosition();

                        if (boat_leave) {
                            clickedCharacter.moveTo(f);
                            ((Boat) map.getField(before).getCharacter()).removeCharacter();
                            f.setCharacter(clickedCharacter);
                            clickedCharacter.setMoved(true);

                            map.getField(before).getCharacter().setMoved(true);
                            active.removeAll(active);

                            Hex h = f.getHex();
                            Main.client.sendEvent(new Event(Event.S_GAME_LEAVE_BOAT,
                                    before.q + "," + before.r + "," + before.s + ";" + h.q + "," + h.r + "," + h.s));
                            break;
                        }

                        // move to next field
                        clickedCharacter.moveTo(f);
                        active.removeAll(active);
                        map.getField(clickedCharacter.getPosition()).removeCharacter();
                        f.setCharacter(clickedCharacter);
                        clickedCharacter.setMoved(true);

                        if (clickedCharacter instanceof Boat) {
                            if (((Boat) clickedCharacter).hasCharacter()) {
                                GameCharacter char2 = ((Boat) clickedCharacter).getCharacter();

                                char2.moveTo(f);
                                char2.setMoved(true);
                            }
                        }

                        openSurroundedFields(f.getHex());

                        Hex after = f.getHex();
                        Main.client.sendEvent(new Event(Event.S_GAME_MOVE, before.q + "," + before.r + ","
                                + before.s + ";" + after.q + "," + after.r + "," + after.s));

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
        */
    }
}
