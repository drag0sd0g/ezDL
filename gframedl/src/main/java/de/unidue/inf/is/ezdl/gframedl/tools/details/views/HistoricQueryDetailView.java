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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.QueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.events.ExecuteSearchEvent;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/**
 * DetailView for the HistoricQuery class.
 */
public class HistoricQueryDetailView extends JScrollPane implements DetailView {

    private static final long serialVersionUID = 1169636339726267071L;

    private HistoricQueryPanel queryPanel;
    private DLObject object;


    /**
     * Constructor.
     */
    public HistoricQueryDetailView() {
        super();
    }


    @Override
    public void setObject(DLObject o, List<String> highlightStrings) {
        object = o;
        if (o != null) {
            setQuery();
        }
    }


    @Override
    public DLObject getObject() {
        return object;
    }


    @Override
    public String getTabName() {
        return I18nSupport.getInstance().getLocString("ezdl.objects.query");
    }


    @Override
    public Icon getIcon() {
        return Icons.QUERY_HISTORY_TOOL.get16x16();
    }


    private void setQuery() {
        HistoricQuery q = (HistoricQuery) getObject();
        queryPanel = new HistoricQueryPanel(q);
        setViewportView(queryPanel);
    }


    private static class HistoricQueryPanel extends JPanel {

        private static final long serialVersionUID = 98258112916298732L;

        private JTextArea queryTextArea;
        private JButton searchButton;

        private HistoricQuery hquery;


        /**
         * Constructor.
         * 
         * @param hquery
         *            the HistoricQuery to display.
         */
        public HistoricQueryPanel(HistoricQuery hquery) {
            super(new GridBagLayout());

            this.hquery = hquery;

            queryTextArea = new JTextArea();
            queryTextArea.setWrapStyleWord(true);
            queryTextArea.setCaretPosition(0);
            queryTextArea.setEnabled(false);
            queryTextArea.setLineWrap(true);
            QueryFactory queryFactory = new WebLikeFactory(FieldRegistry.getInstance());
            String queryString;
            if (hquery != null) {
                queryString = queryFactory.getTextForQueryNode(hquery.getQuery().getTree());
            }
            else {
                queryString = "";
            }
            queryTextArea.setText(queryString);

            searchButton = new JButton();
            searchButton.setText(I18nSupport.getInstance()
                            .getLocString("ezdl.tools.queryhistorydetailview.repeatquery"));

            searchButton.setIcon(Icons.SEARCH_TOOL.get16x16());
            searchButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    repeatQuery();
                }
            });

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(queryTextArea, c);
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0;
            c.weighty = 0;
            c.fill = GridBagConstraints.NONE;
            add(searchButton, c);

            // clearButton.setText(I18nSupport.getInstance().getLocString("ezdl.search.controls.clear"));
            // clearButton.setIcon(Icons.CLEAR_ACTION.get16x16());
            // clearButton.addActionListener(new ActionListener() {
            //
            // public void actionPerformed(ActionEvent e) {
            // resetQuery();
            // }
            // });

        }


        private void repeatQuery() {
            if (hquery != null) {
                ExecuteSearchEvent executeSearchEvent = new ExecuteSearchEvent(this, hquery.getWrappers(),
                                hquery.getQuery(), true);
                Dispatcher.postEvent(executeSearchEvent);
            }
        }

    }


    @Override
    public List<Action> getPossibleActions() {
        return null;
    }
}