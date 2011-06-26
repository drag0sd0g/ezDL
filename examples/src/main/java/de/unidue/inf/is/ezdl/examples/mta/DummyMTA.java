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
package de.unidue.inf.is.ezdl.examples.mta;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.CancelRequestNotify;
import de.unidue.inf.is.ezdl.dlcore.message.MTAMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.ClearQueryHistoryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentQueryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.QueryHistoryAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.RelatedTermsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.StoreQueryHistoryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.SynonymousTermsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.MessageTransformation;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http.GatedHttpMTA;
import de.unidue.inf.is.ezdl.examples.agent.DummyAsk;



public class DummyMTA extends GatedHttpMTA {

    /**
     * Sends the given message "as is" to the receiver in the backend.
     * 
     * @param message
     *            the message to send
     */
    @Override
    protected void forwardMessageFromClient(String connectionId, MTAMessage message) {
        final Message tMessage = transformMessage(connectionId, message);

        if (tMessage != null) {
            send(tMessage);
        }
        else {
            getLogger().info("Dropped unknown message content " + message);
        }
    }


    /**
     * Transforms an inbound client message into a message for some agent.
     * 
     * @param connectionId
     *            the ID of the connection over which the message arrived
     * @param message
     *            the message to transform
     * @return the transformed message
     */
    private Message transformMessage(String connectionId, MTAMessage message) {
        Message transformedMessage = new Message();

        final MessageTransformation mt = transform(message.getContent());

        if (mt != null) {
            transformedMessage.setRequestId(message.getRequestId());
            transformedMessage.setRequestInternalId(connectionId);
            transformedMessage.setFrom(agentName());
            transformedMessage.setTo(mt.getTo());
            transformedMessage.setContent(mt.getMessageContent());
        }
        else {
            transformedMessage = null;
        }
        return transformedMessage;
    }


    /**
     * Returns a strategy for transforming a message based on the content
     * passed.
     * 
     * @param content
     *            the content to base the transformation on
     * @return the transformation strategy
     */
    /* <snip> */
    private MessageTransformation transform(MessageContent content) {
        MessageTransformation mt = null;
        try {
            if (content instanceof AvailableWrappersAsk) {
                mt = new MessageTransformation(getDirectoryName(), content);
            }
            if (content instanceof DocumentQueryAsk) {
                mt = new MessageTransformation(findAgent("/service/search"), content);
            }
            if (content instanceof DocumentDetailsAsk) {
                mt = new MessageTransformation(findAgent("/service/repository"), content);
            }
            if (content instanceof DummyAsk) {
                mt = new MessageTransformation(findAgent("/service/dummy"), content);
            }
            /* </snip> */
            if (content instanceof QueryHistoryAsk) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof StoreQueryHistoryNotify) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof ClearQueryHistoryNotify) {
                mt = new MessageTransformation(findAgent("/service/queryhistory"), content);
            }
            if (content instanceof RelatedTermsAsk) {
                mt = new MessageTransformation(findAgent("/service/terminfo"), content);
            }
            if (content instanceof SynonymousTermsAsk) {
                mt = new MessageTransformation(findAgent("/service/terminfo"), content);
            }
            if (content instanceof UserLogNotify) {
                // Do not handle UserLogNotify
            }
            if (content instanceof CancelSearchNotify) {
                CancelSearchNotify cancel = (CancelSearchNotify) content;
                final String queryID = cancel.getQueryID();
                final boolean sendPartialResults = cancel.isSendPartialResults();
                content = new CancelRequestNotify(queryID, sendPartialResults);
                mt = new MessageTransformation(findAgent("/service/search"), content);
            }
        }
        catch (EzDLException e) {
            getLogger().error("Could not transform incoming message", e);
            mt = null;
        }
        return mt;
    }

}
