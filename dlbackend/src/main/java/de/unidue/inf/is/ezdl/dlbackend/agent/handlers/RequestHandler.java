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

package de.unidue.inf.is.ezdl.dlbackend.agent.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.agent.MessageWaiter;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.data.agent.RequestHandlerInfo;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.Haltable;



/**
 * A RequestHandler is an object that knows how to handle a request. A request
 * is a logical context that an agent is supposed to handle. An example for a
 * request is a search. The user sends a message to the search agent, signaling
 * that she wants documents for a query. The request is now the sequence of
 * actions necessary to generate an answer to the message. The document agent
 * might want to send a message to acknowledge the new search and then send
 * search message itself to a collection of wrappers. All these actions comprise
 * the request. Another request would be to ask the document agent for its
 * AgentLog. This request would be answered by just sending the log data.
 * <p>
 * The actions needed to handle a specific request are encapsulated in a
 * specific RequestHandler object. The implementor of RequestHandler only
 * contains methods and data that is needed to handle one specific request (e.g.
 * "give me the AgentLog") and all methods and data needed for that request are
 * in only one specific implementor of RequestHandler. This way we obey the DRY
 * principle.
 * <p>
 * RequestHandler implementors have to be annotated by {@link StartedBy} because
 * this is the only mechanism to invoke a RequestHandler.
 * 
 * @see StartedBy
 */
public interface RequestHandler extends Runnable, Haltable {

    /**
     * This method is called from within the RequestHandlerStore to initialize
     * the RequestHandler.
     * <p>
     * <b>For reasons yet unknown, calls to {@link Agent#findAgent(String)} from
     * within this method block and time out.</b> This seems to be related to
     * {@link MessageWaiter#messageArrived(Message)} being synchronized.
     * 
     * @param requestId
     *            The id of the request this request handler should handle
     * @param agent
     *            the reference to the agent who owns this handler
     */
    void init(String requestId, Agent agent);


    /**
     * Adds a message to the queue. If msg is null, no action is performed.
     * 
     * @param msg
     *            a message or null
     */
    void addMessageToQueue(Message msg);


    /**
     * Returns the request ID that the request handler handles.
     * 
     * @return the request ID
     */
    String getRequestId();


    /**
     * Returns information about the RequestHandler.
     * 
     * @return the information object
     */
    RequestHandlerInfo getInfo();


    /**
     * Configures if the {@link RequestHandler} should send partial results to
     * the client, if complete results cannot be obtained.
     * 
     * @param sendPartialResults
     *            true, if partial results are accepted. Else false.
     */
    void setSendPartialResults(boolean sendPartialResults);

}