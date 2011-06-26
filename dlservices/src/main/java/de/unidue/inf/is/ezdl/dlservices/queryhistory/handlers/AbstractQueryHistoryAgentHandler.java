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

package de.unidue.inf.is.ezdl.dlservices.queryhistory.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlservices.queryhistory.QueryHistoryAgent;
import de.unidue.inf.is.ezdl.dlservices.queryhistory.store.QueryHistoryStore;



abstract class AbstractQueryHistoryAgentHandler extends AbstractRequestHandler {

    /**
     * Reference to the {@link QueryHistoryStore};
     */
    private volatile QueryHistoryStore queryHistoryStore;


    /**
     * Returns a reference to the {@link QueryHistoryStore} in the agent.
     * 
     * @return the {@link QueryHistoryStore} reference
     */
    protected QueryHistoryStore getStore() {
        if (queryHistoryStore == null) {
            queryHistoryStore = ((QueryHistoryAgent) getAgent()).getQueryHistoryStore();
        }
        return queryHistoryStore;
    }
}
