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

package de.unidue.inf.is.ezdl.gframedl.tools.queryhistory;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.components.DefaultHighlightingListCellRenderer;
import de.unidue.inf.is.ezdl.gframedl.components.FilterTextField;
import de.unidue.inf.is.ezdl.gframedl.helper.ListItemStringRenderer;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;



public final class QueryHistoryListCellRenderer extends DefaultHighlightingListCellRenderer {

    private static final long serialVersionUID = 8021172557679142868L;


    public QueryHistoryListCellRenderer(FilterTextField filterTextField) {
        super(filterTextField);
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
        HistoricQuery q = (HistoricQuery) value;
        label.setIcon(Icons.MEDIA_QUERY.get22x22());
        label.setText(highlight(ListItemStringRenderer.render(q), false));
        return label;
    }

}
