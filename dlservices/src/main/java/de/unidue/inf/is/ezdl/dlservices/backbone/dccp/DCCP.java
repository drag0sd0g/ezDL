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

import java.util.Properties;

import de.unidue.inf.is.ezdl.dlbackend.agent.AbstractAgent;



/**
 * Dynamic Client Configuration Protocol.
 * <p>
 * Sends configuration information to clients. E.g. MTA server to use. The
 * format of the configuration data is a properties file.
 * <p>
 * <h2>Protocol</h2>
 * <p>
 * The client sends an HTTP request to the DCCP asking for the resource
 * <code>config#[protocol]</code> where <code>[protocol]</code> is one of
 * <ul>
 * <li>gui</li>
 * </ul>
 * <p>
 * The server then answers with a properties file suitable to establish a
 * connection using said protocol. The properties file will contain the
 * following keys:
 * <ul>
 * <li>mta &ndash; the address of the MTA to connect to</li>
 * </ul>
 * <p>
 * Multiple DCCP servers for different protocols may be run. E.g. one for
 * external GUI clients that want to use an MTA and one for clients that want to
 * use a SOAP server.
 */
public final class DCCP extends AbstractAgent {

    /**
     * Service name of this agent.
     */
    private static final String SERVICE_NAME = "/service/dccp";

    private static final String PROPERTIES_KEY_DCCP_PORT = "dccp.port";

    private static final int DEFAULT_SERVER_PORT = 1337;

    private DCCPServer dccpServer;


    public static class DCCPConfig {

        private String mtaAddress;


        /**
         * @return the mtaAddress
         */
        public String getMtaAddress() {
            return mtaAddress;
        }


        /**
         * @param mtaAddress
         *            the mtaAddress to set
         */
        public void setMtaAddress(String mtaAddress) {
            this.mtaAddress = mtaAddress;
        }

    }


    public DCCP() {
        super();
    }


    @Override
    public void initOnline() {
        int port = DEFAULT_SERVER_PORT;

        try {
            final Properties props = getProperties();
            port = Integer.parseInt(props.getProperty(PROPERTIES_KEY_DCCP_PORT, String.valueOf(port)));
            super.initOnline();
        }
        catch (NumberFormatException exception) {
            getLogger().error("Could not parse config. Using default port " + port, exception);
        }

        dccpServer = new DCCPServer(this, port);
    }


    /**
     * Stop server
     */
    @Override
    public void halt() {
        if (dccpServer != null) {
            dccpServer.halt();
        }
        super.halt();
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    /**
     * Returns configuration information for the client.
     * 
     * @return the configuration object
     */
    public DCCPConfig getConfig(String protocol) {
        DCCPConfig config = new DCCPConfig();
        if ("gui".equals(protocol)) {
            config.setMtaAddress("http://localhost:8080");
        }
        return config;
    }
}
