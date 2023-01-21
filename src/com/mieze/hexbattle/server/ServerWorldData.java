package com.mieze.hexbattle.server;

import java.util.ArrayList;
import java.util.List;

public class ServerWorldData {
    private ServerMap map;
    private List<ServerPlayer> players = new ArrayList<>();

    public long getSeed() {
        return map.getSeed();
    }

    public ServerMap getMap() {
        return map;
    }

    public List<ServerPlayer> getPlayers() {
        return this.players;
    }

    public void addPlayer(ServerPlayer p) {
        this.players.add(p);
    }

    public ServerWorldData() {
        System.out.println("[server] creating world");
        this.map = new ServerMap();
    }
}
