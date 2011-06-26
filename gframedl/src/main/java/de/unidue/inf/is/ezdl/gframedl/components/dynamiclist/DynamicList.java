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

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.JXList;



/**
 * The DynamicList class is a JXList with a special tool tip.
 * 
 * @author R.Tipografov
 */
public class DynamicList extends JXList {

    private static final long serialVersionUID = -3100000039551581219L;

    private BufferedImage mouseOverItemImage;
    private Point mouseOverItemPos;
    private boolean showToolTip;
    private DynamicListCellRenderer dlcr;


    public DynamicList() {
        this(null);
    }


    /**
     * Constructor.
     */
    public DynamicList(ListCellRenderer lcr) {
        super();
        setToolTipText("Dummytext to ensure the createToolTip method is called.");
        dlcr = new DynamicListCellRenderer(this, lcr);
        super.setCellRenderer(dlcr);
        setDoubleBuffered(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (showToolTip) {
                    if ((e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_UP)) {
                        dlcr.hideToolTip();
                    }
                }
            }
        });

    }


    @Override
    public JToolTip createToolTip() {
        paintImmediately(getVisibleRect());
        DynamicListCellRenderer.setOwnerInstance(this);
        return new DynamicListToolTip(this);
    }


    @Override
    public Point getToolTipLocation(MouseEvent event) {
        paintImmediately(getVisibleRect());
        return mouseOverItemPos;
    }


    /**
     * setMouseOverItemPos.
     */
    public void setMouseOverItemPos(Point p) {
        mouseOverItemPos = p;
    }


    /**
     * getMouseOverItemPos.
     */
    public Point getMouseOverItemPos() {
        return mouseOverItemPos;
    }


    /**
     * setShowToolTip.
     */
    public void setShowToolTip(boolean b) {
        showToolTip = b;
        if (!showToolTip) {
            mouseOverItemImage = null;
        }
    }


    /**
     * getShowToolTip.
     */
    public boolean getShowToolTip() {
        return showToolTip;
    }


    /**
     * setMouseOverItemImage.
     */
    public void setMouseOverItemImage(BufferedImage bi) {
        mouseOverItemImage = bi;
    }


    /**
     * getMouseOverItemImage.
     */
    public BufferedImage getMouseOverItemImage() {
        return mouseOverItemImage;
    }


    @Override
    public void setCellRenderer(ListCellRenderer renderer) {
        dlcr.setCellRenderer(renderer);
    }
}
