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

package de.unidue.inf.is.ezdl.dlservices.library;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.AddGroupHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.DeleteGroupHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.DeleteLibraryHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.InitializeLibraryHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.RetrieveGroupsHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.RetrieveLibraryHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.RetrieveReferenceSystemsHandler;
import de.unidue.inf.is.ezdl.dlservices.library.handlers.StoreLibraryHandler;
import de.unidue.inf.is.ezdl.dlservices.library.manager.LibraryManager;
import de.unidue.inf.is.ezdl.dlservices.library.store.LibraryStore;
import de.unidue.inf.is.ezdl.dlservices.library.store.LibraryStoreFactory;



/** Implements the library agent */
public class LibraryAgent extends AbstractAgent {

    private static final String SERVICE_NAME = "/service/library";

    private LibraryManager libraryManager;


    @Override
    public void initOnline() {
        libraryManager = new LibraryManager(LibraryStoreFactory.DBStore, getProperties());
        if (libraryManager.getLibraryStore().testConnection()) {
            super.initOnline();
        }
        else {
            getLogger().error("Connecting to library store failed.");
            halt();
        }
    }


    /** Add handlers */
    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(RetrieveLibraryHandler.class);
        handlers.add(StoreLibraryHandler.class);
        handlers.add(DeleteLibraryHandler.class);
        handlers.add(RetrieveGroupsHandler.class);
        handlers.add(InitializeLibraryHandler.class);
        handlers.add(AddGroupHandler.class);
        handlers.add(DeleteGroupHandler.class);
        handlers.add(RetrieveReferenceSystemsHandler.class);

        return handlers;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    /** Returns the libraryStore */
    public LibraryStore getLibraryStore() {
        return libraryManager.getLibraryStore();
    }


    /** Returns the libraryManager */
    public LibraryManager getLibraryManager() {
        return libraryManager;
    }
}
