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

package de.unidue.inf.is.ezdl.dlservices.repository;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlbackend.database.BoneCPConnectionProvider;
import de.unidue.inf.is.ezdl.dlbackend.database.ConnectionProvider;
import de.unidue.inf.is.ezdl.dlservices.repository.handlers.DocumentDetailsAskHandler;
import de.unidue.inf.is.ezdl.dlservices.repository.handlers.DocumentDetailsFillTellHandler;
import de.unidue.inf.is.ezdl.dlservices.repository.handlers.DocumentQueryTellHandler;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DetailRetrievalDecision;
import de.unidue.inf.is.ezdl.dlservices.repository.store.DocumentStore;
import de.unidue.inf.is.ezdl.dlservices.repository.store.SmartDetailRetrievalDecision;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DBRepository;
import de.unidue.inf.is.ezdl.dlservices.repository.store.repositories.DocumentRepository;



/**
 * The document agent is one of the main agents for ezDL. It provides several
 * functions like document search, citation and reference search etc.
 */
public class RepositoryAgent extends AbstractAgent {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/repository";
    /**
     * Stores the long-term stuff.
     */
    private DocumentStore store;


    @Override
    public void initOnline() {
        try {
            final ConnectionProvider provider = new BoneCPConnectionProvider(getProperties(), false);
            final DocumentRepository repository = new DBRepository(provider);
            final DetailRetrievalDecision decisionStrategy = new SmartDetailRetrievalDecision();
            store = new DocumentStore(this, repository, decisionStrategy);
            super.initOnline();
        }
        catch (IllegalArgumentException e) {
            getLogger().error("Connecting to the database failed.", e);
            halt();
        }
    }


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(DocumentDetailsAskHandler.class);
        handlers.add(DocumentQueryTellHandler.class);
        handlers.add(DocumentDetailsFillTellHandler.class);
        return handlers;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    public DocumentStore getStore() {
        return store;
    }


    @Override
    public void halt() {
        store.halt();
        super.halt();
    }
}
