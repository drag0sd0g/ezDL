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

package de.unidue.inf.is.ezdl.gframedl.tools.search;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.ResultPanel;



/**
 * Implements that view of the {@link SearchTool} that has the result list in
 * it.
 * 
 * @author tbeckers
 */
public final class ResultView extends AbstractToolView {

    private static final long serialVersionUID = 1736044856551650513L;

    private ResultPanel resultPanel;


    /**
     * Constructor.
     * 
     * @param tool
     *            reference to the tool the view belongs to (this should be the
     *            {@link SearchTool})
     * @param index
     *            SearchTool-local ID of this view
     */
    public ResultView(Tool tool, int index) {
        super(tool, index);
        createContent();
    }


    private void createContent() {
        setLayout(new GridBagLayout());
        resultPanel = new ResultPanel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        add(resultPanel, c);
    }


    public void clearResultList() {
        resultPanel.clear();
    }


    public Sorting getSorting() {
        return resultPanel.getSorting();
    }


    public void setQueryForHighlighting(Query query) {
        resultPanel.setQueryForHighlighting(query);
    }


    public void addResultListData(List<ResultItem> partialResults) {
        resultPanel.addResultListData(partialResults);
    }


    @Override
    public String getToolViewName() {
        return I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "results.name");
    }


    public void applySorting() {
        resultPanel.applySorting();
    }


    public void updateSorting(List<Field> fields) {
        resultPanel.updateSorting(fields);
    }


    public ResultPanel getResultPanel() {
        return resultPanel;
    }
}
