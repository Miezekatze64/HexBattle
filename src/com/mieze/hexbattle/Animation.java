package com.mieze.hexbattle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.mieze.hexbattle.hex.Hex;
import com.mieze.hexbattle.hex.Point;

public abstract class Animation {
    private Map map;
    private float rel_pos = 0;
    private Point startPoint;
    private Point endPoint;
    private double length;

    public Animation(final Hex start, final Hex end, final long duration, final Map map) {
        this.map = map;
        startPoint = HexPanel.hexLayout.hexToPixel(start);
        endPoint = HexPanel.hexLayout.hexToPixel(end);
        length = Math.sqrt(Math.pow(endPoint.x - startPoint.x, 2) + Math.pow(endPoint.y - startPoint.y, 2));

        new Timer(20,new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (rel_pos < 1) {
                        rel_pos += 20.0 / duration;
                    } else {
                        animationFinished();
                        ((Timer)e.getSource()).stop();
                        return;
                    }
                }
            }).start();
    }

    public Point getPosition() {
        final double angle = Math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x);
        final double newLength = length * rel_pos;

        final int x = (int) (startPoint.x + Math.cos(angle) * newLength);
        final int y = (int) (startPoint.y + Math.sin(angle) * newLength);

        return map.hexToDisplay(new Point(x, y));
    }

    public abstract void animationFinished();
}
