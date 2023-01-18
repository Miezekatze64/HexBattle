package com.mieze.hexbattle.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.mieze.hexbattle.Main;

public class Server {
    public static final int PORT = 8462;
    private ServerSocket server;
    private InetAddress ip;
    private ArrayList<Socket> sockets = new ArrayList<>();
    private ArrayList<PrintStream> outputs = new ArrayList<>();
    private PrintStream log;

    public Server() throws IOException {
        //creating log stream and server
        log = new PrintStream(new File("logs/server.log"));
        server = new ServerSocket(PORT);

        try {
            //get IP address to display
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
            log.println("---SocketException---\n");
            e.printStackTrace(log);
            log.println("\n-----------------");
        }

        if (this.ip == null) throw new RuntimeException("No network connection found...");
    }
    public void start() {
        //server thread
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        //get new server connections
                        Socket socket = server.accept();
                        sockets.add(socket);
                        log.println("SERVER: New connection! ("+outputs.size()+")");
                        System.out.println("SERVER: New player joined! (ID "+outputs.size()+")");
                        outputs.add(new PrintStream(socket.getOutputStream()));

                        //client thread
                        new Thread() {
                            final int index = sockets.size()-1;
                            final Socket socket = sockets.get(index);
                            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            public void run() {
                                try {
                                    while (!socket.isClosed()) {
                                        char[] chars = new char[1];
                                        in.read(chars);
                                        char c = chars[0];
                                        for (int i = 0; i < sockets.size(); i++) {
                                            if (i != index) {
                                                //send events to other clients
                                                outputs.get(i).print(c);
                                                outputs.get(i).flush();
                                                log.println("SERVER: Sent char to socket " + i + ": " + c);
                                            }
                                        }

                                        //wait
                                        sleep(10);
                                    }
                                    log.println("SERVER: Socket " + index + " disconnected!");
                                    System.out.println("SERVER: Player disconnected! (ID "+index+")");
                                    Main.getPanel().disconnected();
                                } catch(SocketException e) {
                                    // socket closing (interrupt)
                                } catch (IOException e) {
                                    log.println("---IOException---\n");
                                    e.printStackTrace(log);
                                    log.println("\n-----------------");
                                } catch(InterruptedException e) {
                                    //should not be thrown
                                } finally {
                                    //closing socket and streams
                                    log.println("SERVER: closing streams of socket "+index+".");
                                    try {
                                        // outputs.get(index).close();
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
                    // will be thrown at socket.close() to stop watch processes
                    if (server.isClosed()) {
                        log.println("SERVER: successfully closed");
                    }
                } catch (IOException e) {
                    // error
                    log.println("---IOException---\n");
                    e.printStackTrace(log);
                    log.println("\n-----------------");
                } finally {
                    // clean up sockets, which are still open
                    log.println("SERVER: clenup sockets");
                    for (int i = 0; i < sockets.size(); i++) {
                        try {
                            // close sockets if still open
                            if (!sockets.get(i).isClosed()) sockets.get(i).close();
                        } catch (Exception e) {
                            //error
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
        // close server
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
