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

package de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.http;

import java.io.IOException;

import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.AbstractGatedMTA;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.MessageHandler;
import de.unidue.inf.is.ezdl.dlservices.backbone.mta.gated.Server;



/**
 * A Message Transport Agent that uses a fake HTTP server as an interface to
 * communicate with user clients.
 * 
 * @author mjordan
 */
public class GatedHttpMTA extends AbstractGatedMTA implements MessageHandler {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/mta";

    /**
     * Property key for the port number of the fake HTTP server that deals with
     * client connections.
     */
    private static final String KEY_MTA_PORT = "http.port";
    /**
     * Property key for the port number of the fake HTTP server that deals with
     * client connections.
     */
    private static final String KEY_MTA_HOST = "http.host";


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    protected String getHostPropertyKey() {
        return KEY_MTA_PORT;
    }


    @Override
    protected String getPortPropertyKey() {
        return KEY_MTA_HOST;
    }


    @Override
    protected Server newServer(MessageHandler messageHandler, String host, int port, String pingMessage)
                    throws IOException {
        return new FakeHttpServer(this, host, port, pingMessage);
    }

}