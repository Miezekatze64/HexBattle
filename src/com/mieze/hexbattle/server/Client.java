package com.mieze.hexbattle.server;

import java.net.*;
import java.io.*;

public class Client{
    private Socket socket = null;
    private PrintStream out;
    private EventListener eventListener = null;
    private Thread socketThread = null;
    private PrintStream log;

    public Client(String ip, int port) {
        try {
            log = new PrintStream(new File("logs/client.log"));
            initServer(InetAddress.getByName(ip), port);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Client(InetAddress ip, int port) {
        try {
            log = new PrintStream(new File("logs/client.log"));
            initServer(ip, port);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initServer(InetAddress ip, int port) {
        try {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        
        public static final String EVENT_START = "start";
        public static final String EVENT_START_PLAYER = "start_player";
        public static final String EVENT_START_PLAYER_END = "start_player_end";
        public static final String EVENT_START_SEED = "seed";
        public static final String EVENT_END = "end";

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