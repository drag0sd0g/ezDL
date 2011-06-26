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

package de.unidue.inf.is.ezdl.gframedl.tools.queryhistory;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.components.FilterTextField;
import de.unidue.inf.is.ezdl.gframedl.components.TextComponentPopupMenu;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent;
import de.unidue.inf.is.ezdl.gframedl.events.ExecuteSearchEvent;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.queryhistory.actions.Actions;



public final class QueryHistoryView extends AbstractToolView {

    private static final long serialVersionUID = 7044568164115108366L;

    private JLabel infoLabel;
    private JXList list;
    private JButton clearButton;
    private JLabel filterLabel;
    private FilterTextField filterTextField;

    private QueryHistoryListModel listModel;

    private JPopupMenu contextMenu;
    private JMenuItem setMenuItem;
    private JMenuItem setAndExcecuteMenuItem;


    protected JList getQueryList() {
        return list;
    }


    public QueryHistoryView(Tool parentTool) {
        super(parentTool);
        createContent();
    }


    private void createContent() {
        infoLabel = new JLabel();
        infoLabel.setText("<html>" + I18nSupport.getInstance().getLocString("ezdl.tools.history.description")
                        + "<html>");

        list = new JXList();
        listModel = new QueryHistoryListModel();
        Timer t = new Timer(5000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.refresh();
            }
        });
        t.start();
        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filterTextField = new FilterTextField(list);
        TextComponentPopupMenu.addPopupMenu(filterTextField);
        list.setCellRenderer(new QueryHistoryListCellRenderer(filterTextField));
        list.addMouseListener(new MouseAdapter() {

            private boolean itemSelected;


            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Actions.SET_AND_EXECUTE_QUERY_ACTION.actionPerformed(new ActionEvent(list,
                                    ActionEvent.ACTION_PERFORMED, ""));
                }
            }


            @Override
            public void mousePressed(MouseEvent e) {
                int clickedRow = list.locationToIndex(e.getPoint());
                itemSelected = clickedRow != -1;
                if (!list.isSelectedIndex(clickedRow)) {
                    list.setSelectedIndex(clickedRow);
                }
                if (itemSelected && e.isPopupTrigger()) {
                    contextMenu.show(list, e.getX(), e.getY());
                }

                if (clickedRow != -1) {
                    DLObject elem = (DLObject) listModel.getElementAt(clickedRow);
                    DetailEvent.fireDetailEvent(this, elem, DetailEvent.OpenMode.DEFAULT);
                }
            }


            @Override
            public void mouseReleased(MouseEvent e) {
                if (itemSelected && e.isPopupTrigger()) {
                    contextMenu.show(list, e.getX(), e.getY());
                }
            }
        });
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Actions.SET_QUERY_ACTION.setEnabled(e.getFirstIndex() != -1);
                Actions.SET_AND_EXECUTE_QUERY_ACTION.setEnabled(e.getFirstIndex() != -1);
            }
        });

        filterLabel = new JLabel(I18nSupport.getInstance().getLocString("ezdl.controls.label.filter"));

        clearButton = new JButton(Actions.CLEAR_ACTION);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(clearButton, BorderLayout.EAST);

        contextMenu = new JPopupMenu();
        setMenuItem = new JMenuItem(Actions.SET_QUERY_ACTION);
        setAndExcecuteMenuItem = new JMenuItem(Actions.SET_AND_EXECUTE_QUERY_ACTION);
        contextMenu.add(setMenuItem);
        contextMenu.add(setAndExcecuteMenuItem);

        Actions.SET_QUERY_ACTION.setEnabled(false);
        Actions.SET_AND_EXECUTE_QUERY_ACTION.setEnabled(false);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        add(infoLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 5);
        add(filterLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        add(filterTextField, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        add(new JScrollPane(list), c);
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        add(buttonPanel, c);
    }


    public void queryChosen(boolean execute) {
        HistoricQuery query = (HistoricQuery) list.getSelectedValue();
        if (query != null) {
            ExecuteSearchEvent queryFromHistorySelectedEvent = new ExecuteSearchEvent(this, query.getWrappers(),
                            query.getQuery(), execute);
            Dispatcher.postEvent(queryFromHistorySelectedEvent);

            UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(),
                            "queryfromhistory");
            userLogNotify.addParameter("query", query.asString());
            userLogNotify.addParameter("execute", String.valueOf(execute));
            Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
        }
    }


    public void addQueryToHistory(HistoricQuery query) {
        listModel.add(0, query);
    }


    public void addQueryHistoryToHistory(List<HistoricQuery> queries) {
        for (HistoricQuery historicQuery : queries) {
            listModel.add(listModel.size(), historicQuery);
        }
    }


    public void clear() {
        listModel.clear();
    }
}
