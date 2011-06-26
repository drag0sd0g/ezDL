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

package de.unidue.inf.is.ezdl.dlservices.library.store;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;



public interface LibraryStore {

    /** Returns the library as a List of Document Objects */
    public List<Document> getLibrary(int userId);


    /** Adds a Document to the Library */
    public void addDocument(Document d, int userId);


    /** Returns the Document with the given OID, if not found: return is null */
    public Document getDocument(String oid, int userId);


    /** Remove a Document from the Library */
    public void removeDocument(Document d, int userId);


    /** Updates the given document in the store. Compares the two ooids */
    public void updateDocument(Document d, int userId);


    /** Returns the groups of the user */
    public List<Group> getGroups(int userId);


    /** Get the Group with the given group id */
    public Group getGroup(String groupId, int userId);


    /** Add a group */
    public void addGroup(Group group, int userId);


    /** Update group */
    public void updateGroup(Group group, int userId);


    /** Removes a group */
    public void removeGroup(Group group, int userId);


    /** Checks if connection can be established */
    public boolean testConnection();
}
