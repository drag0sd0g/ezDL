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

package de.unidue.inf.is.ezdl.gframedl.components.checkboxlist;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.unidue.inf.is.ezdl.gframedl.components.dynamiclist.DynamicList;



public class CheckBoxJList extends DynamicList implements MouseListener {

    private static final long serialVersionUID = -6776112121543343775L;


    public CheckBoxJList() {
        super();
        addMouseListener(this);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        JList list = (JList) e.getSource();

        // Get index of item clicked
        int index = list.locationToIndex(e.getPoint());
        Rectangle rect = list.getCellBounds(index, index);

        if (rect != null && rect.contains(e.getPoint())) {
            rect.width = 120;
            CheckBoxListItem item = (CheckBoxListItem) list.getModel().getElementAt(index);

            if (item != null) {
                // Toggle selected state
                item.setSelected(!item.isSelected());

                // Repaint cell
                list.repaint(list.getCellBounds(index, index));
            }
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {
    }


    @Override
    public void mouseExited(MouseEvent e) {
    }


    @Override
    public void mousePressed(MouseEvent e) {
    }


    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
