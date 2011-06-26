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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



/**
 * Default implementation of {@link SecurityInfo}.
 * 
 * @author tbeckers
 */
public final class DefaultSecurityInfo implements SecurityInfo {

    private Set<Privilege> cache;


    public DefaultSecurityInfo() {
        this.cache = new HashSet<Privilege>();
    }


    public DefaultSecurityInfo(Collection<Privilege> privileges) {
        this();
        this.cache.addAll(privileges);
    }


    @Override
    public boolean check(Privilege privilege) {
        return cache.contains(privilege);
    }


    @Override
    public Set<Privilege> privileges() {
        return Collections.unmodifiableSet(cache);
    }

}
