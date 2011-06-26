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

package de.unidue.inf.is.ezdl.gframedl.tools.details.views;

import java.util.List;

import javax.swing.Icon;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlfrontend.converter.SimpleObjectConversionStrategy;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.converter.HTMLConversionStrategy;



/**
 * DetailView for {@link Person}.
 */
public class AuthorDetailView extends DefaultDetailView {

    private static final long serialVersionUID = 1941115097685449467L;

    private static final SimpleObjectConversionStrategy strategy = new HTMLConversionStrategy();


    public AuthorDetailView() {
        super();
    }


    @Override
    public String getTabName() {
        if (object != null) {
            return StringUtils.shortenString(((Person) object).getLastName(), 10);
        }
        else {
            return null;
        }
    }


    @Override
    public void setObject(DLObject o, List<String> highlightStrings) {
        super.setObject(o, highlightStrings);
        if (o instanceof Person) {
            editorPane.setText(strategy.print((Person) o).toString());
        }
    }


    @Override
    public Icon getIcon() {
        if (object != null) {
            return Icons.MEDIA_AUTHOR.get22x22();
        }
        else {
            return null;
        }
    }
}
