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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

import de.unidue.inf.is.ezdl.gframedl.Icons;



/**
 * This buttons are used in the title bar of the Group/Groups.
 * 
 * @author R.Tipografov
 */
public class TitleBtn extends JButton implements ActionListener {

    private static final long serialVersionUID = -1275757546636810788L;


    /**
     * The Styles which could be used for Display the button. The TITLEBTN-style
     * is the Default.
     */
    public enum Style {
        SOLID, BBORDER, TRANSPARENT, TITLEBTN
    }


    protected static final int BTN_SIZE = 18;
    protected static final boolean TOOLTIPS_ENABLED = true;

    protected Style style;
    protected boolean mouseOver;


    /**
     * Constructor, an Action with Name & Icon is expected to work properly. The
     * buttons size depends on actions icon size, normally 16x16 is used.
     */
    public TitleBtn(Action a) {
        super(a);
        setOpaque(false);

        setStyle(Style.TITLEBTN);

        Icon icon = getIcon();
        if (icon == null) {
            setIcon(Icons.DEFAULT.get22x22());
        }

        int size = BTN_SIZE;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));

        if (TOOLTIPS_ENABLED && a != null) {
            String s = (String) a.getValue(Action.NAME);
            if (s != null) {
                setToolTipText(s);
            }
        }
        setUI(new BasicButtonUI());
        setFocusable(false);

        addMouseListener(buttonMouseListener);
        setRolloverEnabled(true);
    }


    /**
     * The Style property.
     */
    public void setStyle(Style s) {
        style = s;
        switch (style) {
            case SOLID: {
                setOpaque(true);
            }
                break;

            case BBORDER: {
                setOpaque(false);
            }
                break;

            case TRANSPARENT: {
                setOpaque(false);
            }
                break;
            case TITLEBTN: {
                setOpaque(false);
            }
                break;

        }
    }


    /**
     * The Style property.
     */
    public Style getStyle() {
        return style;
    }


    @Override
    public void updateUI() {
    }


    /**
     * Button was clicked.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Action a = this.getAction();
        if (a != null) {
            a.actionPerformed(e);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        switch (style) {
            case SOLID: {
                if (mouseOver) {
                    g2.setColor(getBackground().brighter());
                }
                else {
                    g2.setColor(getBackground());
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                if (mouseOver) {
                    g2.setColor(g2.getColor().darker().darker());
                }
                g2.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, !getModel().isPressed());

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }

                getIcon().paintIcon(this, g2, (getWidth() / 2) - (getIcon().getIconWidth() / 2),
                                (getHeight() / 2) - (getIcon().getIconHeight() / 2));
            }
                break;

            case BBORDER: {
                g2.setColor(Color.darkGray);
                if (mouseOver) {
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                }
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }

                getIcon().paintIcon(this, g2, (getWidth() / 2) - (getIcon().getIconWidth() / 2),
                                (getHeight() / 2) - (getIcon().getIconHeight() / 2));

            }
                break;

            case TRANSPARENT: {
                Composite oldComp = g2.getComposite();
                Composite alphaComp;
                if (!mouseOver) {
                    alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                }
                else {
                    alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
                }
                g2.setComposite(alphaComp);

                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, !getModel().isPressed());

                g2.setComposite(oldComp);

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }

                getIcon().paintIcon(this, g2, (getWidth() / 2) - (getIcon().getIconWidth() / 2),
                                (getHeight() / 2) - (getIcon().getIconHeight() / 2));

            }
                break;
            case TITLEBTN: {
                Composite oldComp = g2.getComposite();
                Composite alphaComp;
                if (!mouseOver) {
                    alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                }
                else {
                    alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
                }
                g2.setComposite(alphaComp);
                /*
                 * g2.setColor(getBackground()); g2.fillRect(0, 0, getWidth(),
                 * getHeight()); g2.draw3DRect(0, 0, getWidth() - 1, getHeight()
                 * - 1, !getModel().isPressed()); /*
                 */

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }

                getIcon().paintIcon(this, g2, (getWidth() / 2) - (getIcon().getIconWidth() / 2),
                                (getHeight() / 2) - (getIcon().getIconHeight() / 2));

                g2.setComposite(oldComp);
            }
                break;
        }
    }


    private final MouseListener buttonMouseListener = new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof TitleBtn) {
                TitleBtn button = (TitleBtn) component;
                button.mouseOver = true;
            }
        }


        @Override
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof TitleBtn) {
                TitleBtn button = (TitleBtn) component;
                button.mouseOver = false;
            }
        }
    };
}
