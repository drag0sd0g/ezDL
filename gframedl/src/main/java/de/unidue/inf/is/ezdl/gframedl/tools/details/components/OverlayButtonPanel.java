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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;



/**
 * The OverlayButtonPanel which is used in DetailViewContainer.
 */
public class OverlayButtonPanel extends JPanel implements AWTEventListener {

    private static final long serialVersionUID = -6665593764645029654L;


    private enum Orientation {
        VERTICAL, HORIZONTAL
    }


    private enum HorizontalPosition {
        LEFT, CENTER, RIGHT
    }


    private enum VerticalPosition {
        TOP, CENTER, BOTTOM
    }


    /**
     * Layout properties.
     */
    private Orientation orientation = Orientation.HORIZONTAL;
    private HorizontalPosition horizontalPosition = HorizontalPosition.RIGHT;
    private VerticalPosition verticalPosition = VerticalPosition.TOP;
    private boolean hidePanelOnMouseExited = true;

    private final int OUTER_OFFSET = 20;
    private final int INNER_OFFSET = 10;


    /**
     * Constructor.
     */
    public OverlayButtonPanel() {
        super();
        setOpaque(false);
        setLayout(null);
        setVisible(true);
        if (hidePanelOnMouseExited) {
            setVisible(false);
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
        }
    }


    /**
     * Sets the bounds, the layout is calculated.
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        calcLayout();
    }


    /**
     * Adds a new button.
     */
    public void addOverlayButton(OverlayButton ob) {
        add(ob);
        calcLayout();
    }


    /**
     * The MouseListener.
     */
    @Override
    public void eventDispatched(AWTEvent event) {
        if (event instanceof MouseEvent) {
            boolean blockedByModalWindow = true;
            if (event.getSource() != null) {
                if (event.getSource() instanceof JComponent) {
                    JComponent evSource = (JComponent) event.getSource();
                    Container c1 = evSource.getTopLevelAncestor();
                    Container c2 = getTopLevelAncestor();

                    if ((c1 != null) && (c2 != null) && (c1 == c2)) {
                        blockedByModalWindow = false;
                    }
                }
            }
            MouseEvent me = (MouseEvent) event;
            Point mousePosition = me.getLocationOnScreen();
            SwingUtilities.convertPointFromScreen(mousePosition, this);
            Rectangle rc = new Rectangle(0, 0, getWidth(), getHeight());
            if (rc.contains(mousePosition) && (!blockedByModalWindow)) {
                this.setVisible(true);
            }
            else {
                this.setVisible(false);
            }
        }
    }


    /**
     * Calculates the layout, according to the properties above.
     */
    private void calcLayout() {
        if (getComponentCount() > 0) {
            int width;
            int height;
            Point origin = new Point(0, 0);

            if (orientation == Orientation.HORIZONTAL) {
                width = ((getComponentCount() - 1) * INNER_OFFSET)
                                + (getComponentCount() * getComponent(0).getPreferredSize().width);
                height = OUTER_OFFSET + getComponent(0).getPreferredSize().height;
            }
            else {
                width = OUTER_OFFSET + getComponent(0).getPreferredSize().width;
                height = ((getComponentCount() - 1) * INNER_OFFSET)
                                + (getComponentCount() * getComponent(0).getPreferredSize().height);
            }

            switch (horizontalPosition) {
                case LEFT: {
                    origin.x = OUTER_OFFSET;
                    break;
                }
                case CENTER: {
                    origin.x = (getWidth() / 2) - (width / 2);
                    break;
                }
                case RIGHT: {
                    origin.x = getWidth() - width - OUTER_OFFSET - 5;
                    break;
                }
            }

            switch (verticalPosition) {
                case TOP: {
                    origin.y = INNER_OFFSET; // OUTER_OFFSET;
                    break;
                }

                case CENTER: {
                    origin.y = (getHeight() / 2) - (height / 2);
                    break;
                }

                case BOTTOM: {
                    origin.y = getHeight() - height - OUTER_OFFSET;
                    break;
                }
            }

            if (orientation == Orientation.VERTICAL) {
                for (Component c : getComponents()) {
                    c.setBounds(origin.x, origin.y, c.getPreferredSize().width, c.getPreferredSize().height);
                    origin.y += INNER_OFFSET;
                    origin.y += c.getPreferredSize().height;
                }
            }
            else {
                for (Component c : getComponents()) {
                    c.setBounds(origin.x, origin.y, c.getPreferredSize().width, c.getPreferredSize().height);
                    origin.x += INNER_OFFSET;
                    origin.x += c.getPreferredSize().width;
                }
            }
        }
    }
}
