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

package de.unidue.inf.is.ezdl.gframedl.tools.clipboard;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.helper.ListItemStringRenderer;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;



public class ClipboardListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 6161801295007042759L;


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

        JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof DLObject) {
            DLObject dObject = (DLObject) value;
            c.setIcon(getIcon(dObject));
            c.setText(ListItemStringRenderer.render(dObject));
        }
        return c;
    }


    /**
     * Factory method for icons. Returns the icon that represents the given
     * object.
     * 
     * @param object
     *            the object whose icon to retrieve
     * @return the icon for the object or a default icon if no special icon is
     *         found
     */
    private Icon getIcon(DLObject object) {
        if (object instanceof Person) {
            return Icons.MEDIA_AUTHOR.get16x16();
        }
        else if (object instanceof HistoricQuery) {
            return Icons.MEDIA_QUERY.get16x16();
        }
        else if (object instanceof Document) {
            return Icons.MEDIA_DOCUMENT.get16x16();
        }
        else if (object instanceof Term) {
            return Icons.MEDIA_TERM.get16x16();
        }
        else {
            return Icons.DEFAULT.get16x16();
        }
    }
}
