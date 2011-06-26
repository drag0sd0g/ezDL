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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.queryviews;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.Actions;



/**
 * Mediator class that takes care of changes that happen in some parts of the
 * SearchTool and propagates the results of these changes to other objects.
 * <p>
 * Currently the objects managed by FormMediator are the SearchTool, the
 * SearchAction and the QueryViews.
 * 
 * @author mj
 */
public class FormMediator {

    /**
     * Reference to the SearchTool.
     */
    private SearchTool searchTool;
    /**
     * List of references to QueryView objects that are updated by this
     * mediator.
     */
    private List<QueryView> queryViews;
    /**
     * Reference to the SearchAction that is connected to some buttons.
     */
    private Action searchAction;
    /**
     * The instance of this Singleton.
     */
    private static FormMediator instance;


    /**
     * Private constructor so only the Singleton can create an instance.
     */
    private FormMediator() {
        queryViews = new LinkedList<QueryView>();
        searchAction = Actions.SEARCH_ACTION;
    }


    /**
     * Returns a reference to the instance of this Singleton.
     * 
     * @return the instance
     */
    public static synchronized FormMediator instance() {
        if (instance == null) {
            instance = new FormMediator();
        }
        return instance;
    }


    /**
     * Add the SearchTool. The previous SearchTool is overwritten by this
     * action. Since there is only one SearchTool this mediator cares about,
     * this doesn't seem like a problem.
     * 
     * @param searchTool
     *            reference to the SearchTool
     */
    public void add(SearchTool searchTool) {
        this.searchTool = searchTool;
    }


    /**
     * Add a QueryView. Each QueryView added here will be updated if there are
     * state changes somewhere that might affect the view.
     * 
     * @param view
     *            the QueryView to add.
     */
    public void add(QueryView view) {
        queryViews.add(view);
    }


    /**
     * Tells the mediator that the SearchTool has changed.
     * 
     * @param searchTool
     *            reference to the SearchTool
     */
    public void tellChanged(SearchTool searchTool) {
        updateAll();
    }


    /**
     * Tells the mediator that a QueryView has changed.
     * 
     * @param view
     *            reference to the changed QueryView
     */
    public void tellChanged(QueryView view) {
        updateQueryView(view);
    }


    /**
     * Updates all classes managed by this mediator.
     */
    private void updateAll() {
        updateAction();
        updateQueryViews();
    }


    /**
     * Updates the SearchAction.
     */
    private void updateAction() {
        switch (searchTool.getState()) {
            case SEARCH_IN_PROGRESS:
            case AWAITING_SEARCH_RESULTS: {
                searchAction.setEnabled(true);
                break;
            }
            case SEARCH_FINISHED: {
                searchAction.setEnabled(true);
                break;
            }
            default: {
                // don't change anything.
            }
        }
    }


    /**
     * Updates a specific QueryView.
     * 
     * @param view
     *            the view to be updated
     */
    private void updateQueryView(QueryView view) {

        switch (searchTool.getState()) {
            case SEARCH_IN_PROGRESS:
            case AWAITING_SEARCH_RESULTS: {
                // search running so query view should not be usable
                view.setUsable(UsabilityState.UNUSABLE);
                break;
            }
            case INIT:
            case SEARCH_FINISHED: {
                // search not even started or finished so usable depends on
                // error
                view.setUsable(view.isErroneous() ? UsabilityState.UNUSABLE_BECAUSE_OF_QUERY : UsabilityState.USABLE);
                break;
            }
            default: {
                // don't change anything.
            }
        }
    }


    /**
     * Updates all QueryView objects the mediator knows about.
     */
    private void updateQueryViews() {
        for (QueryView view : queryViews) {
            updateQueryView(view);
        }
    }
}
