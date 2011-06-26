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

package de.unidue.inf.is.ezdl.dlservices.backbone.dccp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import de.unidue.inf.is.ezdl.dlcore.Haltable;
import de.unidue.inf.is.ezdl.dlservices.backbone.dccp.DCCP.DCCPConfig;



public final class DCCPServer implements Haltable {

    private static Logger logger = Logger.getLogger(DCCPServer.class);

    private DCCP agent;
    private Server server;


    /**
     * Konstruktor.
     * 
     * @param dccp
     *            der DCCP-Agent
     * @param port
     *            Port auf dem der Server lauschen soll
     */
    public DCCPServer(DCCP dccp, int port) {
        agent = dccp;

        server = new Server(port);
        Handler handler = new AbstractHandler() {

            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                            HttpServletResponse response) throws IOException, ServletException {
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);

                String uri = request.getRequestURI();
                logger.debug("MTAServer received " + uri);

                String protocol = getEzdlProtocolFromUri(uri);
                DCCPConfig config = agent.getConfig(protocol);

                StringBuffer out = new StringBuffer();
                out.append(config.getMtaAddress());

                answer(request, response, out.toString());
            }
        };
        server.setHandler(handler);
        try {
            server.start();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    private static String getEzdlProtocolFromUri(String uriStr) {
        String ezdlProtocol = null;
        try {
            URI uri = new URI(uriStr);
            ezdlProtocol = uri.getRawPath();
            if (ezdlProtocol.startsWith("/")) {
                ezdlProtocol = ezdlProtocol.substring(1);
            }
        }
        catch (URISyntaxException e) {
        }
        return ezdlProtocol;
    }


    private void answer(HttpServletRequest request, HttpServletResponse response, String p) {
        response.setContentType("text/plain");
        try {
            response.getWriter().print(p);
            response.getWriter().flush();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void halt() {
        if (server != null) {
            try {
                server.stop();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public boolean isHalted() {
        return server.isStopped();
    }

}
