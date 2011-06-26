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

package de.unidue.inf.is.ezdl.dlservices.user.store;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



public interface UserStore {

    /**
     * Check if the database connection is working.
     * 
     * @return true, if the connection is okay, else false.
     */
    public boolean testConnection();


    /**
     * returns the User object corresponding to the given user login.
     * 
     * @param login
     * @return
     */
    public User getUser(String login);


    /**
     * Returns the user id for a session id.
     * 
     * @param sessionId
     * @return the corresponding user id
     */
    public int getUserIdForSessionId(String sessionId);


    /**
     * This method tests if the user has access to this system.
     * 
     * @param userName
     *            describes the user.
     * @param password
     *            is the secret access-identifier.
     */
    public boolean login(String userName, String password) throws Exception;


    /**
     * Saves the sessionID per user
     * 
     * @param sSession
     * @param sUser
     */
    public void saveSessionIdForUserLogin(String sSession, String sUser);


    /**
     * Checks if a session has a privilege.
     * 
     * @param privilege
     *            the privilege
     * @param sessionId
     *            the session id
     * @return if the session has the specified privilege.
     */
    public boolean checkPrivilege(Privilege privilege, String sessionId);


    /**
     * Returns all privileges of a user login
     * 
     * @param uid
     * @return all privileges of a user login
     */
    public Set<Privilege> getPrivilegesForUserLogin(String login);
}