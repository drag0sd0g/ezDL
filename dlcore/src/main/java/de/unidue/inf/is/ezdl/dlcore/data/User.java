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

// This class was written from : S. Frankmoelle & M. Baldschus
// Version 11.7.2001

/**
 * This is the FSObjectClass from which most objects are descended. This class
 * contains the following attributes: lastname, firstname, address, uid,
 * email, birthday, gender, username, password, groups, homefolder. For every
 * attribut exist appropriate get and set methods.
 */
package de.unidue.inf.is.ezdl.dlcore.data;

import java.io.Serializable;



public class User implements Serializable {

    private static final long serialVersionUID = 6280835039044773161L;

    /**
     * First name of the user.
     */
    private String firstName = "";
    /**
     * Last name of the user.
     */
    private String lastName = "";
    /**
     * User login of the user.
     */
    private String login = "visitor";
    /**
     * Password of the user.
     */
    private String pwd = "visitor";
    /**
     * Email address of the user.
     */
    private String eMail = "";
    /**
     * The time of the user's last login.
     */
    private long lastLoginTime;


    /**
     * Constructor.
     * <p>
     * Creates a visitor user with name and password "visitor", empty email
     * address and a last login time of null. Also the first and last name are
     * empty.
     */
    public User() {
    }


    /**
     * Creates a user with the given data.
     * <p>
     * The password is set to "visitor". All other fields are null.
     * 
     * @param lastname
     *            last name of the user
     * @param firstname
     *            first name of the user
     * @param login
     *            user login
     */
    public User(String lastname, String firstname, String login) {
        this.lastName = lastname;
        this.firstName = firstname;
        this.login = login;
    }


    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * @param firstname
     *            the firstname to set
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
     *            the time stamp to set in UNIX time
     */
    public void setLastLoginTime(long timestamp) {
        this.lastLoginTime = timestamp;
    }


    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }


    /**
     * @param pwd
     *            the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


    /**
     * @return the eMail
     */
    public String getEMail() {
        return eMail;
    }


    /**
     * @param eMail
     *            the eMail to set
     */
    public void setEMail(String eMail) {
        this.eMail = eMail;
    }


    @Override
    public String toString() {
        return lastName + ", " + firstName;
    }

}
