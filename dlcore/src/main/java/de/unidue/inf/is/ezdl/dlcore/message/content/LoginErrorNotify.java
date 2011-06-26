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

public class LoginErrorNotify implements MessageContent {

    private static final long serialVersionUID = -5935911951463536252L;

    private String error;


    public LoginErrorNotify(String error) {
        super();
        this.error = error;
    }


    public String getError() {
        return error;
    }


    public void setError(String error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "{LoginErrorNotify " + error + "}";
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LoginErrorNotify) {
            LoginErrorNotify l = (LoginErrorNotify) o;
            return l.getError().equals(getError());
        }
        return false;
    }
}
