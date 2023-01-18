package com.mieze.hexbattle.server;

import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketException;

import java.io.File;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class Client {
    private Socket socket = null;
    private PrintStream out;
    private EventListener eventListener = null;
    private Thread socketThread = null;
    private PrintStream log;

    public Client(String ip, int port) throws IOException, IllegalArgumentException {
        log = new PrintStream(new File("logs/client.log"));
        initServer(InetAddress.getByName(ip), port);
    }

    public Client(InetAddress ip, int port) throws IOException, IllegalArgumentException {
        log = new PrintStream(new File("logs/client.log"));
        initServer(ip, port);
    }

    private String readMessage(BufferedReader reader) throws IOException {
        String s = "";
        for (;;) {
            char c = (char)reader.read();
            if (c == '\0') break;
            s += c;
        }
        return s;
    }

    private void initServer(InetAddress ip, int port) throws IOException, IllegalArgumentException {
        socket = new Socket(ip, port);
        OutputStream output = socket.getOutputStream();
        out = new PrintStream(output, true);

        InputStream in = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        socketThread = new Thread() { //client listen thread
            public void run() {
                log.println("CLIENT: listening on" + ip);
                try {
                    while (true) {
                        String line = readMessage(bufferedReader);
                        if (line != null) {
                            log.println("CLIENT: got line: " + line);
                            handleEvent(line);
                        }
                        sleep(10);
                    }
                } catch(SocketException e) {
                    if (socket.isClosed()) {
                        log.println("CLIENT: socket closed");
                        try {
                            bufferedReader.close();
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {

                }
            }
        };
        socketThread.start();

    }

    public void setEventListener(EventListener e) {
        this.eventListener = e;
    }

    public void close() {
        try {
            log.println("CLIENT: closing socket");
            log.flush();
            log.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(Event e) {
        out.print((char)e.getType() + e.getValue() + '\0');
        out.flush();
//        out.print(e.getValue()+'\n');
        log.println("CLIENT: Event sent! [Type: "+e.getType() + " | Value: " + e.getValue()+"]");
    }

    private void handleEvent(String s) {
        if (s != null) {
            byte type;
            String value = "";
            if (s.length() == 0) throw new RuntimeException("got empty event");
            type = (byte)s.charAt(0);
            if (s.length() > 2) value = s.substring(1);
            
            log.println("CLIENT: Event received! [Type: " + type + " | Value: " + value +"]");
            if (hasEventListener()) getEventListener().newEvent(new Event(type, value));
        }
    }

    public boolean hasEventListener() {
        return eventListener != null;
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    @FunctionalInterface
    public static interface EventListener {
        public void newEvent(Event e);
    }

    public static class Event {
        public static final byte EVENT_JOIN = 0x01;
        public static final byte EVENT_ADD_PLAYER = 0x02;

        public static final byte EVENT_CONNECTED_CHANGED  = 0x03;
        
        public static final byte EVENT_START = 0x04;
        public static final byte EVENT_START_PLAYER = 0x05;
        public static final byte EVENT_START_PLAYER_END = 0x06;
        public static final byte EVENT_START_SEED = 0x07;
        
        public static final byte EVENT_GAME_START = 0x08;
        public static final byte EVENT_GAME_MOVE = 0x09;
        public static final byte EVENT_GAME_ATTACK = 0x0A;
        public static final byte EVENT_GAME_NEW_CHARACTER = 0x0B;
        public static final byte EVENT_GAME_NEW_PORT =0x0C;
        public static final byte EVENT_GAME_CONQUER_CITY = 0x0D;
        public static final byte EVENT_GAME_BUILD_MINE = 0x0E;
        public static final byte EVENT_GAME_CHOP_WOOD = 0x0F;
        public static final byte EVENT_GAME_LEAVE_BOAT = 0x10;

        public static final byte EVENT_END_TURN = 0x11;
        public static final byte EVENT_GET_CONNECTED = 0x12;
        public static final byte EVENT_SEND_NAME = 0x13;
        public static final byte EVENT_SERVER_CLOSE = 0x14;
        public static final byte EVENT_ALREADY_STARTED = 0x15;

        private byte type;
        private String value;

        public Event(byte type, String value) {
            this.type = type;
            this.value = value;
        }

        public byte getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Event clone() {
            return new Event(type, value);
        }

        @Override
        public String toString() {
            return "Event {Type: '" + type + "', Value: '" + value + "'}";
        }
    }
}
