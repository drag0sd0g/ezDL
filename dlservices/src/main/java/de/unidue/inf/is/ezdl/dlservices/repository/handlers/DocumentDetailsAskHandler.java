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

package de.unidue.inf.is.ezdl.dlservices.repository.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.DocumentListConverter;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlservices.repository.RepositoryAgent;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DocumentStore;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DocumentStoreResult;



/**
 * This RequestHandler handles requests for document details. The documents are
 * defined by object ID's ({@link DocumentDetailsAsk}). If half-filled document
 * objects are available the object ID's from them can be used to retrieve more
 * details.
 * 
 * @author mj
 */
@StartedBy(DocumentDetailsAsk.class)
public class DocumentDetailsAskHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;

        MessageContent content = message.getContent();

        if (content instanceof DocumentDetailsAsk) {
            handle(message, (DocumentDetailsAsk) content);
        }
        else {
            handled = false;
        }
        halt();
        return handled;
    }


    private void handle(Message message, DocumentDetailsAsk content) {
        DocumentStoreResult r = getStore().getDocuments(content.getOids(), true, 12000);
        endAndSendReceivedResults(r);
    }


    private void endAndSendReceivedResults(DocumentStoreResult r) {
        ResultDocumentList results = DocumentListConverter.toResultDocumentList(r.getStoredDocumentList());
        MessageContent content = new DocumentDetailsTell(results, r.isTimeout());

        sendReply(content);
    }


    /**
     * Sends message content to the party that sent the initial
     * DocumentQueryAsk.
     * 
     * @param content
     *            the content to send
     */
    protected void sendReply(MessageContent content) {
        getLogger().debug("Sending reply");
        send(getInitialMessage().tell(content));
        getLogger().debug("Sent reply");
    }


    DocumentStore getStore() {
        return ((RepositoryAgent) getAgent()).getStore();
    }
}
