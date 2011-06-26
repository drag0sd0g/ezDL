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

import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentDetailsFillAsk;
import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentDetailsFillTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlwrapper.Wrapper;
import de.unidue.inf.is.ezdl.dlwrapper.WrapperMapper;



/**
 * Handles requests that arrive at the WrapperMapper.
 */
@StartedBy(DocumentDetailsFillAsk.class)
public class DocumentDetailsFillHandler extends AbstractRequestHandler {

    /**
     * The wrapper that runs the request.
     */
    private Wrapper wrapper;


    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent messageContent = message.getContent();

        getLogger().info("Handling " + messageContent);

        if (messageContent instanceof DocumentDetailsFillAsk) {
            invokeWrapper(message, (DocumentDetailsFillAsk) messageContent);
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
     * @param ddfAsk
     *            the content including the query
     */
    private void invokeWrapper(Message message, DocumentDetailsFillAsk ddfAsk) {
        if (wrapper != null) {
            getLogger().error("Wrapper already running for request " + getRequestId());
            return;
        }
        WrapperMapper mapper = (WrapperMapper) getAgent();
        wrapper = mapper.getNewWrapperInstance();
        StoredDocumentList toComplete = ddfAsk.getDocuments();
        wrapper.askDetails(toComplete);
        DocumentDetailsFillTell tell = new DocumentDetailsFillTell(toComplete);
        if (!isHalted()) {
            send(message.tell(tell));
        }
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
