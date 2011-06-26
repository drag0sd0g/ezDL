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

import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.security.Privilege;



/**
 * The answer to a LoginAsk message, containing information about the logged in
 * user.
 * 
 * @author mjordan
 */
public class LoginTell implements MessageContent {

    private static final long serialVersionUID = -2148236397065161423L;

    private String login;
    private String firstName;
    private String lastName;
    private String sessionId;
    private Set<Privilege> privileges;
    /**
     * The time of the user's last login.
     */
    private long lastLoginTime;


    public LoginTell(String login, String firstName, String lastName, String sessionId, Set<Privilege> privileges) {
        super();
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sessionId = sessionId;
        this.privileges = privileges;
    }


    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }


    /**
     * @param login
     *            the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }


    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }


    /**
     * @param sessionId
     *            the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public Set<Privilege> getPrivileges() {
        return privileges;
    }


    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }


    /**
     * Returns the time stamp of the user's last login.
     * 
     * @return the last login time of the user
     */
    public long getLastLoginTime() {
        return lastLoginTime;
    }


    /**
     * Sets the time stamp of the user's last login.
     * 
     * @param timestamp
     *            the time stamp to set
     */
    public void setLastLoginTime(long timestamp) {
        this.lastLoginTime = timestamp;
    }


    @Override
    public String toString() {
        return "{LoginTell " + login + "/" + firstName + "/" + lastName + "/" + sessionId + "/" + privileges + "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((privileges == null) ? 0 : privileges.hashCode());
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
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
        LoginTell other = (LoginTell) obj;
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        }
        else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        }
        else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (privileges == null) {
            if (other.privileges != null) {
                return false;
            }
        }
        else if (!privileges.equals(other.privileges)) {
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
        if (login == null) {
            if (other.login != null) {
                return false;
            }
        }
        else if (!login.equals(other.login)) {
            return false;
        }
        return true;
    }

}
