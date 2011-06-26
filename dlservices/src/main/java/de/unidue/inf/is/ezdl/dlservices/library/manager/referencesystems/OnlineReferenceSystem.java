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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.Group;



/** Interface for an online reference system */
public abstract class OnlineReferenceSystem {

    /**
     * Returns the required authentication parameters as the keys in the Map.
     * these parameters have to be set and then give back. for example:
     * Bibsonomy needs the Username and a API Key. So the map contains two keys
     * called "username" and "api_key"
     */
    public abstract Map<String, String> getRequiredAuthParameters();


    /**
     * Returns other required parameters if needed. These parameters are set
     * automatically for example. Mendeley need the tokensecret and accesstoken.
     * They will be set automatically when available
     */
    public abstract Map<String, String> getOtherRequiredParameters();


    /*
     * Initializes the Wrapper
     * @param requiredParameters contains the required authentication parameters
     * which the wrapper need. the required parameters can be obtained by the
     * getRequriedAuthInf() method
     * @param props Property file
     * @param otherParameters Contains parameters which are needed bei the
     * referenceSystem like access_token in mendley Are set automatically
     */
    public abstract void initialize(Map<String, String> requiredAuthParameters, Map<String, String> otherParameters,
                    Properties props) throws Exception;


    /** Returns all references stored in the reference system */
    public abstract List<Document> getReferences() throws Exception;


    /** Remove a reference from the reference system */
    public abstract void removeReference(Document document) throws Exception;


    /** Update a reference from the reference system */
    public abstract void updateReference(Document document) throws Exception;


    /** Adds a new document in the user's library. */
    public abstract void addReference(Document document) throws Exception;


    /** returns all groups which are stored in the reference system */
    public abstract List<Group> getGroups() throws Exception;


    /** Adds a group to the reference system */
    public abstract void addGroup(Group group) throws Exception;


    /** Removes a group from the online reference system */
    public abstract void removeGroup(Group group) throws Exception;

}
