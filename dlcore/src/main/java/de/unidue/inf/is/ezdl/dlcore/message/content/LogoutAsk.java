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

public class LogoutAsk implements MessageContent {

    private static final long serialVersionUID = 4803126962580467893L;

    private String sessionID;


    public LogoutAsk(String sessionID) {
        this.sessionID = sessionID;
    }


    /**
     * @return the sessionID
     */
    public String getSessionID() {
        return sessionID;
    }


    /**
     * @param sessionID
     *            the sessionID to set
     */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sessionID == null) ? 0 : sessionID.hashCode());
        return result;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        LogoutAsk other = (LogoutAsk) obj;
        if (sessionID == null) {
            if (other.sessionID != null) {
                return false;
            }
        }
        else if (!sessionID.equals(other.sessionID)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("{LogoutAsk ").append(sessionID).append('}');
        return out.toString();
    }
}
