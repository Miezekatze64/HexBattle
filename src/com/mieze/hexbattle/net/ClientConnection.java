package com.mieze.hexbattle.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.mieze.hexbattle.Main;

public class ClientConnection {
    private Socket socket = null;
    private PrintStream out;
    private Map<Byte, EventListener> eventListener = new HashMap<>();
    private Thread socketThread = null;
    private PrintStream log;

    public ClientConnection(String ip, int port) throws IOException, IllegalArgumentException {
        log = new PrintStream(new File("logs/client.log"));
        initServer(InetAddress.getByName(ip), port);
    }

    public ClientConnection(InetAddress ip, int port) throws IOException, IllegalArgumentException {
        log = new PrintStream(new File("logs/client.log"));
        initServer(ip, port);
    }

    private byte[] readMessage(InputStream reader) throws IOException {
        int length = ByteBuffer.wrap(reader.readNBytes(4)).getInt();
        System.out.printf("LENGTH: %d\n", length);
        return reader.readNBytes(length);
    }

    private void initServer(InetAddress ip, int port) throws IOException, IllegalArgumentException {
        socket = new Socket(ip, port);
        OutputStream output = socket.getOutputStream();
        out = new PrintStream(output, true);

        InputStream in = socket.getInputStream();

        socketThread = new Thread() { //client listen thread
            public void run() {
                log.println("CLIENT: listening on" + ip);
                try {
                    while (true) {
                        byte[] eventBytes = readMessage(in);
                        log.println("CLIENT: got bytes: " + Arrays.toString(eventBytes));
                        handleEvent(eventBytes);
                    }
                } catch(SocketException e) {
                    if (socket.isClosed()) {
                        log.println("CLIENT: socket closed");
                        try {
                            in.close();
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        socketThread.start();

    }

    public void setEventListener(byte event, EventListener e) {
        this.eventListener.put(event, e);
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
        final int length = e.getValue().length;

        out.writeBytes(ByteBuffer.allocate(4).putInt(1 + length).array());
        out.write(e.getType());
        out.writeBytes(e.getValue());
        out.flush();

//        out.print(e.getValue()+'\n');
        log.println("CLIENT: Event sent! [Type: "+e.getType() + " | Value: " + Arrays.toString(e.getValue())+"]");
    }

    private void handleEvent(byte[] bytes) {
        if (bytes.length == 0) return;

        byte type = bytes[0];
        byte[] value = ByteBuffer.allocate(bytes.length - 1).put(bytes, 1, bytes.length - 1).array();

        Event event = new Event(type, value);

        System.out.println("[client] Event: " + event);
        log.println("CLIENT: Event received! [Type: " + type + " | Value: " + value +"]");
        if (hasEventListener(type)) getEventListener(type).newEvent(event);
    }

    public boolean hasEventListener(byte event) {
        return eventListener.containsKey(event);
    }

    public EventListener getEventListener(byte type) {
        return eventListener.get(type);
    }

    @FunctionalInterface
    public static interface EventListener {
        public void newEvent(Event e);
    }
}
