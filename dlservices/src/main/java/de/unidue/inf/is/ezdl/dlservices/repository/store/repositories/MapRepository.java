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

import java.util.HashMap;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * Dummy repository, used mainly for testing.
 * 
 * @author mj
 */
public class MapRepository extends AbstractRepository {

    /**
     * Where the data goes when you call {@link #addDocument(String, Document)}.
     */
    private Map<String, StoredDocument> repo = new HashMap<String, StoredDocument>();


    @Override
    public StoredDocument getDocument(String oid) {
        return repo.get(oid);
    }


    @Override
    protected void putAsIs(String oid, StoredDocument document) {
        repo.put(oid, document);
    }


    @Override
    public int getRepositorySize() {
        return repo.size();
    }


    @Override
    public void removeDocument(String oid) {
        repo.remove(oid);
    }
}
