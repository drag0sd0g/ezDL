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

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.QueryHistoryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.QueryHistoryTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.StoreQueryHistoryNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.SearchEvent;
import de.unidue.inf.is.ezdl.gframedl.query.HistoricQuery;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.transfer.DLObjectTransferable;



public final class QueryHistoryTool extends AbstractTool {

    private static final long serialVersionUID = 167027352569906725L;

    public static final String I18N_PREFIX = "ezdl.tools.history.";

    private QueryHistoryView queryHistoryView;


    public QueryHistoryTool() {
        initialize();
    }


    private void initialize() {
        Dispatcher.registerInterest(this, SearchEvent.class);
        Dispatcher.registerInterest(this, BackendEvent.class);
        sendQueryHistoryAskToBackend();
    }


    private void sendQueryHistoryAskToBackend() {
        BackendEvent backendEvent = new BackendEvent(this, new QueryHistoryAsk(ToolController.getInstance()
                        .getSessionId()));
        Dispatcher.postEvent(backendEvent);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev.getSource() == this) {
            return false;
        }
        else if (ev instanceof SearchEvent) {
            handleSearchEvent((SearchEvent) ev);
        }
        else if (ev instanceof BackendEvent) {
            handleBackendEvent((BackendEvent) ev);
        }
        return true;
    }


    private void handleBackendEvent(BackendEvent ev) {
        MessageContent content = ev.getContent();
        if (content instanceof QueryHistoryTell) {
            handleQueryHistoryTell((QueryHistoryTell) content);
        }
    }


    private void handleQueryHistoryTell(QueryHistoryTell content) {
        List<HistoricQuery> historicQueries = new ArrayList<HistoricQuery>();

        for (de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery historicBackendQuery : content.getQueryHistory()) {
            HistoricQuery historicQuery;
            historicQuery = new HistoricQuery(historicBackendQuery.getWrappers(), historicBackendQuery.getQuery(),
                            historicBackendQuery.getQueryId(), historicBackendQuery.getSearchResultsCount(),
                            historicBackendQuery.getTimestamp());
            historicQueries.add(historicQuery);
        }
        queryHistoryView.addQueryHistoryToHistory(historicQueries);
    }


    private void handleSearchEvent(SearchEvent ev) {
        HistoricQuery query = new HistoricQuery(ev.getWrappers(), ev.getQuery(), ev.getQueryId(),
                        ev.getSearchResultsCount(), ev.getTimestamp());
        queryHistoryView.addQueryToHistory(query);

        de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery historicQuery = new de.unidue.inf.is.ezdl.dlcore.data.query.HistoricQuery(
                        query.getWrappers(), query.getQuery(), query.getQueryId(), query.getSearchResultsCount(),
                        query.getTimestamp());
        Dispatcher.postEvent(new BackendEvent(this, new StoreQueryHistoryNotify(ToolController.getInstance()
                        .getSessionId(), historicQuery)));
    }


    public void clear() {
        queryHistoryView.clear();
    }


    @Override
    public List<ToolView> createViews() {
        queryHistoryView = new QueryHistoryView(this);
        queryHistoryView.getQueryList().setTransferHandler(new QueryTransferHandler());
        queryHistoryView.getQueryList().setDragEnabled(true);

        return Arrays.<ToolView> asList(queryHistoryView);
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.QUERY_HISTORY_TOOL.toIconsTuple();
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }


    private static class QueryTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -3497363730382420798L;


        @Override
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList) c;
            Object[] values = list.getSelectedValues();

            return new DLObjectTransferable((DLObject) values[0]);
        }


        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY;
        }

    }

}
