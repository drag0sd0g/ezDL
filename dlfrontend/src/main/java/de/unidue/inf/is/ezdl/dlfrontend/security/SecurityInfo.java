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

package de.unidue.inf.is.ezdl.dlfrontend.security;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



/**
 * Provides security information of the current session.
 * 
 * @author tbeckers
 */
public interface SecurityInfo {

    /**
     * Checks if a privilege was granted.
     * 
     * @param privilege
     *            A privilege
     * @return if the privilege was granted
     */
    public boolean check(Privilege privilege);


    /**
     * Returns all granted privileges.
     * 
     * @return all granted privileges
     */
    public Set<Privilege> privileges();

}