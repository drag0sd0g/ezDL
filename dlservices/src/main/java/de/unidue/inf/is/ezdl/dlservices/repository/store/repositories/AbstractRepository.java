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

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;



/**
 * Some glue code for repositories.
 * 
 * @author mj
 */
abstract class AbstractRepository implements DocumentRepository {

    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(AbstractRepository.class);


    @Override
    public StoredDocumentList getDocuments(Collection<String> oids) {
        StoredDocumentList list = new StoredDocumentList();
        for (String oid : oids) {
            StoredDocument d = getDocument(oid);
            if (d != null) {
                list.add(d);
            }
        }
        return list;
    }


    @Override
    public void addDocument(String oid, StoredDocument document) {
        if (oid == null) {
            logger.warn("Document has no OID given:" + document);
            return;
        }

        StoredDocument inStore = getDocument(oid);
        StoredDocument toStore = null;
        if (inStore != null) {
            inStore.merge(document);
            toStore = inStore;
        }
        else {
            toStore = document;
        }
        putAsIs(oid, toStore);
    }


    /**
     * Inserts a document object in the repository, using the given OID to
     * identify it. No further processing is done on the object. Especially no
     * merging.
     * 
     * @param oid
     *            the object ID
     * @param document
     *            the object to insert
     */
    protected abstract void putAsIs(String oid, StoredDocument document);


    /**
     * Returns the logger
     * 
     * @return the logger
     */
    protected Logger getLogger() {
        return logger;
    }
}
