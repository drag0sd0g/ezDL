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

package de.unidue.inf.is.ezdl.gframedl.tools.search.projection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ListCellRenderer;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;



public class CompactProjection implements Projection {

    private static final Icon IMAGE_ICON = Icons.COMPACT_VIEW_ACTION.get16x16();

    private I18nSupport i18n = I18nSupport.getInstance();

    private List<Field> fields = Collections.unmodifiableList(Arrays.asList(Field.AUTHOR, Field.TITLE, Field.YEAR,
                    Field.ABSTRACT));


    public CompactProjection() {
    }


    @Override
    public List<Field> getFields() {
        return fields;
    }


    @Override
    public String getName() {
        return i18n.getLocString("ezdl.controls.resultlistpanel.label.view.compact");
    }


    @Override
    public ListCellRenderer getRenderer() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Icon getIcon() {
        return IMAGE_ICON;
    }


    @Override
    public String toString() {
        return getName();
    }

}
