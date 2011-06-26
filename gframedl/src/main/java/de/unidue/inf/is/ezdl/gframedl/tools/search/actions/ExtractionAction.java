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

package de.unidue.inf.is.ezdl.gframedl.tools.search.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.ExtractionEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionTool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionType;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool.SearchContext;



/**
 * Is triggered by the <code>SearchTool</code> and dispatches an
 * <code>ExtractionEvent</code> containing the current <code>DocumentList</code>
 * in the result list.
 * 
 * @see ExtractionEvent
 * @author tacke
 */
public final class ExtractionAction extends AbstractAction {

    private static final long serialVersionUID = 2138588959409531016L;

    private ExtractionType type;

    private ToolController tc = ToolController.getInstance();


    /**
     * @param type
     *            There are three different types of extraction events:
     *            <ul>
     *            <li>Extracting Tags/Terms ({@link #TAGS})</li>
     *            <li>Extracting Authors ({@link #AUTHORS})</li>
     *            <li>Extracting Years ({@link #YEARS})</li>
     *            </ul>
     *            The {@link ExtractionTool} might handle the supplied result
     *            data differently according to the type
     */
    public ExtractionAction(ExtractionType type) {

        this.type = type;

        putValue(Action.NAME, type.getName());
        putValue(Action.SMALL_ICON, type.getIcon());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        SearchTool tool = tc.getTool(SearchTool.class);
        SearchContext search = tool.getCurrentSearch();
        if (search == null) {
            return;
        }

        if (search.documentList != null) {
            ExtractionEvent.fireExtractionEvent(e, type, search.documentList);
        }
    }
}
