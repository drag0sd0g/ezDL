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

package de.unidue.inf.is.ezdl.dlservices.library.handlers;

import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.AbstractRequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemMessage;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemMessageTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemTell;
import de.unidue.inf.is.ezdl.dlservices.library.LibraryAgent;
import de.unidue.inf.is.ezdl.dlservices.library.manager.LibraryManager;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.ReferenceSystemException;



public abstract class AbstractLibraryAgentHandler extends AbstractRequestHandler {

    /**
     * Reference to the {@link libraryManager};
     */
    private volatile LibraryManager libraryManager;


    protected LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            libraryManager = ((LibraryAgent) getAgent()).getLibraryManager();
        }
        return libraryManager;
    }


    /** Handles Exceptions */
    protected void handleException(Exception e, Message message, String sessionId, ReferenceSystem referenceSystem) {
        if (e instanceof ReferenceSystemException) {
            ReferenceSystemException re = (ReferenceSystemException) e;
            if (re.getOtherRequired() == ReferenceSystemException.MENDELEY_VERIFIER_REQUIRED) {
                ReferenceSystemMessage m = new ReferenceSystemMessage(
                                ReferenceSystemMessage.MENDELEY_USER_ACTION_REQUIRED, null, null, re.getUrl());
                send(message.tell(new ReferenceSystemMessageTell(sessionId, m)));

            }
            else {
                // Send other error message
                getLogger().error(e.getMessage(), e);
                ReferenceSystemMessage m = new ReferenceSystemMessage(ReferenceSystemMessage.ERROR_MESSAGE,
                                re.getHttpCode(), re.getMessage(), re.getUrl());
                send(message.tell(new ReferenceSystemMessageTell(sessionId, m)));
            }
            // Send updated referencesystem object back
            send(message.tell(new ReferenceSystemTell(sessionId, referenceSystem)));
        }
        else {
            getLogger().error(e.getMessage(), e);
        }
    }
}
