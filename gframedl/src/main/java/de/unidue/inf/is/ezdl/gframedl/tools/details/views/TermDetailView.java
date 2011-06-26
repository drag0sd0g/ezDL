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
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlfrontend.converter.SimpleObjectConversionStrategy;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.converter.HTMLConversionStrategy;



/**
 * DetailView for {@link Term}.
 */
public class TermDetailView extends DefaultDetailView {

    private static final long serialVersionUID = 5331012270877132674L;

    private static final SimpleObjectConversionStrategy strategy = new HTMLConversionStrategy();


    public TermDetailView() {
        super();
    }


    @Override
    public String getTabName() {
        return I18nSupport.getInstance().getLocString("ezdl.objects.term");
    }


    @Override
    public void setObject(DLObject o, List<String> highlightStrings) {
        super.setObject(o, highlightStrings);
        if (o instanceof Term) {
            editorPane.setText(strategy.print((Term) o).toString());
        }
    }


    @Override
    public Icon getIcon() {
        if (object != null) {
            return Icons.MEDIA_TERM.get22x22();
        }
        else {
            return null;
        }
    }
}
