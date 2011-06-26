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

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;



public class SortingListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -6963364262216611677L;


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
        Field sortingField = (Field) value;
        if (sortingField == null) {
            sortingField = Field.RSV;
        }
        try {
            final String fieldName = FieldRegistry.getInstance().getFieldName(sortingField);
            final String locString = I18nSupport.getInstance().getLocString(fieldName);
            final JLabel label = (JLabel) super.getListCellRendererComponent(list, locString, index, isSelected,
                            cellHasFocus);
            return label;
        }
        catch (NoSuchFieldCodeException e) {
            throw new RuntimeException("Unimplemented field: " + sortingField.name(), e);
        }
    }
}
