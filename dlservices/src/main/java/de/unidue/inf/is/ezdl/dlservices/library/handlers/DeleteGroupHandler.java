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

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.DeleteGroupNotify;



/** Deletes a group */
@Reusable
@StartedBy(DeleteGroupNotify.class)
public class DeleteGroupHandler extends AbstractLibraryAgentHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();
        if (content instanceof DeleteGroupNotify) {
            DeleteGroupNotify n = (DeleteGroupNotify) content;
            handleDeleteGroup(message, n);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void handleDeleteGroup(Message message, DeleteGroupNotify n) {
        try {
            final int uid = getAgent().userIdForSessionId(n.getSessionId());
            getLibraryManager().removeGroup(n.getGroup(), uid, n.getReferenceSystem());
        }
        catch (Exception e) {
            handleException(e, message, n.getSessionId(), n.getReferenceSystem());
        }
    }

}
