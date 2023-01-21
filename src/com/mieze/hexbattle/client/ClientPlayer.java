package com.mieze.hexbattle.client;

import java.awt.Color;

import com.mieze.hexbattle.Player;

public class ClientPlayer implements Player {
    private Color color;
    private String name;
    private ClientMap map;

    public ClientPlayer(String player, Color color, ClientMap map) {
        this.color = color;
        this.name  = player;
        this.map   = map;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ClientMap getMap() {
        return this.map;
    }

}
