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

import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.agent.Reusable;
import de.unidue.inf.is.ezdl.dlbackend.agent.StartedBy;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddGroupNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.GroupsTell;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.LibraryTell;



/** Add a group */
@Reusable
@StartedBy(AddGroupNotify.class)
public class AddGroupHandler extends AbstractLibraryAgentHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();
        if (content instanceof AddGroupNotify) {
            AddGroupNotify n = (AddGroupNotify) content;
            handleAddGroup(message, n);
        }
        else {
            handled = false;
        }

        return handled;
    }


    private void handleAddGroup(Message message, AddGroupNotify n) {
        try {
            final int uid = getAgent().userIdForSessionId(n.getSessionId());

            Group g = getLibraryManager().getGroup(n.getGroup(), uid);

            if (g != null) {
                // Group already in local store. Update it
                List<Document> updatedDocuments = getLibraryManager().updateGroup(n.getGroup(), uid,
                                n.getReferenceSystem(), n.getDocuments());

                // Send updated documents back to GUI
                if (updatedDocuments != null) {
                    send(message.tell(new LibraryTell(n.getSessionId(), updatedDocuments)));
                }

            }
            else {
                getLibraryManager().addGroup(n.getGroup(), uid, n.getReferenceSystem());
            }

            // Return updated group to GUI.
            List<Group> list = new ArrayList<Group>();
            list.add(n.getGroup());
            send(message.tell(new GroupsTell(n.getSessionId(), list)));

        }
        catch (Exception e) {
            handleException(e, message, n.getSessionId(), n.getReferenceSystem());
        }
    }

}
