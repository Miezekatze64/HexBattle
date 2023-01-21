package com.mieze.hexbattle;

import java.util.HashMap;

import com.mieze.hexbattle.characters.Boat;
import com.mieze.hexbattle.characters.BuilderCharacter;
import com.mieze.hexbattle.characters.CharacterData;
import com.mieze.hexbattle.characters.RiderCharacter;
import com.mieze.hexbattle.characters.SwordsmanCharacter;
import com.mieze.hexbattle.characters.WorkerCharacter;
import com.mieze.hexbattle.fields.EmptyField;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.ForestField;
import com.mieze.hexbattle.fields.MountainField;
import com.mieze.hexbattle.fields.WaterField;
import com.mieze.hexbattle.fields.building.Building;
import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Forest;
import com.mieze.hexbattle.fields.building.Mine;
import com.mieze.hexbattle.fields.building.Port;
import com.mieze.hexbattle.fields.building.Village;

public class Registry {
    public static final RegistryItem<Class<? extends Field>> FIELDS = new RegistryItem<>();
    public static final RegistryItem<Class<? extends CharacterData>> CHARACTERS = new RegistryItem<>();
    public static final RegistryItem<Class<? extends Building>> BUILDINGS = new RegistryItem<>();

    public static void init() {
        FIELDS.register("empty",    EmptyField.class);
        FIELDS.register("water",    WaterField.class);
        FIELDS.register("forest",   ForestField.class);
        FIELDS.register("mountain", MountainField.class);

        CHARACTERS.register("builder",   BuilderCharacter.class);
        CHARACTERS.register("worker",    WorkerCharacter.class);
        CHARACTERS.register("boat",      Boat.class);
        CHARACTERS.register("swordsman", SwordsmanCharacter.class);
        CHARACTERS.register("rider",     RiderCharacter.class);

        BUILDINGS.register("village", Village.class);
        BUILDINGS.register("forest",  Forest.class);
        BUILDINGS.register("port",    Port.class);
        BUILDINGS.register("city",    City.class);
        BUILDINGS.register("mine",    Mine.class);
    }
    
    public static class RegistryItem<T> {
        private HashMap<String, T> map = new HashMap<>();

        public void register(String key, T value) {
            map.put(key, value);
        }

        public T get(String key) {
            return map.get(key);
        }
    }
}
