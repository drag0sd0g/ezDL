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

package de.unidue.inf.is.ezdl.gframedl.components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



/**
 * a JPanel with a titled line at the top, to be used to group components
 * according to the ezDL HIG
 */
public class GroupBox extends JPanel {

    private static final long serialVersionUID = -3390680145786062890L;
    private TitledBorder border;


    public GroupBox(final String title) {
        super();
        border = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), title);

        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));

        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0),
                        BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 0, 0, 0))));
    }


    /**
     * A method to change the border title.
     * 
     * @param title
     *            Text of the new title.
     */
    public void updateTitle(String title) {
        border.setTitle(title);
    }
}
