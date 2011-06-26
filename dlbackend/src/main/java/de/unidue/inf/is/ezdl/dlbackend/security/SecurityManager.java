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

package de.unidue.inf.is.ezdl.dlbackend.security;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.security.SecurityException;



/**
 * A security manager checks if privileges are granted to user sessions.
 * 
 * @author tbeckers
 */
public interface SecurityManager extends Haltable {

    /**
     * Checks if a privilege is granted to a user session.
     * 
     * @param privilege
     *            The privilege
     * @param sessionId
     *            The id of the session
     * @throws SecurityException
     *             If the privilege is not graned to the user session
     */
    public void check(Privilege privilege, String sessionId) throws SecurityException;


    /**
     * Checks if a privilege is granted to a user session.
     * 
     * @param privilege
     * @param sessionId
     * @returnIf the privilege is not graned to the user session
     */
    public boolean has(Privilege privilege, String sessionId);

}
