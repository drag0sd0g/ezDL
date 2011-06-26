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

package de.unidue.inf.is.ezdl.dlcore.message.content.library;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/** Adds a new Document to the Library */
public class AddToLibraryNotify implements MessageContent {

    private static final long serialVersionUID = 5286343820967222788L;

    private String sessionId;
    private List<Document> documents;
    private ReferenceSystem referenceSystem;


    public AddToLibraryNotify(String sessionId, List<Document> d, ReferenceSystem referenceSystem) {
        this.sessionId = sessionId;
        documents = d;
        this.referenceSystem = referenceSystem;

    }


    @Override
    public String toString() {
        return AddToLibraryNotify.class.getSimpleName();
    }


    public String getSessionId() {
        return sessionId;
    }


    public List<Document> getDocuments() {
        return documents;
    }


    public ReferenceSystem getReferenceSystem() {
        return referenceSystem;
    }
}
