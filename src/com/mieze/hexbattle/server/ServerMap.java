package com.mieze.hexbattle.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mieze.hexbattle.HexPanel;
import com.mieze.hexbattle.Map;
import com.mieze.hexbattle.OpenSimplexNoise;
import com.mieze.hexbattle.fields.EmptyField;
import com.mieze.hexbattle.fields.Field;
import com.mieze.hexbattle.fields.ForestField;
import com.mieze.hexbattle.fields.MountainField;
import com.mieze.hexbattle.fields.WaterField;
import com.mieze.hexbattle.fields.building.City;
import com.mieze.hexbattle.fields.building.Village;
import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.OffsetCoord;
import com.mieze.hexbattle.hex.Point;

public class ServerMap implements Map {
    private long seed;
    private ArrayList<Field> fields = new ArrayList<>();
    private OpenSimplexNoise noise;

    private static final double SMOOTH_FACTOR = 0.3;

    public ServerMap(long seed) {
        this.seed = seed;
        this.noise = new OpenSimplexNoise(seed);
    }

    public ServerMap() {
        this(new Random(System.currentTimeMillis()).nextLong());
    }

    public long getSeed() {
        return this.seed;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Field getField(Hex hex, boolean createNew) {
        for (int i = fields.size()-1; i >= 0; i--) {
            if (fields.get(i).getHex().equals(hex)) {
                return fields.get(i);
            }
        }

        if (createNew) {
            addField(hex);
            return getField(hex);
        } else {
            return null;
        }
    }

    public void addField(Hex hex) {
        addField(hex, getType(hex));
    }

    public int getType(Hex hex) {
        OffsetCoord oc = OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, hex);
        double val = noise.eval(oc.row * SMOOTH_FACTOR, oc.col * SMOOTH_FACTOR);
        double type_val = val * 10.0;

        if (type_val < -3) {
            // lake / oceans / rivers / ...
            return Field.WATER;
        } else if (type_val < 4) {
            // grass
            if (type_val < 0 && type_val > -1) {
                return -1;
            }
            return Field.EMPTY;
        } else if (type_val < 5) {
            // forests
            return Field.FOREST;
        } else if (type_val < 9) {
            // mountains
            return Field.MOUNTAIN;
        } else {
            //grass
            return Field.EMPTY;
        }
    }

    protected boolean contains(Hex hex) {
        return fields.stream().anyMatch(a -> a.getHex().equals(hex));
    }

    public void addField(Hex hex, int type) {
        Field add;
        if (!contains(hex)) {
            switch(type) {
            default:
            case Field.EMPTY:
                add = new EmptyField(hex, null);
                break;
            case -1:
                add = new EmptyField(hex, null);
                boolean village = true;
                for (int i = 0; i < 6; i++) {
                    if (getField(hex.neighbor(i), false) != null
                        && getField(hex.neighbor(i)).hasBuilding()
                        && (getField(hex.neighbor(i)).getBuilding() instanceof City ||
                            getField(hex.neighbor(i)).getBuilding() instanceof Village)) {
                        village = false;
                    }
                }
                if (village) {
                    add.setBuilding(new Village(add));
                }
                break;
            case Field.WATER:
                add = new WaterField(hex, null);
                break;
            case Field.MOUNTAIN:
                add = new MountainField(hex, null);
                break;
            case Field.FOREST:
                add = new ForestField(hex, null);
                break;
            }

            this.fields.add(add);
        }
    }

}
