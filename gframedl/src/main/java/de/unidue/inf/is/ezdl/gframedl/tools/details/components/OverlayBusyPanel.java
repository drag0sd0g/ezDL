/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.gframedl.tools.details.components;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JLabel;
import javax.swing.Timer;

import org.jdesktop.swingx.icon.PainterIcon;
import org.jdesktop.swingx.painter.BusyPainter;



/**
 * OverlayBusyPanel.
 */
public class OverlayBusyPanel extends JLabel {

    private static final long serialVersionUID = -5345582469881033900L;

    private static final float INITIAL_ALPHA = 0.5f;

    private BusyPainter painter;
    private float alpha = INITIAL_ALPHA;
    private Timer busy;
    private Timer fadeOut;
    private int frame = 8;
    private PainterIcon icon;
    private int dx = 0;
    private int dy = 0;


    /**
     * Constructor.
     */
    public OverlayBusyPanel() {
        super();
        setOpaque(false);
        resetPainter();
    }


    /**
     * Sets the size of the BusyPainter. This method should be called, if the
     * Panel was resized.
     */
    public void resetPainter() {
        if (isBusy()) {
            float minDim = Math.min(this.getWidth(), this.getHeight());
            float scale = minDim / 70.0f;
            painter = new BusyPainter(new Ellipse2D.Float(0, 0, 5.5f * scale, 6.0f * scale), new Ellipse2D.Float(
                            7.5f * scale, 7.5f * scale, 35.0f * scale, 35.0f * scale));

            dx = Math.round(getWidth() / 2f - (50.0f * scale / 2));
            dy = Math.round(getHeight() / 2f - (50.0f * scale / 2));

            painter.setTrailLength(4);
            painter.setPoints(8);
            painter.setFrame(-1);

            Dimension dim = new Dimension(getSize());
            icon = new PainterIcon(dim);
            icon.setPainter(painter);
            setIcon(icon);
        }
    }


    /**
     * Getter for the busy-state.
     */
    public boolean isBusy() {
        return busy != null;
    }


    /**
     * Setter for the busy-state.
     */
    public void setBusy(boolean busy) {
        boolean old = isBusy();
        if (!old && busy) {
            startAnimation();
            firePropertyChange("busy", old, isBusy());
        }
        else if (old && !busy) {
            stopAnimation(true);
            firePropertyChange("busy", old, isBusy());
        }
    }


    @Override
    public void paint(Graphics g) {
        if (isBusy()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(dx, dy);
            Composite oldComp = g2.getComposite();
            Composite alphaComp;
            alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(alphaComp);
            super.paint(g);
            g2.setComposite(oldComp);
        }
    }


    private void startAnimation() {
        if (busy != null) {
            stopAnimation(false);
        }
        alpha = INITIAL_ALPHA;
        busy = new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame = (frame + 1) % 8;
                painter.setFrame(frame);
                repaint();
            }
        });
        busy.start();
    }


    private void stopFadeOut() {
        if (fadeOut != null) {
            fadeOut.stop();
            fadeOut = null;
        }
    }


    private void stopAnimation(boolean doFadeOut) {
        stopFadeOut();
        if (doFadeOut) {
            fadeOut = new Timer(150, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    alpha -= 0.15f;
                    if (alpha <= 0) {
                        busy.stop();
                        painter.setFrame(-1);
                        repaint();
                        busy = null;
                        stopFadeOut();
                    }
                }
            });
            fadeOut.start();
        }
        else {
            busy.stop();
            painter.setFrame(-1);
            repaint();
            busy = null;
        }
    }
}
