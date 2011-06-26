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

package de.unidue.inf.is.ezdl.dlwrapper.handlers;

import java.util.concurrent.atomic.AtomicInteger;

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentQueryStoredTell;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.WrapperMapper;



/**
 * Handles requests that arrive at the WrapperMapper.
 */
@StartedBy(DocumentQueryAsk.class)
public class WrapperRequestHandler extends AbstractRequestHandler {

    /**
     * Constant to signify an uninitialized {@link #maxSessionCount}.
     */
    private static final int UNINITIALIZED = -1;
    /**
     * A stored empty list for speed-up.
     */
    private static final StoredDocumentList emptyList = new StoredDocumentList();
    /**
     * The number of concurrent calls to the wrapper. This is another safe-guard
     * to prevent the wrapper from hammering a DL - most prominently the one of
     * the ACM.
     */
    private static AtomicInteger sessionCount = new AtomicInteger(0);
    /**
     * The maximum number of sessions allowed.
     */
    private static int maxSessionCount = UNINITIALIZED;
    /**
     * The wrapper that runs the request.
     */
    private Wrapper wrapper;


    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent messageContent = message.getContent();

        getLogger().info("Handling " + messageContent);

        if (messageContent instanceof DocumentQueryAsk) {
            invokeWrapper(message, (DocumentQueryAsk) messageContent);
        }
        else {
            handled = false;
        }
        halt();
        return handled;
    }


    /**
     * Gets the wrapper and forwards the query to it.
     * 
     * @param message
     *            the message
     * @param dqAsk
     *            the content including the query
     */
    private void invokeWrapper(Message message, DocumentQueryAsk dqAsk) {
        if (wrapper != null) {
            getLogger().error("Wrapper already running for request " + getRequestId());
            return;
        }

        StoredDocumentList documentList = emptyList;

        getLogger().debug("Current Sessions: " + sessionCount.get());

        if (sessionCount.incrementAndGet() <= getMaxSessionCount()) {
            WrapperMapper mapper = (WrapperMapper) getAgent();
            wrapper = mapper.getNewWrapperInstance();
            DocumentQuery query = dqAsk.getQuery();
            boolean fromCache = dqAsk.isUsingCache();
            documentList = wrapper.askDocument(query, fromCache);
        }
        else {
            getLogger().debug("Rejecting query due to session threshold: " + dqAsk.getQuery());
        }

        sessionCount.decrementAndGet();

        if (!isHalted()) {
            DocumentQueryStoredTell documentQueryTell = new DocumentQueryStoredTell(documentList);
            send(message.tell(documentQueryTell));
        }
    }


    private int getMaxSessionCount() {
        if (maxSessionCount == UNINITIALIZED) {
            WrapperMapper mapper = (WrapperMapper) getAgent();
            maxSessionCount = mapper.maxSessionCount();
        }
        return maxSessionCount;
    }


    /**
     * Shuts down the wrapper and then halts the request handler.
     */
    @Override
    public void halt() {
        wrapper.halt();
        super.halt();
    }

}
