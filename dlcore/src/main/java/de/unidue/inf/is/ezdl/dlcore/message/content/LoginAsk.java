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

import de.unidue.inf.is.ezdl.dlcore.log.SessionType;



public class LoginAsk implements MessageContent {

    private static final long serialVersionUID = -2249875576726508581L;
    /**
     * The login of the user.
     */
    private String login;
    /**
     * The user's secret that is used for the authentication. This might be a
     * password or the result of e.g. XORing a known secret with a
     * challenge/nonce.
     */
    private String secret;
    /**
     * Defines the type of the session.
     */
    private SessionType sessionType;


    /**
     * Creates a LoginAsk object with the given login and secret, using a
     * session type of {@value SessionType#STANDARD}.
     * 
     * @param login
     *            the login to use
     * @param secret
     *            the password for authentication
     */
    public LoginAsk(String login, String secret) {
        this(login, secret, SessionType.STANDARD);
    }


    /**
     * Creates a LoginAsk object with the given login, secret and session type.
     * 
     * @param login
     *            the login to use
     * @param secret
     *            the passwort for authentication
     * @param sessionType
     *            the session type
     */
    public LoginAsk(String login, String secret, SessionType sessionType) {
        this.login = login;
        this.secret = secret;
        this.sessionType = sessionType;
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
     * @return the password
     */
    public String getSecret() {
        return secret;
    }


    /**
     * @param secret
     *            the secret to set
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }


    /**
     * Returns the session type.
     * 
     * @return the session type
     */
    public SessionType getSessionType() {
        return sessionType;
    }


    @Override
    public String toString() {
        return "{LoginAsk " + login + " with a" + ("".equals(secret) ? "n " : " non-") + "empty secret " + sessionType
                        + "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((secret == null) ? 0 : secret.hashCode());
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
        LoginAsk other = (LoginAsk) obj;
        if (secret == null) {
            if (other.secret != null) {
                return false;
            }
        }
        else if (!secret.equals(other.secret)) {
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
