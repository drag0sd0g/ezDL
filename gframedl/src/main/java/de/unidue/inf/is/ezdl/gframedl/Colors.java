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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.Color;

import javax.swing.UIManager;



/**
 * Colors for text highlighting.
 * 
 * @author tbeckers
 */
public final class Colors {

    /**
     * foreground color for highlighted text
     */
    public static final Color HIGHLIGHT_FOREGROUND_COLOR = new Color(210, 105, 030);
    /**
     * background color for highlighted text
     */
    public static final Color HIGHLIGHT_BACKGROUND_COLOR = Color.WHITE;
    /**
     * foreground color for selected highlighted text
     */
    public static final Color SELECTED_HIGHLIGHT_FOREGROUND_COLOR = new Color(139, 253, 0);

    /**
     * foreground color for highlighted filter text
     */
    public static final Color FILTER_HIGHLIGHT_FOREGROUND_COLOR = UIManager.getColor("Label.background");
    /**
     * background color for highlighted filter text
     */
    public static final Color FILTER_HIGHLIGHT_BACKGROUND_COLOR = UIManager.getColor("textHighlight");


    private Colors() {
    }

}
