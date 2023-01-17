package com.mieze.hexbattle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.mieze.hexbattle.server.Client;
import com.mieze.hexbattle.server.Client.Event;
import com.mieze.hexbattle.server.Server;

/**
 * <strong>Main class for Hexbattle </strong>
 * <p>
 * This class contains the game loop and the {@link Server} and {@link Client}
 * objects.
 * </p>
 * extends {@link javax.swing.JFrame}
 */
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    public static int FPS = 60;

    public static final int WIDTH = 1200; // Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int HEIGHT = 800; // Toolkit.getDefaultToolkit().getScreenSize().height;

    private long lastTime;
    private long frameCount = 0;
    private static HexPanel panel;
    private static Menu menu;

    private static boolean error = false;

    public static boolean isHost = false;
    public static Server server = null;
    public static Client client;

    private static Main instance;

    /**
     * main method of Hexbattle
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            File logs = new File("./logs/");
            if (!logs.exists()) {
                logs.mkdir();
            }

            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.metal.MetalLookAndFeel");
                } catch (Exception er) {}
            }

            instance = new Main();
        } catch (Throwable t) {
            handleException(t);
        }
    }

    /**
     * <p>
     * A function to handle not caught Exceptions.
     * </p>
     * <p>
     * Full stack trace will be writen to log file
     * </p>
     * <p>
     * Error message will be shown in a {@link JOptionPane} message dialog
     *
     * @param t the exception to handle
     */
    public static void handleException(Throwable t) {
        // show message box at error
        if (t != null && !error) {
            error = true;
            javax.swing.JOptionPane.showMessageDialog(null, t.toString() + "\n\nMore infos in ./logs/err.log", "ERROR",
                                                      JOptionPane.ERROR_MESSAGE);

            // writing error to logs/error.log
            try {
                FileWriter fstream = new FileWriter("logs/err.log", true);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(t.toString() + '\n');
                out.close();

                PrintStream stream = new PrintStream(new java.io.File("logs/err.log"));
                t.printStackTrace(stream);
                stream.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occured during the save process of an error message:\n"
                                              + e.getClass() + ":\n" + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }

            System.exit(1);
        }
    }

    /**
     * Connect to a game server
     * 
     * @param ip IP-address to connect to
     * @throws IOException              if an I/O error occures at server connection
     * @throws IllegalArgumentException if IP outside of range
     */
    public void connect(String ip) throws IOException, IllegalArgumentException {
        isHost = false;
        client = new Client(ip, Server.PORT);
    }

    /**
     * Disconnect from current game server
     */
    public void leaveGame() {
        menu.leave();
        isHost = false;
        client.close();
        client = null;
    }

    /**
     * start game server
     * 
     * @throws IOException              if an I/O error occures at server creation
     * @throws IllegalArgumentException if IP outside of range
     */
    public void startServer() throws IOException, IllegalArgumentException {
        isHost = true;
        server = new Server();
        server.start();
        client = new Client(server.getIp(), Server.PORT);
    }

    public void stopServer() {
        panel.connected.removeAll(panel.connected);
        client.sendEvent(new Event(Event.EVENT_SERVER_CLOSE, ""));
        isHost = false;
        server.close();
        server = null;
        client.close();
        client = null;
    }

    public void showGame() {
        panel.started();
        setContentPane(panel);
        validate();
    }

    public void showMenu() {
        setContentPane(menu);
        validate();
    }

    public static Main getInstance() {
        return instance;
    }

    public Main() {
        // creating JFrame
        super("Hexbattle");
        instance = this;
        // initServer();
        
        // add shutdown hook to close client and server streams at exit
        Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (client != null)
                        client.close();
                    if (server != null)
                        server.close();
                }
            });
        
        if (isHost)
            setTitle("Hexbattle [Host]");

        menu = new Menu();

        // creating main panel
        panel = new HexPanel();
        showMenu();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setUndecorated(true);

        pack();
        setSize(WIDTH, HEIGHT);
        setVisible(true);

        // creating game loop
        Timer gameLoop = new Timer(1000 / FPS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                    
                    frameCount++;
                    if (frameCount % 50 == 0)
                        calcFPS(System.currentTimeMillis());
                }
            });

        gameLoop.start();
    }
    
    public static HexPanel getPanel() {
        return panel;
    }

    private void calcFPS(long time) {
        // calculate current FPS
        
        double fps = 1000.0 / (System.currentTimeMillis() - lastTime) * 50;
        lastTime = System.currentTimeMillis();
        
        panel.currentFPS((int) fps);
    }
    
    public void updateConnectedList(ArrayList<String> a) {
        if (menu.isVisible()) {
            menu.updateConnectedList(a);
        }
    }
}
