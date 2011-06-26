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

package de.unidue.inf.is.ezdl.dlwrapper;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.cache.Cache;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;



/**
 * Defines the interface for Wrappers.
 */
public interface Wrapper extends Haltable {

    /**
     * Initializes the wrapper.
     * 
     * @param agent
     *            the agent that owns the wrapper.
     * @param cache
     *            the cache to use
     */
    void init(Agent agent, Cache cache);


    /**
     * Processes a request for documents for a given query.
     * 
     * @param documentQuery
     *            the query to process
     * @param usingCache
     *            true, if the cache is allowed to be used. Else false.
     * @return the result list, which is never null
     */
    StoredDocumentList askDocument(DocumentQuery documentQuery, boolean usingCache);


    /**
     * Processes a request for document details for the given documents.
     * <p>
     * There are two ways to implement this method:
     * <ul>
     * <li>The wrapper can retrieve the source information in the passed
     * documents, searching for the record that itself put there. Then it could
     * try to retrieve the details using the source information record.</li>
     * <li>The wrapper can use the already present pieces of meta-information to
     * generate a query for a known-item instantiation.</li>
     * </ul>
     * <p>
     * Since wrappers might get passed documents they have not found themselves
     * (maybe they haven't been asked in the first place because they weren't
     * running, weren't included in the original search query or weren't even
     * implemented) implementors are encouraged to implement all of the above
     * procedures.
     * 
     * @param incomplete
     *            the list of incomplete documents to retrieve details for.
     */
    void askDetails(StoredDocumentList incomplete);


    /**
     * Returns a WrapperInfo object that describes the wrapper.
     * 
     * @return a WrapperInfo object
     */
    WrapperInfo getWrapperInfo();


    SourceID getSourceID();


    /**
     * Returns the service name to register with.
     * <p>
     * Convention is to start wrapper services with "/wrapper/".
     * {@link ServiceNames#getServiceNameForDL(String)} can be used to generate
     * a service name for an implementor.
     * 
     * @return the service name for this wrapper.
     * @see ServiceNames#getServiceNameForDL(String)
     */
    String getServiceName();
}
