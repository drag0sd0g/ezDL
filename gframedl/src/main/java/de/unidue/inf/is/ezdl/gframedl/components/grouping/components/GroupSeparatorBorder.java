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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.border.AbstractBorder;



/**
 * The separator border, used by Group & GroupsContainer.
 * 
 * @author RB1
 */
public class GroupSeparatorBorder extends AbstractBorder {

    private static final long serialVersionUID = 1L;

    public static final GroupSeparatorBorder DEFAULT_GROUP_CONTAINER_TITLE_BORDER = new GroupSeparatorBorder(
                    SystemColor.controlShadow, 1);
    public static final GroupSeparatorBorder DEFAULT_GROUP_CONTAINER_COMPONENT_BORDER = new GroupSeparatorBorder(
                    SystemColor.controlShadow, 1);
    // public static final GroupBorder DEFAULT_GROUP_CONTAINER_COMPONENT_BORDER
    // = new GroupBorder(SystemColor.controlHighlight,1);
    public static final GroupSeparatorBorder DEFAULT_GROUPS_CONTAINER_TITLE_BORDER = new GroupSeparatorBorder(
                    SystemColor.controlShadow, 2);

    private Color color;
    private int thickness;


    public GroupSeparatorBorder(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }


    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = 0;
        insets.left = 0;
        insets.bottom = thickness;
        insets.right = 0;
        return insets;
    }


    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, thickness, 0);
    }


    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color clr = color;
        for (int i = 0; i < thickness; i++) {
            g.setColor(clr);
            g.drawLine(0, height - 1 - i, width, height - 1 - i);
            clr = clr.brighter();
        }
    }
}
