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

package de.unidue.inf.is.ezdl.dlservices.repository.store.repositories;

import java.util.Collection;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;



/**
 * Interface to make testing and changing repository implementations easier.
 * <p>
 * A DocumentRepository is an object that stores Document objects and is able to
 * retrieve them again as long as the user is able to recall its ID.
 * <p>
 * The object IDs used to identify the documents is defined by the client to
 * maintain consistency and to make sure that knowledge about what this ID is
 * and how it is generated is not shared between the {@link DocumentRepository}
 * and the client.
 * 
 * @author mjordan
 */
public interface DocumentRepository {

    /**
     * Retrieves document for a single object ID.
     * 
     * @param oid
     *            the object ID of the document
     * @return the document's document or null if none present
     */
    StoredDocument getDocument(String oid);


    /**
     * Retrieves documents for multiple object IDs.
     * 
     * @param oids
     *            the list of object ID's of the documents which might be empty
     *            but never null
     * @return the documents' document TODO: decide if the output has null
     *         values for non-existent documents
     */
    StoredDocumentList getDocuments(Collection<String> oids);


    /**
     * Puts a document into the repository. Merges records, if a document object
     * is already present for the given document.
     * 
     * @param oid
     *            the ID to store the document under. If the oid is null, the
     *            implementor does not process the document.
     * @param document
     *            the document to store for it
     */
    void addDocument(String oid, StoredDocument document);


    /**
     * Returns the number of documents stored in the repository.
     * 
     * @return the number of documents
     */
    int getRepositorySize();


    /**
     * Removes the document with the given ID from the repository.
     * 
     * @param oid
     *            the OID whose document to delete
     */
    void removeDocument(String oid);
}
