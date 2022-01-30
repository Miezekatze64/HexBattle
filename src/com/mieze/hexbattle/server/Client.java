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
                        String line = bufferedReader.readLine();
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
        out.println(e.getType() + "||" + e.getValue());
        log.println("CLIENT: Event sent! [Type: "+e.getType() + " | Value: " + e.getValue()+"]");
    }

    private void handleEvent(String s) {
        if (s != null) {
            String type, value = "";

            String[] strings = s.split("\\|\\|");
            type = strings[0];
            if (strings.length > 1) value = strings[1];
            
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

    public abstract static class EventListener {
        public abstract void newEvent(Event e);
    }

    public static class Event {
        public static final String EVENT_JOIN = "join";
        public static final String EVENT_ADD_PLAYER = "add_player";

        public static final String EVENT_CONNECTED_CHANGED  = "connected_changed";
        
        public static final String EVENT_START = "start_start";
        public static final String EVENT_START_PLAYER = "start_player";
        public static final String EVENT_START_PLAYER_END = "start_player_end";
        public static final String EVENT_START_SEED = "start_seed";
        
        public static final String EVENT_GAME_START = "game_start";
        public static final String EVENT_GAME_MOVE = "game_move";
        public static final String EVENT_GAME_ATTACK = "game_attack";
        public static final String EVENT_GAME_NEW_CHARACTER = "game_new_character";
        public static final String EVENT_GAME_NEW_PORT = "game_new_port";
        public static final String EVENT_GAME_CONQUER_CITY = "game_conquer_city";
        public static final String EVENT_GAME_BUILD_MINE = "game_build_mine";
        public static final String EVENT_GAME_CHOP_WOOD = "game_cop_wood";
        public static final String EVENT_GAME_LEAVE_BOAT = "game_leave_boat";

        public static final String EVENT_END_TURN = "end_turn";
        public static final String EVENT_GET_CONNECTED = "connected";
        public static final String EVENT_SEND_NAME = "send_name";
        public static final String EVENT_SERVER_CLOSE = "server_close";
        public static final String EVENT_ALREADY_STARTED = "already_started";

        private String type;
        private String value;

        public Event(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
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