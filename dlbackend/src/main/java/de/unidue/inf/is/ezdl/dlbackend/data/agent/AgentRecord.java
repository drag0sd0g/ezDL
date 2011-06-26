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

package de.unidue.inf.is.ezdl.dlbackend.data.agent;

import java.io.Serializable;

import de.unidue.inf.is.ezdl.dlbackend.message.content.DeregisterAsk;



/**
 * AgentRecord is used to store information about agents.
 * 
 * @author mjordan
 */
public class AgentRecord implements Serializable {

    private static final long serialVersionUID = -7999593067992294051L;

    /**
     * The name of the agent.
     */
    private String name;
    /**
     * The service name in the directory. E.g. "/wrappers/acm"
     */
    private String service;
    /**
     * A secret that is only know to the remote agent and the directory agent.
     * Used for authenticating {@link DeregisterAsk} messages.
     */
    private String sharedSecret;


    /**
     * Constructor.
     * 
     * @param service
     *            the service of the agent
     * @param name
     *            the name of the agent
     */
    public AgentRecord(String service, String name) {
        this.name = name;
        this.service = service;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("[s: ");
        out.append(service).append(" - n: ").append(name);
        out.append("]");
        return out.toString();
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getService() {
        return service;
    }


    public void setService(String service) {
        this.service = service;
    }


    public String getSharedSecret() {
        return sharedSecret;
    }


    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

}