package com.mieze.hexbattle;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.mieze.hexbattle.client.ClientMap;
import com.mieze.hexbattle.client.render.ClientRenderer;
import com.mieze.hexbattle.hex.Layout;
import com.mieze.hexbattle.hex.Point;
import com.mieze.hexbattle.net.Event;

public class HexPanel extends JPanel {
    private static final long serialVersionUID = 1L;
//    private transient Player player;
//    private ArrayList<Player> opponents;

    private int last_x = 0, last_y = 0;
    private boolean drag = false;
    public static Layout hexLayout;

    private static final float ZOOM_CONSTANT = 1.2f;
    private double fps = Main.FPS;

    private float off_x;
    private float off_y;
//    private int player_index;
//    private int current_index = 0;

//    private static final Color[] COLORS = { Color.RED, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.GRAY };
//    private int colorIndex = 0;
    private boolean first = true;
//    private boolean started = false;
//    private boolean gameStarted = false;

//    private transient Player currentPlayer = null;
    protected ArrayList<String> connected = new ArrayList<>();
    private String name = "";

    static {
        hexLayout = new Layout(Layout.pointy, new Point(40, 40), new Point(0, 0));
    }

/*
    private Color getNextColor() {
        try {
            return COLORS[colorIndex++];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Too many players!");
        }
    }
*/
    public HexPanel() {
        off_x = -Main.WIDTH / 2.0f;
        off_y = -Main.HEIGHT / 2.0f;

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (Main.getClient().getWorldData().getMap() != null)
                    Main.getClient().getWorldData().getMap().addZoom(Math.pow(ZOOM_CONSTANT, -e.getPreciseWheelRotation()));
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    if (Main.getClient().getWorldData().getMap() != null && Main.getClient().getRenderer() != null)
                        Main.getClient().getRenderer().mouseMoved(new Point(e.getX(), e.getY()));
                } catch (Throwable t) {
                    Main.handleException(t);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (drag && Main.getClient().getWorldData().getMap() != null) {

                    off_x = e.getX() - last_x;
                    off_y = e.getY() - last_y;

                    last_x = e.getX();
                    last_y = e.getY();

                    Main.getClient().getWorldData().getMap().addOffset((int) (off_x / Main.getClient().getWorldData().getMap().zoom), (int) (off_y / Main.getClient().getWorldData().getMap().zoom));
                }
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    if (Main.getClient().getWorldData().getMap() != null) {
                        if (e.getButton() == MouseEvent.BUTTON1)
//                            throw new RuntimeException("TODO: here");
                            Main.getClient().onClick(new Point(e.getX(), e.getY()));
                        if (e.getButton() != MouseEvent.BUTTON1) {
                            last_x = e.getX();
                            last_y = e.getY();
                            drag = true;
                        }
                    }
                } catch (Throwable t) {
                    Main.handleException(t);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    public void init(String name) {
        this.name = name;
//        opponents = new ArrayList<Player>();
    }
/*
    public void newPlayer(Hex hex) {
        opponents.add(new Player(null, getNextColor(), false, hex));
    }
*/

    public void currentFPS(double fps) {
        this.fps = fps;
    }

/*    public void nextTurn() {
        nextTurn(false);
    }
*/
    public void disconnected() {
        if (connected == null)
            connected = new ArrayList<>();

        connected.removeAll(connected);
        connected.add(name);
        Main.getClient().getConnection().sendEvent(new Event(Event.S_GET_CONNECTED, ""));
        Main.getInstance().updateConnectedList(connected);
        validate();
    }

    /*public void nextTurn(boolean fromEvent) {
        currentPlayer.state = Player.STATE_OTHER_PLAYER;
        if (!fromEvent)
            Main.getClient().getConnection().sendEvent(new Event(Event.S_END_TURN, ""));

        current_index = (current_index + 1) % (opponents.size() + 1);

        if (current_index == player_index) {
            currentPlayer = player;
        } else if (current_index < player_index) {
            currentPlayer = opponents.get(current_index);
        } else {
            currentPlayer = opponents.get(current_index - 1);
        }

        player.checkKill();
        for (int i = 0; i < opponents.size(); i++) {
            opponents.get(i).checkKill();
        }

        if (currentPlayer == player)
            player.yourTurn(false);
    }*/

/*    public void started() {
        gameStarted = true;
    }
*/
    @Override
    protected void paintComponent(Graphics g) {
        if (Main.getClient().getWorldData().getMap() != null && Main.getClient().getRenderer() != null) {
            try {
                if (first) {
                    ((Graphics2D) getGraphics()).setComposite(AlphaComposite.Clear);
                    first = false;
                }

                Main.getClient().getRenderer().render((Graphics2D) g);
                renderFPS(g);
//                throw new RuntimeException("TODO: here");
            } catch (Exception e) {
                Main.handleException(e);
            }
        }
    }

    public void renderFPS(Graphics g) {
        ((Graphics2D) g).drawString("FPS: " + fps, 10, 20);
    }
}
