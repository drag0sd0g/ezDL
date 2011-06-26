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
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/** Send all Documents saved in the Library */
public class LibraryTell implements MessageContent {

    private static final long serialVersionUID = -7466301413980547475L;

    private String sessionId;
    private List<Document> documents;


    public LibraryTell(String sessionId, List<Document> documents) {
        this.sessionId = sessionId;
        this.documents = documents;
    }


    @Override
    public String toString() {
        return LibraryTell.class.getSimpleName();
    }


    public String getSessionId() {
        return sessionId;
    }


    public List<Document> getDocuments() {
        return documents;
    }
}
