package com.mieze.hexbattle.client.render;

import static com.mieze.hexbattle.HexPanel.hexLayout;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;

import com.mieze.hexbattle.characters.GameCharacter;
import com.mieze.hexbattle.client.Client;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.UnexploredField;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Point;

public class ClientRenderer {
    private Client client;
    private GameCharacter clickedCharacter = null;

    public ClientRenderer(Client client) {
        this.client = client;
    }

    public void setClickedCharacter(GameCharacter character) {
        this.clickedCharacter = character;
    }

    public GameCharacter getClickedCharacter() {
        return this.clickedCharacter;
    }

    private boolean isOnScreen(Field f) {
        var map = client.getWorldData().getMap();
        return f.isOnScreen(map.getOffsetX(), map.getOffsetY(), map.zoom);
    }

    public void render(Graphics2D g) {
        var map = client.getWorldData().getMap();

        for (int i = 0; i < map.getFields().size(); i++) {
            if (isOnScreen(map.getFields().get(i))) {
                map.getFields().get(i).render(g);
            }
        }

        for (Hex hex : new HashSet<Hex>(map.getUnexplored())) {
            UnexploredField field = new UnexploredField(hex, map);
            field.render(g);
        }

        for (int i = 0; i < map.getFields().size(); i++) {
            if (isOnScreen(map.getFields().get(i))) {
                if (map.getFields().get(i).getCharacter() != null)
                    map.getFields().get(i).getCharacter().render(g, map.zoom);
            }
        }

        if (clickedCharacter != null) {
            for (Hex hex : map.getActive(clickedCharacter)) {
                Point p = map.hexToDisplay(hexLayout.hexToPixel(hex));
                int point_x = (int) p.x;
                int point_y = (int) p.y;

                double w = (hexLayout.size.x * map.zoom) / 2;
                double h = (hexLayout.size.x * map.zoom) / 2;

                int left = (int) (point_x - w / 2);
                int top = (int) (point_y - h / 2);

                g.setColor(Color.ORANGE);
                g.fillOval(left, top, (int) w, (int) h);
                g.setColor(Color.BLACK);
            }
        }

/*
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
            g.fillOval(left, top, (int) w, (int) h);
            g.setColor(Color.BLACK);
        }
*/
        client.getWorldData().getToolbar().render(g, client.getWorldData().getMap());
        client.getWorldData().getInventory().render(g, client.getWorldData().getMap());
    }

    public void mouseMoved(Point point) {
        client.getWorldData().getToolbar().mouseMoved(point, client.getWorldData().getMap());
        client.getWorldData().getInventory().mouseMoved(point, client.getWorldData().getMap());
    }
}
