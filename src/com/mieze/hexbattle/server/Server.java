package com.mieze.hexbattle.server;

import java.util.Enumeration;
import java.util.ArrayList;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.Inet4Address;

public class Server {
    public static final int PORT = 1234;
    private ServerSocket server;
    private InetAddress ip;
    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<PrintStream> outputs = new ArrayList<>();
    private PrintStream log;

    public Server() {
        try {
            log = new PrintStream(new File("logs/server.log"));
            server = new ServerSocket(PORT);

            try {
                Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
                while (nics.hasMoreElements()) {
                    NetworkInterface nic = nics.nextElement();
                    if (!nic.isLoopback()) {
                        Enumeration<InetAddress> addrs = nic.getInetAddresses();
                        while (addrs.hasMoreElements()) {
                            InetAddress addr = addrs.nextElement();
                            if (addr instanceof Inet4Address) this.ip = addr;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            if (this.ip == null) throw new RuntimeException("No network connection found...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        Socket socket = server.accept();
                        sockets.add(socket);
                        log.println("SERVER: New connection! ("+outputs.size()+")");
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
                                                    log.println("SERVER: Sent line to socket " + i + ": " + line);
                                                }
                                            }
                                        } else  {
                                            break;
                                        }
                                        sleep(10);
                                    }
                                    log.println("SERVER: Socket " + index + " disconnected!");
                                } catch(SocketException e) {
                                    //socket closing (interrupt)
                                } catch (IOException e) {
                                    log.println("---IOException---\n");
                                    e.printStackTrace(log);
                                    log.println("\n-----------------");
                                } catch(InterruptedException e) {

                                } finally {
                                    log.println("SERVER: closing streams of socket "+index+".");
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
                        log.println("SERVER: successfully closed");
                    }
                } catch (IOException e) {
                    log.println("---IOException---\n");
                    e.printStackTrace(log);
                    log.println("\n-----------------");
                } finally {
                    log.println("SERVER: clenup sockets");
                    for (int i = 0; i < sockets.size(); i++) {
                        try {
                            if (!sockets.get(i).isClosed()) sockets.get(i).close();
                        } catch (Exception e) {
                            log.println("---Exception---\n");
                            e.printStackTrace(log);
                            log.println("\n-----------------");
                        }
                    }
                }
            }
        }.start();
    }

    public void close() {
        try {
            log.flush();
            log.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIp() {
        return ip;
    }
}