package com.mieze.hexbattle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mieze.hexbattle.net.Event;

public class Menu extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int STATE_MAIN_MENU = 0;
    private static final int STATE_CREATE_GAME = 1;
    private static final int STATE_JOIN_GAME = 2;

    private int state = STATE_MAIN_MENU;

    private JPanel players;

    public Menu() {
        showMainMenu();
    }

    private void showCreateGameMenu() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel label = new JLabel("\nCreate new game");
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 36));

        add(label, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new GridBagLayout());
        JPanel ip = new JPanel();
        ip.setLayout(new BoxLayout(ip, BoxLayout.Y_AXIS));
        ip.add(getLabel("Your IP is: " + Main.server.getConnection().getIp().getHostAddress()));
        buttons.add(ip, gbc);

        players = new JPanel();
        players.setLayout(new BoxLayout(players, BoxLayout.Y_AXIS));
        players.add(getLabel("Players connected: "));

        buttons.add(players, gbc);

        buttons.add(getButton("Start", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getClient().getConnection().sendEvent(new Event(Event.S_GAME_START, ""));
                //updateState(STATE_MAIN_MENU);
            }
        }), gbc);

        buttons.add(getButton("Close", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().stopServer();
                updateState(STATE_MAIN_MENU);
            }
        }), gbc);

        gbc.weighty = 1;
        add(buttons, gbc);
    }

    private void showJoinGameMenu() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel label = new JLabel("\nWaiting for host to start game...");
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 36));

        add(label, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new GridBagLayout());

        players = new JPanel();
        players.setLayout(new BoxLayout(players, BoxLayout.Y_AXIS));
        players.add(getLabel("Players connected: "));
        buttons.add(players, gbc);

        buttons.add(getButton("Close", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.getInstance().leaveGame();
                updateState(STATE_MAIN_MENU);
            }
        }), gbc);

        gbc.weighty = 1;
        add(buttons, gbc);
    }

    private void showMainMenu() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel label = new JLabel("\nHexbattle");
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 36));

        add(label, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new GridBagLayout());

        buttons.add(getButton("Create new game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Main.getInstance().startServer();
                    updateState(STATE_CREATE_GAME);

                    String name = JOptionPane.showInputDialog(null, "Enter name:");
                    while (name == null || name.length() == 0 || name.contains(",")) {
                        name = JOptionPane.showInputDialog(null, "Invalid name!\nEnter name:");
                    }
                    Main.getClient().getConnection().sendEvent(new Event(Event.S_JOIN, name));
                    Main.getPanel().init(name);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error during server startup", JOptionPane.ERROR_MESSAGE);
                }

            }
        }), gbc);

        buttons.add(getButton("Join Game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = JOptionPane.showInputDialog(null, "Enter IP-address");
                try {
                    Main.getInstance().connect(ip);
                    String name = JOptionPane.showInputDialog(null, "Enter name:");
                    Main.getPanel().init(name);
                    updateState(STATE_JOIN_GAME);
                    Main.getClient().getConnection().sendEvent(new Event(Event.S_JOIN, name));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error during connection", JOptionPane.ERROR_MESSAGE);
                }

            }
        }), gbc);

        buttons.add(getButton("Exit", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }), gbc);
        
        gbc.weighty = 1;
        add(buttons, gbc);
    }
    
    private void updateState(int state) {
        this.state = state;
        removeAll();
        switch(state) {
            case STATE_MAIN_MENU -> showMainMenu();
            case STATE_CREATE_GAME -> showCreateGameMenu();
            case STATE_JOIN_GAME -> showJoinGameMenu();
        };

        validate();
    }

    public void updateConnectedList(List<String> list) {
        if (( state == STATE_CREATE_GAME || state == STATE_JOIN_GAME ) && players != null) {
            players.removeAll();

            players.add(getLabel("Players connected: "));

            for (int i = 0; i < list.size(); i++) {
                JLabel label = getLabel(list.get(i));
                label.setBorder(new EmptyBorder(4, 0, 4, 0));
                players.add(label);
            }
            validate();
            repaint();
        }
    }

    private JLabel getLabel(String text) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(300, 50));
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        return label;
    }

    private JButton getButton(String text, ActionListener listner) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 50));
        
        btn.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        btn.setFocusPainted(false);
//        btn.setContentAreaFilled(false);

        btn.addActionListener(listner);

        return btn;
    }

    public void leave() {
        if (state != STATE_MAIN_MENU) {
            updateState(STATE_MAIN_MENU);
        }
    }
}
