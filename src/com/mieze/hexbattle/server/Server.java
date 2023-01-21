package com.mieze.hexbattle.server;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.net.Event;
import com.mieze.hexbattle.net.ServerConnection;

public class Server {
    private ServerConnection connection = null;
    private ServerWorldData worldData = new ServerWorldData();

    public Server() {
    }

    private void forEachSeeingPlayer(Hex h1, Hex h2, PlayerAction pa) {
        for (ServerPlayer p : worldData.getPlayers()) {
            if (p.canSee(h1) && p.canSee(h2)) pa.perform(p);
        }
    }

    private void forEachHalfSeeingPlayer(Hex h1, Hex h2, PlayerAction pa) {
        for (ServerPlayer p : worldData.getPlayers()) {
            if (!p.canSee(h1) && p.canSee(h2)) pa.perform(p);
        }
    }

    @FunctionalInterface
    static interface PlayerAction {
        public void perform(Player player);
    }

    public void connect() {
        try {
            this.connection = new ServerConnection();
            this.connection.setEventListener(Event.S_JOIN, (e, idx) -> {
                System.out.println("[server] Got player: " + new String(e.getValue()));
                var player = new ServerPlayer(new String(e.getValue()), worldData.getMap(), Color.RED, idx);
                worldData.addPlayer(player);

                // send updated player list to all clients (join/leave event could lead to weird side-effect at packet loss)
                ArrayList<Byte> bytes = new ArrayList<>();
                for (Player p : worldData.getPlayers()) {
                    String s = p.getName();
                    bytes.add((byte)s.length());

                    for (byte b : s.getBytes()) {
                        bytes.add(b);
                    }
                }

                bytes.add((byte)0);

                this.connection.broadcastEvent(new Event(Event.C_NAMES, bytes));
                System.out.println("[server] broadcasting player list");
            });

            this.connection.setEventListener(Event.S_GAME_START, (e, idx) -> {
                System.out.println("[server] starting game... ");
                this.connection.broadcastEvent(new Event(Event.C_GAME_START, ""));

                for (Player p : worldData.getPlayers()) {
                    var coords = new ArrayList<Hex>();
                    coords.add(((ServerPlayer)p).getStartPos());

                    for (int i = 0; i < 6; i++) {
                        coords.add(((ServerPlayer)p).getStartPos().neighbor(i));
                    }

                    for (var coord : coords) {
                        exploreField(p, coord);

                        /*
                        byte[] sbytes = ByteBuffer
                            .allocate(4 * 4 + p.getName().getBytes().length)
                            .putInt(coord.q)
                            .putInt(coord.r)
                            .putInt(coord.s)
                            .putInt(p.getName().getBytes().length)
                            .put(p.getName().getBytes())
                            .array();

                        // occupy field
                        this.connection.sendEvent(p, new Event(Event.C_OCCUPY_FIELD, sbytes));
                        */
                    }

                    Hex coord = ((ServerPlayer)p).getStartPos();
/*                    String building = "city";

                    byte[] bbytes = ByteBuffer
                        .allocate(3 * 4 + 4 + building.getBytes().length + 4 + p.getName().getBytes().length)
                        .putInt(coord.q)
                        .putInt(coord.r)
                        .putInt(coord.s)
                        .putInt(building.getBytes().length)
                        .put(building.getBytes())
                        .putInt(p.getName().getBytes().length)
                        .put(p.getName().getBytes())
                        .array();

                    this.connection.sendEvent(p, new Event(Event.C_SPAWN_BUILDING, bbytes));
*/
                    String name = "builder";
                    byte[] bytes = ByteBuffer
                        .allocate(3 * 4 + 4 + name.getBytes().length + 4 + p.getName().getBytes().length)
                        .putInt(coord.q)
                        .putInt(coord.r)
                        .putInt(coord.s)
                        .putInt(name.getBytes().length)
                        .put(name.getBytes())
                        .putInt(p.getName().getBytes().length)
                        .put(p.getName().getBytes())
                        .array();

                    this.connection.sendEvent(p, new Event(Event.C_SPAWN_CHARACTER, bytes));
                }
            });

            this.connection.setEventListener(Event.S_GAME_MOVE, (e, idx) -> {
                var buf = ByteBuffer.wrap(e.getValue());
                Hex start_pos = new Hex(buf.getInt(), buf.getInt(), buf.getInt());
                Hex end_pos = new Hex(buf.getInt(), buf.getInt(), buf.getInt());
                System.out.printf("Move from %s to %s\n", start_pos, end_pos);

                Field start_field = worldData.getMap().getField(start_pos);
                Field end_field = worldData.getMap().getField(end_pos);

                if (! start_field.hasCharacter()) {
                    System.out.printf("[server, ERR] tried to move character from empty field. (%s)\n", start_pos);
                    return;
                }

                if (end_field.hasCharacter()) {
                    System.out.printf("[server, ERR] tried to move character to taken field. (%s)\n", end_pos);
                    return;
                }

                GameCharacter character = start_field.getCharacter();
                if (! worldData.getMap().canMoveTo(character, end_pos)) {
                    System.out.printf("[server, ERR] tried to move character to unreachable field. (%s) -> (%s)\n",
                                      start_pos,
                                      end_pos);
                }

                character.setField(end_field);

                start_field.removeCharacter();
                end_field.setCharacter(character);

                forEachSeeingPlayer(start_pos, end_pos, p -> {
                    getConnection().sendEvent(p, new Event(Event.C_GAME_MOVE, e.getValue()));
                });

                forEachHalfSeeingPlayer(start_pos, end_pos, p -> {
                    String name = character.getData().getID();
                    byte[] bytes = ByteBuffer
                        .allocate(3 * 4 + 4 + name.getBytes().length + 4 + p.getName().getBytes().length)
                        .putInt(end_pos.q)
                        .putInt(end_pos.r)
                        .putInt(end_pos.s)
                        .putInt(name.getBytes().length)
                        .put(name.getBytes())
                        .putInt(character.getPlayer().getName().getBytes().length)
                        .put(character.getPlayer().getName().getBytes())
                        .array();

                    getConnection().sendEvent(p, new Event(Event.C_SPAWN_CHARACTER, bytes));
                });

                exploreSurroundings(character.getPlayer(), end_pos);
            });

        } catch (IOException e) {
            Main.handleException(e);
        }
    }

