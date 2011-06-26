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

package de.unidue.inf.is.ezdl.gframedl.tools.search.renderers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Order;
import de.unidue.inf.is.ezdl.gframedl.Icons;



public class SortOrderListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 7287325218082843971L;


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Order o = (Order) value;
        switch (o) {
            case ASCENDING: {
                label.setIcon(Icons.UP_ACTION.get16x16());
                break;
            }
            case DESCENDING: {
                label.setIcon(Icons.DOWN_ACTION.get16x16());
                break;
            }
            default: {
                label.setIcon(null);
                break;
            }
        }
        label.setText("");
        return label;
    }
}
