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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListCellRenderer;

import de.unidue.inf.is.ezdl.gframedl.Colors;
import de.unidue.inf.is.ezdl.gframedl.utils.HighlightingUtils;



public class DefaultHighlightingListCellRenderer extends DefaultListCellRenderer implements FilterChangeListener {

    private static final long serialVersionUID = -7506650915968617281L;

    private static final String FILTER_HIGHLIGHT_COLOR_FOREGROUND = HighlightingUtils
                    .colorToHex(Colors.FILTER_HIGHLIGHT_FOREGROUND_COLOR);
    private static final String FILTER_HIGHLIGHT_COLOR_BACKGROUND = HighlightingUtils
                    .colorToHex(Colors.FILTER_HIGHLIGHT_BACKGROUND_COLOR);

    private List<String> filterStrings;


    public DefaultHighlightingListCellRenderer(FilterTextField filterTextField) {
        if (filterTextField != null) {
            this.filterStrings = new ArrayList<String>();
            filterTextField.addFilterChangeListener(this);
        }
    }


    @Override
    public void filterHasChanged(String filter) {
        filterStrings.clear();
        filterStrings.addAll(Arrays.asList(filter.split("\\s")));
    }


    protected final String highlight(String s, boolean escape) {
        return HighlightingUtils.highlightParts(s, escape, filterStrings, true, FILTER_HIGHLIGHT_COLOR_FOREGROUND,
                        FILTER_HIGHLIGHT_COLOR_BACKGROUND);
    }

}
