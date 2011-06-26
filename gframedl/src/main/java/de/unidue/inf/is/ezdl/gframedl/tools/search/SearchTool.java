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

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryInfoNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryResultTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple.WebLikeFactory;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.ExecuteInternalQueryEvent;
import de.unidue.inf.is.ezdl.gframedl.events.ExecuteSearchEvent;
import de.unidue.inf.is.ezdl.gframedl.events.SearchEvent;
import de.unidue.inf.is.ezdl.gframedl.events.StatusEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.SearchAction.Type;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews.FormMediator;
import de.unidue.inf.is.ezdl.gframedl.tools.search.projection.Projection;



public final class SearchTool extends AbstractTool {

    public static final String I18N_PREFIX = "ezdl.tools.search.";


    public static class SearchContext {

        public Query query;
        public ResultConfiguration resultConfig;
        public String requestId;
        public Projection projection = null;
        public Sorting sorting = null;
        public int resultCount = 0;
        public ResultDocumentList documentList = null;
        public List<FrontendWrapperInfo> usedWrappers;


        /**
         * Returns the IDs of the wrappers that were used for this search.
         * 
         * @return the list of wrapper IDs
         */
        public List<String> wrapperIDs() {
            LinkedList<String> ids = new LinkedList<String>();
            if (usedWrappers != null) {
                for (FrontendWrapperInfo info : usedWrappers) {
                    ids.add(info.getId());
                }
            }
            return ids;
        }
    }


    private List<Field> summaryFields;

    private Logger logger = Logger.getLogger(SearchTool.class);

    private Set<String> seenIds;
    private QueryView queryView;
    private ResultView resultView;
    private WrapperChoiceView wrapperChoiceView;
    private Map<String, String> wrapperNames;

    /**
     * The context of the current search.
     */
    private SearchContext currentSearch;
    /**
     * The state the search tool is in.
     */
    private SearchToolState state;


    public SearchTool() {
        init();
    }


    private void init() {
        FormMediator.instance().add(this);
        seenIds = new HashSet<String>();
        currentSearch = new SearchContext();
        wrapperNames = new HashMap<String, String>();
        sendWrapperListRequest();
        applyState(SearchToolState.INIT);

        summaryFields = new ArrayList<Field>();
        summaryFields.add(Field.AUTHOR);
        summaryFields.add(Field.TITLE);
        summaryFields.add(Field.YEAR);
        summaryFields.add(Field.RSV);
        summaryFields.add(Field.ISBN);

        Dispatcher.registerInterest(this, BackendEvent.class);
        Dispatcher.registerInterest(this, ExecuteSearchEvent.class);
        Dispatcher.registerInterest(this, ExecuteInternalQueryEvent.class);

    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.SEARCH_TOOL.toIconsTuple();
    }


    @Override
    public String getName() {
        return I18nSupport.getInstance().getLocString(I18N_PREFIX + "search.name");
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }


