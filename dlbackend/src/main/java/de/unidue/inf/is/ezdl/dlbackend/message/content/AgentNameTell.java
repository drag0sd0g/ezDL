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

package de.unidue.inf.is.ezdl.dlbackend.message.content;

import de.unidue.inf.is.ezdl.dlbackend.data.agent.AgentRecord;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public class AgentNameTell implements MessageContent {

    private static final long serialVersionUID = 5284494802953929462L;

    private AgentRecord agentInfo;


    public AgentNameTell(AgentRecord agentInfo) {
        super();
        this.agentInfo = agentInfo;
    }


    /**
     * @return the agentInfo
     */
    public AgentRecord getAgentInfo() {
        return agentInfo;
    }


    /**
     * @param agentInfo
     *            the agentInfo to set
     */
    public void setAgentInfo(AgentRecord agentInfo) {
        this.agentInfo = agentInfo;
    }


    @Override
    public String toString() {
        return "{AgentNameTell " + agentInfo + "}";
    }
}
