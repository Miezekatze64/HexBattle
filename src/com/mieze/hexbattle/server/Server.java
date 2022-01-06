package com.mieze.hexbattle.server;

import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
    public static final int PORT = 1234;
    private ServerSocket server;
    private InetAddress ip;
    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<PrintStream> outputs = new ArrayList<>();

    public Server() {
        try {
            server = new ServerSocket(PORT);
            this.ip = server.getInetAddress();
        } catch (IOException e) {
            System.out.println("---IOException---\n");
            e.printStackTrace();
            System.out.println("\n-----------------");
        }
    }

    public void start() {
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        Socket socket = server.accept();
                        sockets.add(socket);
                        System.out.println("SERVER: New connection! ("+outputs.size()+")");
                        outputs.add(new PrintStream(socket.getOutputStream()));

                        new Thread() {
                            final int index = sockets.size()-1;
                            final Socket socket = sockets.get(index);
                            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            public void run() {
                                try {
                                    while (!socket.isClosed()) {
                                        String line = in.readLine();
                                        if (line != null) {
                                            for (int i = 0; i < sockets.size(); i++) {
                                                if (i != index) {
                                                    outputs.get(i).println(line);
                                                    System.out.println("SERVER: Sent line to socket " + i + ": " + line);
                                                }
                                            }
                                        } else  {
                                            break;
                                        }
                                        sleep(10);
                                    }
                                    System.out.println("SERVER: Socket " + index + " disconnected!");
                                } catch(SocketException e) {
                                    //socket closing (interrupt)
                                } catch (IOException e) {
                                    System.out.println("---IOException---\n");
                                    e.printStackTrace();
                                    System.out.println("\n-----------------");
                                } catch(InterruptedException e) {

                                } finally {
                                    System.out.println("SERVER: closing streams of socket "+index+".");
                                    try {
                                        outputs.get(index).close();
                                        in.close();
                                        socket.close();
                                        outputs.remove(index);
                                        sockets.remove(index);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();
                    }   
                } catch(SocketException e) {
                    if (server.isClosed()) {
                        System.out.println("SERVER: successfully closed");
                    }
                } catch (IOException e) {
                    System.out.println("---IOException---\n");
                    e.printStackTrace();
                    System.out.println("\n-----------------");
                } finally {
                    System.out.println("SERVER: clenup sockets");
                    for (int i = 0; i < sockets.size(); i++) {
                        try {
                            if (!sockets.get(i).isClosed()) sockets.get(i).close();
                        } catch (Exception e) {
                            System.out.println("---Exception---\n");
                            e.printStackTrace();
                            System.out.println("\n-----------------");
                        }
                    }
                }
            }
        }.start();
    }

    public void close() {
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIp() {
        return ip;
    }
}