    @Override
    public List<ToolView> createViews() {
        queryView = new QueryView(this, 0);
        resultView = new ResultView(this, 1);
        wrapperChoiceView = new WrapperChoiceView(this, 2);
        return Arrays.<ToolView> asList(queryView, resultView, wrapperChoiceView);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev.getSource() == this) {
            return false;
        }
        else if (ev instanceof BackendEvent) {
            MessageContent messageContent = ((BackendEvent) ev).getContent();
            handleIncomingBackendEvent(messageContent);
        }
        else if (ev instanceof ExecuteSearchEvent) {
            handleExecuteSearchEvent((ExecuteSearchEvent) ev);
        }
        else if (ev instanceof ExecuteInternalQueryEvent) {
            handleExecuteInternalQueryEvent((ExecuteInternalQueryEvent) ev);
        }
        return true;
    }


    private void handleExecuteInternalQueryEvent(ExecuteInternalQueryEvent ev) {
        try {
            queryView.setQuery(new WebLikeFactory(FieldRegistry.getInstance()).parse(ev.getQueryString()));
            Actions.SEARCH_ACTION.actionPerformed(null);
        }
        catch (Exception e) {
            logger.error(e);
        }
    }


    private void handleExecuteSearchEvent(ExecuteSearchEvent ev) {
        queryView.setQuery(ev.getQuery());
        wrapperChoiceView.setCheckedWrappers(ev.getWrappers());

        if (ev.shouldBeExecuted()) {
            Actions.SEARCH_ACTION.actionPerformed(null);
        }
    }


    private void handleIncomingBackendEvent(MessageContent messageContent) {
        if (messageContent instanceof DocumentQueryInfoNotify) {
            handleDocumentQueryInfoMessage((DocumentQueryInfoNotify) messageContent);
        }
        else if (messageContent instanceof DocumentQueryResultTell) {
            handleDocumentQueryTellMessage((DocumentQueryResultTell) messageContent);
        }
        else if (messageContent instanceof AvailableWrappersTell) {
            handleAvailableWrappersTellMessage((AvailableWrappersTell) messageContent);
        }
    }


    private void handleAvailableWrappersTellMessage(AvailableWrappersTell messageContent) {
        List<FrontendWrapperInfo> wrapperInfos = messageContent.getWrapperInfos();
        wrapperNames.clear();
        for (FrontendWrapperInfo wrapperInfo : wrapperInfos) {
            wrapperNames.put(wrapperInfo.getId(), wrapperInfo.getRemoteName());
        }
        wrapperChoiceView.update(wrapperInfos);
    }


    private void handleDocumentQueryTellMessage(DocumentQueryResultTell messageContent) {
        logger.debug("Got result list message: " + messageContent);

        ResultDocumentList results = messageContent.getResults();

        currentSearch.documentList = results;
        currentSearch.resultCount = results.size();

        postSearchEvent();
        resultView.setQueryForHighlighting(currentSearch.query);

        List<ResultItem> resultItems = createResultItems(results);

        resultView.clearResultList();
        resultView.addResultListData(resultItems);
        resultView.updateSorting(currentSearch.resultConfig.getFields());

        if (currentSearch.resultCount > 0) {
            // applyFilter();
            applySorting();
        }

        applyState(SearchToolState.SEARCH_FINISHED);
    }


    /**
     * Notify subscribers that a search has been finished.
     */
    private void postSearchEvent() {
        SearchEvent searchEvent = new SearchEvent(this, currentSearch.query, currentSearch.resultCount, new Date(),
                        currentSearch.requestId, currentSearch.wrapperIDs());
        Dispatcher.postEvent(searchEvent);
    }


    private void setStatusToSearching() {
        StatusEvent statusEvent = new StatusEvent(this);
        statusEvent.setText(I18nSupport.getInstance().getLocString(I18N_PREFIX + "status.started"));
        Dispatcher.postEvent(statusEvent);
    }


    private void setStatusToSearchingFinished() {
        StatusEvent statusEvent = new StatusEvent(this);
        statusEvent.setText(I18nSupport.getInstance().getLocString(I18N_PREFIX + "status.finished"));
        Dispatcher.postEvent(statusEvent);
    }


    private List<ResultItem> createResultItems(ResultDocumentList results) {
        List<ResultItem> resultItems = new ArrayList<ResultItem>();

        for (ResultDocument result : results) {
            resultItems.add(new ResultItem(result, wrapperNames));
        }
        return resultItems;
    }


    private void handleDocumentQueryInfoMessage(DocumentQueryInfoNotify documentQueryInfoTell) {
        currentSearch.resultCount = documentQueryInfoTell.getCount();
        currentSearch.requestId = documentQueryInfoTell.getRequestId();

        applyState(SearchToolState.AWAITING_SEARCH_RESULTS);
    }


    public void applySorting() {
        resultView.applySorting();
    }


    /**
     * Set the SearchTool to a new state, executing state transition specific
     * commands.
     * 
     * @param searchToolState
     *            the new state
     */
    public void applyState(SearchToolState newState) {
        getViews();
        performStateChange(newState);
        FormMediator.instance().tellChanged(this);
    }


    /**
     * Changes to the new state and performs corresponding transitional actions.
     * 
     * @param newState
     *            the state to change to
     */
    private void performStateChange(SearchToolState newState) {
        state = newState;
        switch (state) {
            case INIT: {
                Actions.SEARCH_ACTION.setEnabled(true);
                Actions.CLEAR_QUERY_ACTION.setEnabled(true);
                Actions.SORT_BY_ACTION.setEnabled(false);
                Actions.SORT_ORDER_ACTION.setEnabled(false);
                Actions.PROJECTION_ACTION.setEnabled(false);
                Actions.FILTER_ACTION.setEnabled(false);
                Actions.DISABLE_EXTRACT_ACTION.setEnabled(false);
                Actions.WRAPPER_UPDATE_ACTION.setEnabled(true);
                queryView.setCursor(Cursor.getDefaultCursor());
                wrapperChoiceView.setCursor(Cursor.getDefaultCursor());
                break;
            }
            case SEARCH_IN_PROGRESS: {
                Actions.SEARCH_ACTION.setType(Type.CANCEL);
                Actions.SEARCH_ACTION.setEnabled(false);
                Actions.CLEAR_QUERY_ACTION.setEnabled(false);
                Actions.SORT_BY_ACTION.setEnabled(false);
                Actions.SORT_ORDER_ACTION.setEnabled(false);
                Actions.PROJECTION_ACTION.setEnabled(false);
                Actions.FILTER_ACTION.setEnabled(false);
                Actions.DISABLE_EXTRACT_ACTION.setEnabled(false);
                Actions.WRAPPER_UPDATE_ACTION.setEnabled(false);
                queryView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                wrapperChoiceView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                setStatusToSearching();
                break;
            }
            case AWAITING_SEARCH_RESULTS: {
                Actions.SEARCH_ACTION.setEnabled(false);
                Actions.CLEAR_QUERY_ACTION.setEnabled(false);
                Actions.SORT_BY_ACTION.setEnabled(true);
                Actions.SORT_ORDER_ACTION.setEnabled(true);
                Actions.PROJECTION_ACTION.setEnabled(true);
                Actions.FILTER_ACTION.setEnabled(true);
                Actions.DISABLE_EXTRACT_ACTION.setEnabled(false);
                Actions.WRAPPER_UPDATE_ACTION.setEnabled(false);
                queryView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                wrapperChoiceView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                break;
            }
            case SEARCH_FINISHED: {
                Actions.SEARCH_ACTION.setType(Type.SEARCH);
                Actions.SEARCH_ACTION.setEnabled(true);
                Actions.CLEAR_QUERY_ACTION.setEnabled(true);
                Actions.SORT_BY_ACTION.setEnabled(true);
                Actions.SORT_ORDER_ACTION.setEnabled(true);
                Actions.PROJECTION_ACTION.setEnabled(true);
                Actions.FILTER_ACTION.setEnabled(true);
                Actions.DISABLE_EXTRACT_ACTION.setEnabled(true);
                Actions.WRAPPER_UPDATE_ACTION.setEnabled(true);
                queryView.setCursor(Cursor.getDefaultCursor());
                wrapperChoiceView.setCursor(Cursor.getDefaultCursor());
                setStatusToSearchingFinished();
                break;
            }
        }
    }


    /**
     * Returns the state of the SearchTool.
     * 
     * @return the state
     */
    public SearchToolState getState() {
        return state;
    }


    public boolean isSeenDocId(String docId) {
        return seenIds.contains(docId);
    }


    public List<FrontendWrapperInfo> getWrapperListFromView() {
        return wrapperChoiceView.getCheckedWrapperInfo();
    }


    public String getSessionId() {
        return ToolController.getInstance().getSessionId();
    }


    /**
     * Clears the result list and the query.
     */
    public void clearSearch() {
        queryView.resetQuery();
        resultView.clearResultList();
    }


    /**
     * Saves the current search and clears the result list.
     */
    public void startNewSearch() {
        saveCurrentSearch();
        resultView.clearResultList();
    }


    private void saveCurrentSearch() {

        currentSearch = new SearchContext();

        currentSearch.query = queryView.getQueryFromView();
        currentSearch.resultConfig = getResultconfig();
        currentSearch.usedWrappers = getWrapperListFromView();
    }


    private ResultConfiguration getResultconfig() {
        final Sorting sorting = resultView.getSorting();
        final int endDocNumber = ResultConfiguration.INF_DOCS;
        final int startDocNumber = 0;
        return new ResultConfiguration(startDocNumber, endDocNumber, summaryFields, sorting);
    }


    /**
     * Returns the current search context.
     * 
     * @return the current search context
     */
    public SearchContext getCurrentSearch() {
        return currentSearch;
    }


    /**
     * Sends a request to the backend to get a recent wrapper list.
     */
    private void sendWrapperListRequest() {
        BackendEvent backendEvent = new BackendEvent(this);
        backendEvent.setContent(new AvailableWrappersAsk(Locale.getDefault()));
        Dispatcher.postEvent(backendEvent);
    }


    public void processWrapperRefresh() {
        sendWrapperListRequest();
    }


    public void processWrapperCategoryChange(String wrapperCategory) {
        wrapperChoiceView.update(wrapperCategory);
    }

}
