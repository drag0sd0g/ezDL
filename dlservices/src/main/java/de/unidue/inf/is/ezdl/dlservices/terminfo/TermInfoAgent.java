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

package de.unidue.inf.is.ezdl.dlservices.terminfo;

import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;
import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlservices.terminfo.handlers.RelatedTermsHandler;
import de.unidue.inf.is.ezdl.dlservices.terminfo.handlers.SynonymousTermsHandler;



public class TermInfoAgent extends AbstractAgent {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/terminfo";


    @Override
    protected Set<Class<? extends RequestHandler>> setupRequestHandlers() {
        Set<Class<? extends RequestHandler>> handlers = super.setupRequestHandlers();
        handlers.add(RelatedTermsHandler.class);
        handlers.add(SynonymousTermsHandler.class);
        return handlers;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

}
