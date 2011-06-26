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

import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemList;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemsAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.ReferenceSystemsTell;
import de.unidue.inf.is.ezdl.dlservices.library.LibraryAgent;
import de.unidue.inf.is.ezdl.dlservices.library.manager.LibraryManager;



/** Returns the available online reference systems */
@Reusable
@StartedBy(ReferenceSystemsAsk.class)
public class RetrieveReferenceSystemsHandler extends AbstractLibraryAgentHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();
        if (content instanceof ReferenceSystemsAsk) {
            ReferenceSystemsAsk n = (ReferenceSystemsAsk) content;
            handleReferenceSystemsAsk(message, n);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void handleReferenceSystemsAsk(Message message, ReferenceSystemsAsk n) {
        // int uid = getAgent().userIdForSessionId(n.getSessionId());

        // Send available Reference systems
        ReferenceSystemList refList = new ReferenceSystemList();

        LibraryManager libraryManager = ((LibraryAgent) getAgent()).getLibraryManager();
        List<ReferenceSystem> refSystemsList = libraryManager.getAvailableReferenceSystems();

        for (ReferenceSystem rs : refSystemsList) {
            refList.add(rs);
        }

        // Send available ReferenceSystems
        send(message.tell(new ReferenceSystemsTell(n.getSessionId(), refList)));
    }

}
