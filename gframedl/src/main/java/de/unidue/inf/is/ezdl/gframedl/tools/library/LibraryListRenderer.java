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

package de.unidue.inf.is.ezdl.gframedl.tools.library;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.helper.ListItemStringRenderer;



/** List renderer for the library List */
public final class LibraryListRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -120363162044247759L;


    // private I18nSupport i18n = I18nSupport.getInstance();

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {

        JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Document) {
            Document dObject = (Document) value;

            String refsystemid = (String) (dObject).getFieldValue(Field.REFERENCESYSTEMID);
            /**
             * has online referencesystem id. document is synchronized with
             * online reference system
             */
            if (refsystemid != null) {
                c.setIcon(Icons.MEDIA_DOCUMENTONLINE.get22x22());
            }
            else {
                c.setIcon(Icons.MEDIA_DOCUMENT.get22x22());
            }

            c.setVerticalAlignment(SwingConstants.TOP);
            c.setText(ListItemStringRenderer.render(dObject, true));

        }
        return c;
    }
}
