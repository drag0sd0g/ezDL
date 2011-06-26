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

package de.unidue.inf.is.ezdl.gframedl.components.dynamiclist;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import org.jdesktop.swingx.renderer.DefaultListRenderer;



/**
 * A ListCellRenderer for the DynamicList.
 * 
 * @author R. Tipografov
 */
public final class DynamicListCellRenderer extends JPanel implements ListCellRenderer, AWTEventListener {

    private static final long serialVersionUID = 1L;

    private static final boolean USE_FANCY_POPUP = true;

    private DynamicList list;
    private int index;
    private ListCellRenderer lcr;

    /**
     * indicates that the mouse is over the JList, not the current cell
     */
    private boolean mouseOver;
    private int mouseOverIndex;

    private static DynamicList ownerInstance = null;


    /**
     * Constructor, expects a JList-owner.
     */
    public DynamicListCellRenderer(DynamicList owner, ListCellRenderer lcr) {
        super();
        setDoubleBuffered(true);
        setLayout(new BorderLayout());
        if (USE_FANCY_POPUP) {
            Toolkit.getDefaultToolkit().addAWTEventListener(
                            this,
                            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
                                            | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
        }
        if (lcr != null) {
            this.lcr = lcr;
        }
        else {
            DefaultListRenderer dlr = new DefaultListRenderer();
            this.lcr = dlr;
        }
        this.list = owner;
    }


    /**
     * Setter for the cell renderer.
     */
    public void setCellRenderer(ListCellRenderer renderer) {
        lcr = renderer;
    }


    /**
     * Forces the list tool tip to close.
     */
    public void hideToolTip() {
        ToolTipManager.sharedInstance().setEnabled(false);
        ToolTipManager.sharedInstance().setEnabled(true);
    }


    /**
     * Forces the list tool tip to show.
     */
    public void showToolTip() {
        try {
            Robot robot = new Robot();
            Point mousePosition = MouseInfo.getPointerInfo().getLocation();
            // Produce a MouseMoveEvent to force the ToolTipManager to show
            // popup
            robot.mouseMove(mousePosition.x + 1, mousePosition.y + 1);
            robot.mouseMove(mousePosition.x, mousePosition.y);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is necessary to avoid conflicts when multiple DynamicList
     * instances exist.
     * 
     * @param dl
     */
    public static void setOwnerInstance(DynamicList dl) {
        ownerInstance = dl;
    }


    /**
     * The MouseListener, causes the ResultListTooltip to close properly.
     */
    @Override
    public void eventDispatched(AWTEvent event) {

        if ((event instanceof MouseEvent) && (list != null)) {
            Point mousePosition = ((MouseEvent) event).getLocationOnScreen();
            SwingUtilities.convertPointFromScreen(mousePosition, list);

            Rectangle rc = list.getVisibleRect();
            if (rc.contains(mousePosition)) {
                ownerInstance = list;
                mouseOver = true;
                mouseOverIndex = list.locationToIndex(mousePosition);
                Rectangle cellRc = list.getCellBounds(mouseOverIndex, mouseOverIndex);

                if ((mouseOverIndex != -1) && cellRc.contains(mousePosition)) {
                    if (!ToolTipManager.sharedInstance().isEnabled()) {
                        ToolTipManager.sharedInstance().setEnabled(true);
                    }
                }
                else {
                    hideToolTip();
                    ToolTipManager.sharedInstance().setEnabled(false);
                }
                if (event instanceof MouseWheelEvent) {
                    hideToolTip();
                }
            }
            else {
                if (ownerInstance == list) {
                    if (list.getShowToolTip()) {
                        list.setShowToolTip(false);
                        hideToolTip();
                    }
                    else {
                        if (!ToolTipManager.sharedInstance().isEnabled()) {
                            ToolTipManager.sharedInstance().setEnabled(true);
                        }
                    }
                    mouseOver = false;
                }
            }
        }
    }


    private static boolean containsRect(Rectangle outerRc, Rectangle innerRc) {
        return (outerRc.x <= innerRc.x) && (outerRc.width >= innerRc.width) && (outerRc.y <= innerRc.y)
                        && (outerRc.height >= innerRc.height);
    }


    private Point correctCellPosToScreen(Rectangle cellRect) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Point cellPos = new Point(cellRect.x, cellRect.y);
        SwingUtilities.convertPointToScreen(cellPos, list);

        if (cellRect.width < d.width) {
            if (cellPos.x < 0) {
                cellPos.x = 0;
            }
            if ((cellPos.x + cellRect.width) > (d.width)) {
                cellPos.x = (d.width) - cellRect.width;
            }
        }

        if (cellRect.height < d.height) {
            if (cellPos.y < 0) {
                cellPos.y = 0;
            }
            if ((cellPos.y + cellRect.height) > (d.height)) {
                cellPos.y = (d.height) - cellRect.height;
            }
        }

        SwingUtilities.convertPointFromScreen(cellPos, list);
        return cellPos;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // If mouse is over this cell, we paint a copy for DynamicListTooltip.
        if ((mouseOver) && (mouseOverIndex == index)) {
            Point cellPos = getLocation();
            Dimension d = getPreferredSize();
            Rectangle visRc = list.getVisibleRect();
            Rectangle cellRc = new Rectangle(cellPos.x, cellPos.y, d.width, d.height);

            // Only show the tool tip if a part of the cell is not visible.
            if (!containsRect(visRc, cellRc)) {
                if (d.width < visRc.width) {
                    d.width = visRc.width;
                }
                // paint the ResultListCell copy.
                BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
                Graphics ggg = bi.createGraphics();
                ggg.setFont(g.getFont());
                super.paint(ggg);

                ggg.setColor(getBackground().darker().darker());
                ggg.draw3DRect(0, 0, d.width - 1, d.height - 1, true);

                list.setMouseOverItemImage(bi);
                list.setMouseOverItemPos(correctCellPosToScreen(cellRc));
                list.setShowToolTip(true);
            }
            else {
                list.setShowToolTip(false);
                ToolTipManager.sharedInstance().setEnabled(false);
            }
        }
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
        Component c = lcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        removeAll();
        add(c, BorderLayout.CENTER);
        setSize(c.getSize());
        setPreferredSize(c.getPreferredSize());
        setMinimumSize(c.getMinimumSize());
        this.index = index;
        return this;
    }
}
