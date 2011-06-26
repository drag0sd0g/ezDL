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
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.AddToLibraryNotify;
import de.unidue.inf.is.ezdl.dlcore.message.content.library.LibraryTell;



/** Stores documents into the library */
@Reusable
@StartedBy(AddToLibraryNotify.class)
public class StoreLibraryHandler extends AbstractLibraryAgentHandler {

    @Override
    protected boolean work(Message message) {
        boolean handled = true;
        MessageContent content = message.getContent();
        if (content instanceof AddToLibraryNotify) {
            AddToLibraryNotify n = (AddToLibraryNotify) content;
            handleAddToLibrary(message, n);
        }
        else {
            handled = false;
        }

        return handled;
    }


    /** Adds a reference to the library */
    private void handleAddToLibrary(Message message, AddToLibraryNotify n) {
        final int uid = getAgent().userIdForSessionId(n.getSessionId());
        List<Document> list = new ArrayList<Document>();

        try {
            for (Document document : n.getDocuments()) {
                Document d = getLibraryManager().getDocument(document.getOid(), uid);
                String referencesystemId = (String) document.getFieldValue(Field.REFERENCESYSTEMID);

                if (d != null || (referencesystemId != null && referencesystemId.length() > 0)) {
                    // Document already in store (local store or online store or
                    // both). Update it
                    getLibraryManager().updateDocument(document, uid, n.getReferenceSystem());
                }
                else {
                    getLibraryManager().addDocument(document, uid, n.getReferenceSystem());
                }
                list.add(document);
            }

            // Send Updated documents to frontend
            send(message.tell(new LibraryTell(n.getSessionId(), list)));
        }
        catch (Exception e) {
            handleException(e, message, n.getSessionId(), n.getReferenceSystem());
        }
    }
}
