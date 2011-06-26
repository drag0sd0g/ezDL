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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.SystemUtils.OperatingSystem;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryContainer;
import de.unidue.inf.is.ezdl.gframedl.components.GroupBox;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.AdvancedFormQueryView;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.BasicQueryView;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.QueryView;



/**
 * This class is the ancestor of QueryPanel and HistoricQueryPanel.
 */
public class AbstractQueryPanel extends GroupBox implements QueryContainer {

    private static final long serialVersionUID = 4371421212941606137L;

    /**
     * The button Panel.
     */
    protected JPanel buttonPanel;

    /**
     * Tab Tabs (form view, query view).
     */
    protected JTabbedPane tabPane;
    /**
     * The search button.
     */
    protected JButton searchButton;
    /**
     * Clear button.
     */
    protected JButton clearButton;
    /**
     * The query.
     */
    protected Query query;


    /**
     * Constructor.
     */
    public AbstractQueryPanel() {
        super(I18nSupport.getInstance().getLocString("ezdl.controls.querypanel.label"));
        setLayout(new BorderLayout());
        clearQuery();
        init();
    }


    private void init() {
        add(initTabPane(), BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }


    private JPanel getButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(getClearButton());
        buttonPanel.add(getSearchButton());
        return buttonPanel;
    }


    /**
     * Returns JTabbedPane with two QueryViews {SingleQueryView, FormQueryView}
     * 
     * @return
     */
    protected JTabbedPane initTabPane() {
        tabPane = new JTabbedPane();

        if (SystemUtils.OS == OperatingSystem.MAC_OS) {
            tabPane.setTabPlacement(SwingConstants.TOP);

        }
        else {
            tabPane.setTabPlacement(SwingConstants.LEFT);
        }

        tabPane.addTab(I18nSupport.getInstance().getLocString("ezdl.controls.querypanel.tab_label.advancedquery"),
                        new AdvancedFormQueryView(this));
        tabPane.addTab(I18nSupport.getInstance().getLocString("ezdl.controls.querypanel.tab_label.basicquery"),
                        new BasicQueryView(this));
        tabPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                Object form = e.getSource();
                if (form instanceof JTabbedPane) {
                    JTabbedPane pane = (JTabbedPane) form;
                    Component selected = pane.getSelectedComponent();
                    if (selected instanceof QueryView) {
                        QueryView queryView = (QueryView) selected;
                        queryView.updateView();
                    }
                }
            }

        });
        return tabPane;
    }


    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton();
        }
        return searchButton;
    }


    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
        }
        return clearButton;
    }


    /**
     * Delete's the query.
     */
    public void resetQuery() {
        clearQuery();
        for (Component comp : tabPane.getComponents()) {
            if (comp instanceof QueryView) {
                QueryView queryView = (QueryView) comp;
                queryView.resetView();
            }
        }
    }


    /**
     * Returns the query.
     * 
     * @return
     */
    @Override
    public Query getQuery() {
        return query;
    }


    /**
     * Sets the query and updates the forms.
     * 
     * @param query
     *            The query
     */
    @Override
    public void setQuery(Query query) {
        this.query = query;
        if (tabPane != null) {
            for (Component comp : tabPane.getComponents()) {
                if (comp instanceof QueryView) {
                    QueryView queryView = (QueryView) comp;
                    queryView.updateView();
                }
            }
        }
    }


    /**
     * Sets the query, but does not update the forms.
     * 
     * @param query
     *            The query
     */
    @Override
    public void setQueryInBackground(Query query) {
        this.query = query;
    }


    private void clearQuery() {
        setQuery(new DefaultQuery());
    }


    public void selectFreeText() {
        tabPane.setSelectedIndex(1);
    }

}
