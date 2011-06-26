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

package de.unidue.inf.is.ezdl.dlcore.message.content;

import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



public class PrivilegeTell implements MessageContent {

    private static final long serialVersionUID = 8262766901765412901L;

    private Privilege privilege;
    private String sessionId;
    private boolean permitted;


    public PrivilegeTell(Privilege privilege, String sessionId, boolean permitted) {
        super();
        this.privilege = privilege;
        this.sessionId = sessionId;
        this.permitted = permitted;
    }


    public Privilege getPrivilege() {
        return privilege;
    }


    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public boolean isPermitted() {
        return permitted;
    }


    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (permitted ? 1231 : 1237);
        result = prime * result + ((privilege == null) ? 0 : privilege.hashCode());
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PrivilegeTell other = (PrivilegeTell) obj;
        if (permitted != other.permitted) {
            return false;
        }
        if (privilege == null) {
            if (other.privilege != null) {
                return false;
            }
        }
        else if (!privilege.equals(other.privilege)) {
            return false;
        }
        if (sessionId == null) {
            if (other.sessionId != null) {
                return false;
            }
        }
        else if (!sessionId.equals(other.sessionId)) {
            return false;
        }
        return true;
    }

}