    private void exploreSurroundings(Player player, Hex hex) {
        for (int i = 0; i < 6; i++) {
            Hex h = hex.neighbor(i);
            if (!((ServerPlayer)player).canSee(h))
                exploreField(player, h);
        }
    }

    private void exploreField(Player player, Hex hex) {
        Field field = this.worldData.getMap().getField(hex);
        String name = field.getID();

        byte[] fbytes = ByteBuffer
            .allocate(4 * 4 + name.getBytes().length)
            .putInt(hex.q)
            .putInt(hex.r)
            .putInt(hex.s)
            .putInt(name.getBytes().length)
            .put(name.getBytes())
            .array();

        // explore new field
        this.connection.sendEvent(player, new Event(Event.C_EXPLORED_FIELD, fbytes));

        // update player data
        ((ServerPlayer)player).exploreField(hex);

        if (field.hasBuilding()) {
            String building = field.getBuilding().getID();

            byte[] bbytes = ByteBuffer
                .allocate(3 * 4 + 4 + building.getBytes().length)
                .putInt(hex.q)
                .putInt(hex.r)
                .putInt(hex.s)
                .putInt(building.getBytes().length)
                .put(building.getBytes())
                .array();

            this.connection.sendEvent(player, new Event(Event.C_SPAWN_BUILDING, bbytes));
        }

        if (field.hasCharacter()) {
            String cname = field.getCharacter().getData().getID();

            byte[] bytes = ByteBuffer
                .allocate(3 * 4 + 4 + cname.getBytes().length + 4 + player.getName().getBytes().length)
                .putInt(hex.q)
                .putInt(hex.r)
                .putInt(hex.s)
                .putInt(cname.getBytes().length)
                .put(cname.getBytes())
                .putInt(player.getName().getBytes().length)
                .put(player.getName().getBytes())
                .array();

            getConnection().sendEvent(player, new Event(Event.C_SPAWN_CHARACTER, bytes));
        }

        if (field.hasOwner()) {
            byte[] sbytes = ByteBuffer
                .allocate(4 * 4 + player.getName().getBytes().length)
                .putInt(hex.q)
                .putInt(hex.r)
                .putInt(hex.s)
                .putInt(player.getName().getBytes().length)
                .put(player.getName().getBytes())
                .array();

            // occupy field
            this.connection.sendEvent(player, new Event(Event.C_OCCUPY_FIELD, sbytes));
        }
    }

    public ServerConnection getConnection() {
        return connection;
    }

    public ServerWorldData getWorldData() {
        return this.worldData;
    }
}
