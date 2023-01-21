package com.mieze.hexbattle.client;

import java.util.HashMap;

import com.mieze.hexbattle.Main;
import com.mieze.hexbattle.toolbars.Inventory;
import com.mieze.hexbattle.toolbars.Toolbar;

public class ClientWorldData {
    private ClientMap map;
    private Toolbar toolbar;
    private Inventory inventory;
    private Client client;
    private HashMap<String, ClientPlayer> players = new HashMap<>();

    public ClientMap getMap() {
        return this.map;
    }

    public ClientWorldData(Client client) {
        this.client = client;
        this.toolbar = new Toolbar(client);
        this.inventory = new Inventory();
        this.map = new ClientMap(0, 0);
    }

    public void createMap() {
        this.map = new ClientMap(-Main.WIDTH / 2, -Main.HEIGHT / 2);
    }

    public void createMap(long seed) {
        this.map = new ClientMap(-Main.WIDTH / 2, -Main.HEIGHT / 2, seed);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ClientPlayer getPlayer(String name) {
        return this.players.get(name);
    }

    public void addPlayer(String name, ClientPlayer player) {
        this.players.put(name, player);
    }

    public void resetPlayers() {
        this.players.clear();
    }
}
