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

package de.unidue.inf.is.ezdl.dlservices.user;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlservices.user.handlers.LoginHandler;
import de.unidue.inf.is.ezdl.dlservices.user.handlers.PrivilegeHandler;
import de.unidue.inf.is.ezdl.dlservices.user.handlers.UserIdForSessionIdHandler;
import de.unidue.inf.is.ezdl.dlservices.user.store.DBUserStore;
import de.unidue.inf.is.ezdl.dlservices.user.store.UserStore;



/**
 * The UserAgent represents the user in the ezdl backend.
 */
public class UserAgent extends AbstractAgent {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/user";
    /**
     * Where the user data is kept.
     */
    private UserStore userStore;


    @Override
    public void initOnline() {
        userStore = new DBUserStore(getProperties());
        if (userStore.testConnection()) {
            super.initOnline();
        }
        else {
            getLogger().error("Connecting to the database failed.");
            halt();
        }
    }


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(LoginHandler.class);
        handlers.add(PrivilegeHandler.class);
        handlers.add(UserIdForSessionIdHandler.class);
        return handlers;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    /**
     * Returns a reference to the {@link UserStore} object.
     * 
     * @return the {@link UserStore} reference
     */
    public UserStore getUserStore() {
        return userStore;
    }

}
