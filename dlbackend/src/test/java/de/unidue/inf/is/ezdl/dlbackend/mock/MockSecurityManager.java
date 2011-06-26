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

package de.unidue.inf.is.ezdl.dlbackend.mock;

import de.unidue.inf.is.ezdl.dlbackend.security.SecurityManager;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlcore.security.SecurityException;



public class MockSecurityManager implements SecurityManager {

    @Override
    public void check(Privilege privilege, String sessionId) throws SecurityException {

    }


    @Override
    public boolean has(Privilege privilege, String sessionId) {
        return true;
    }


    @Override
    public void halt() {
    }


    @Override
    public boolean isHalted() {
        return true;
    }

}
