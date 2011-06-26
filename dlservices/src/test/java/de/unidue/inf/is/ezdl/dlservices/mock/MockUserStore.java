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

package de.unidue.inf.is.ezdl.dlservices.mock;

import java.util.Collections;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlservices.user.store.UserStore;



public class MockUserStore implements UserStore {

    public static final String WRONG_SECRET = "secret";
    public static final String RIGHT_SECRET = "joshua";
    public static final String LOGIN = "login";


    @Override
    public boolean login(String userName, String password) throws Exception {
        return RIGHT_SECRET.equals(password);
    }


    @Override
    public User getUser(String login) {
        return new User("lastname", "name", "login");
    }


    @Override
    public void saveSessionIdForUserLogin(String sSession, String sUser) {
    }


    @Override
    public boolean checkPrivilege(Privilege privilege, String sessionId) {
        return true;
    }


    @Override
    public Set<Privilege> getPrivilegesForUserLogin(String uid) {
        return Collections.emptySet();
    }


    @Override
    public boolean testConnection() {
        return true;
    }


    @Override
    public int getUserIdForSessionId(String sessionId) {
        return -1;
    }

}
