package com.mieze.hexbattle.net;

import java.util.Arrays;
import java.util.List;

public class Event {
    public static final byte S_JOIN = 0x01;
    public static final byte S_ADD_PLAYER = 0x02;
    public static final byte S_CONNECTED_CHANGED  = 0x03;

    public static final byte S_GAME_START = 0x08;
    public static final byte S_GAME_MOVE = 0x09;
    public static final byte S_GAME_ATTACK = 0x0A;
    public static final byte S_GAME_NEW_CHARACTER = 0x0B;
    public static final byte S_GAME_NEW_PORT =0x0C;
    public static final byte S_GAME_CONQUER_CITY = 0x0D;
    public static final byte S_GAME_BUILD_MINE = 0x0E;
    public static final byte S_GAME_CHOP_WOOD = 0x0F;
    public static final byte S_GAME_LEAVE_BOAT = 0x10;

    public static final byte S_END_TURN = 0x11;
    public static final byte S_GET_CONNECTED = 0x12;
    public static final byte C_NAMES = 0x13;
    public static final byte C_CLOSE = 0x14;
    public static final byte C_ALREADY_STARTED = 0x15;
    public static final byte C_GAME_START = 0x16;
    public static final byte C_EXPLORED_FIELD = 0x17;
    public static final byte C_SPAWN_CHARACTER = 0x18;
    public static final byte C_SPAWN_BUILDING = 0x19;
    public static final byte C_OCCUPY_FIELD = 0x20;
    public static final byte C_GAME_MOVE = 0x21;

    private byte type;
    private byte[] value;

    public Event(byte type, String value) {
        this.type = type;
        this.value = value.getBytes();
    }

    public Event(byte type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    public Event(byte type, List<Byte> value) {
        this.type = type;
        this.value = new byte[value.size()];

        for (int i = 0; i < value.size(); i++) {
            this.value[i] = value.get(i);
        }
    }

    public byte getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    public Event clone() {
        return new Event(type, value);
    }

    @Override
    public String toString() {
        return "Event {Type: '" + type + "', Value: '" + Arrays.toString(value) + "'}";
    }
}
