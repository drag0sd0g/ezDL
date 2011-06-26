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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Config;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchToolState;



public final class SearchAction extends AbstractAction {

    public static enum Type {
        SEARCH(I18nSupport.getInstance().getLocString("ezdl.search.controls.search"), Icons.SEARCH.get16x16()), CANCEL(
                        I18nSupport.getInstance().getLocString("ezdl.search.controls.stopsearch"), Icons.CANCEL
                                        .get16x16());

        private String name;
        private Icon icon;


        private Type(String name, Icon icon) {
            this.name = name;
            this.icon = icon;
        }


        public String getName() {
            return name;
        }


        public Icon getIcon() {
            return icon;
        }
    }


    private static final int DEFAULT_TIMEOUT_S = 10;

    private static final long serialVersionUID = 4021229030018855304L;

    private Logger logger = Logger.getLogger(SearchAction.class);

    private ToolController tc;
    private Type type;


    public SearchAction() {
        type = Type.SEARCH;
        setType(type);
        tc = ToolController.getInstance();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        SearchTool searchTool = tc.getTool(SearchTool.class);

        switch (type) {
            case SEARCH: {
                performSearch(searchTool);
                break;
            }
            case CANCEL: {
                cancelSearch(searchTool);
                break;
            }
            default: {
                throw new RuntimeException("Unknown search action performed: " + type);
            }
        }
    }


    private void cancelSearch(SearchTool searchTool) {
        String requestIdToCancel = searchTool.getCurrentSearch().requestId;
        searchTool.applyState(SearchToolState.SEARCH_FINISHED);
        BackendEvent ask = new BackendEvent(this);
        ask.setContent(new CancelSearchNotify(requestIdToCancel));
        Dispatcher.postEvent(ask);
    }


    private void performSearch(SearchTool searchTool) {
        searchTool.startNewSearch();
        SearchTool.SearchContext search = searchTool.getCurrentSearch();

        List<String> wrapperList = search.wrapperIDs();

        if (!wrapperList.isEmpty()) {
            if (search.query != null && search.query.getTree() != null) {
                actuallyStartSearch(searchTool, search, wrapperList);
            }
        }
        else {
            final JRootPane dektopRootPane = tc.getDesktop().getDektopRootPane();
            final String message = I18nSupport.getInstance().getLocString(SearchTool.I18N_PREFIX + "dialog.no_dls");
            final String title = I18nSupport.getInstance().getLocString("error");
            JOptionPane.showMessageDialog(dektopRootPane, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }


    private void actuallyStartSearch(SearchTool searchTool, SearchTool.SearchContext search, List<String> wrapperList) {
        final ResultConfiguration resultConfig = search.resultConfig;
        final DocumentQuery documentQuery = new DocumentQuery(search.query, wrapperList);
        final DocumentQueryAsk s = new DocumentQueryAsk(documentQuery, resultConfig);
        s.setMaxDurationMs(getTimeoutMs());

        final BackendEvent query = new BackendEvent(searchTool);
        query.setContent(s);

        Dispatcher.postEvent(query);
        logger.info("Posted event " + query);

        searchTool.applyState(SearchToolState.SEARCH_IN_PROGRESS);
    }


    private int getTimeoutMs() {
        int timeoutS = Config.getInstance().getUserPropertyAsInt("search.timeout_s");
        if (timeoutS == 0) {
            timeoutS = DEFAULT_TIMEOUT_S;
        }
        return timeoutS * 1000;
    }


    public void setType(Type type) {
        firePropertyChange("type", this.type, type);
        this.type = type;
        putValue(Action.NAME, type.getName());
        putValue(Action.SMALL_ICON, type.getIcon());
    }

}
