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

import de.unidue.inf.is.ezdl.dlcore.data.wrappers.WrapperInfo;



/**
 * WrapperRecord is used to store information about wrappers.
 * 
 * @author mjordan
 */
public final class WrapperRecord extends AgentRecord {

    private static final long serialVersionUID = 3502542543402205905L;

    private WrapperInfo wrapperInfo;


    public WrapperRecord(String service, String agentName, WrapperInfo wrapperInfo) {
        super(service, agentName);
        this.wrapperInfo = wrapperInfo;
    }


    public WrapperInfo getInfo() {
        return wrapperInfo;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append('[');
        out.append(getService()).append(" - ").append(getName());
        out.append(" for ").append(wrapperInfo.getRemoteName());
        out.append(" (in ").append(wrapperInfo.getCategory()).append(')');
        out.append(']');
        return out.toString();
    }
}
