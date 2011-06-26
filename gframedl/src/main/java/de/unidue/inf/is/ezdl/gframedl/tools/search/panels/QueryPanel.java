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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels;

import java.awt.Component;

import javax.swing.JTabbedPane;

import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.FormMediator;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.QueryView;



/**
 * Contains one or multiple views of the query object - AKA search forms.
 */
public class QueryPanel extends AbstractQueryPanel {

    private static final long serialVersionUID = 552986113216760050L;


    /**
     * Constructor.
     */
    public QueryPanel() {
        super();
        searchButton.setAction(Actions.SEARCH_ACTION);
        clearButton.setAction(Actions.CLEAR_QUERY_ACTION);
    }


    /**
     * In addition to the inherited method, the QueryView's are added to the
     * FormMediator.
     */
    @Override
    protected JTabbedPane initTabPane() {
        JTabbedPane result;
        result = super.initTabPane();
        for (int i = 0; i < result.getTabCount(); i++) {
            Component o = result.getComponentAt(i);
            if (o instanceof QueryView) {
                FormMediator.instance().add((QueryView) o);
            }
        }
        return result;
    }

}
