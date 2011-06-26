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

import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlbackend.message.content.SharedSecretMessageContent;
import de.unidue.inf.is.ezdl.dlcore.ErrorConstants;
import de.unidue.inf.is.ezdl.dlcore.message.content.ErrorNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.misc.EzDLException;



/**
 * Handler for the messages that only the directory agent is allowed to send.
 * <p>
 * This {@link RequestHandler} resolves the name of the directory automatically
 * and sends an error message in case of an error.
 * 
 * @author mj
 */
public abstract class AbstractDirAuthRequestHandler extends AbstractRequestHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;

        try {
            if (isMessageSenderAuthorized(message)) {
                handled = handleAuthorizedMessage(message);
            }
        }
        catch (EzDLException e) {
            send(message.tell(new ErrorNotify(ErrorConstants.SERVER_NOT_READY)));
        }

        return handled;
    }


    private boolean isMessageSenderAuthorized(Message message) throws EzDLException {
        final boolean nameOk = message.getFrom().equals(getAgent().getDirectoryName());
        if (!nameOk) {
            return false;
        }

        final MessageContent content = message.getContent();
        if (content instanceof SharedSecretMessageContent) {
            SharedSecretMessageContent ssc = (SharedSecretMessageContent) content;
            String sharedSecret = ssc.getSharedSecret();
            final boolean secretOk = sharedSecret.equals(getAgent().getSharedSecret());
            if (!secretOk) {
                return false;
            }
        }
        return true;
    }


    /**
     * Handles the authenticated message.
     * 
     * @param message
     *            the message that is from the directory
     * @return true, if the message has been handled. Else false
     */
    protected abstract boolean handleAuthorizedMessage(Message message);
}
