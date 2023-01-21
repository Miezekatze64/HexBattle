package com.mieze.hexbattle.characters;

import java.awt.Image;

import com.mieze.hexbattle.Player;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.server.ServerPlayer;

public class Action {
    private String id;
    private String displayName;
    private VariadicString description;

    private VariadicImage image;

    private ActionPredicate pred;
    private ActionAction action;

    public Action(String id, String name, VariadicString desc,
                  VariadicImage image,
                  ActionPredicate pred, ActionAction action) {
        this.action = action;
        this.displayName = name;
        this.description = desc;
        this.pred = pred;
        this.id = id;
        this.image = image;
    }

    public String getID() {
        return this.id;
    }

    public Image getImage(Field field, Player player) {
        return this.image.get(field, player);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription(Field field, Player player) {
        return this.description.get(field, player);
    }

    public boolean isTrue(Field field, Player player) {
        return this.pred.isTrue(field, player);
    }

    public void performAction(Field field, ServerPlayer player) {
        this.action.performAction(field, player);
    }

    @FunctionalInterface
    public interface ActionPredicate {
        public boolean isTrue(Field field, Player player);
    }

    @FunctionalInterface
    public interface ActionAction {
        public void performAction(Field field, ServerPlayer player);
    }

    @FunctionalInterface
    public interface VariadicString {
        public String get(Field field, Player player);
    }

    @FunctionalInterface
    public interface VariadicImage {
        public Image get(Field field, Player player);
    }
}
