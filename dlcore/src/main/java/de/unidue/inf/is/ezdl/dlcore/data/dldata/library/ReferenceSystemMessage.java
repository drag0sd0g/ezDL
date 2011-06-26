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

package de.unidue.inf.is.ezdl.dlcore.data.dldata.library;

import java.util.HashMap;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.AbstractDLObject;



/** A message from the online reference system */
public class ReferenceSystemMessage extends AbstractDLObject {

    private static final long serialVersionUID = -3924408090697478545L;

    public static final int MENDELEY_USER_ACTION_REQUIRED = 1;
    public static final int ERROR_MESSAGE = 2;
    public static final int NO_REFERENCE_SYSTEM_INFO = 3;

    private int type;
    private String httpCode;
    private String message;
    private String url;

    // here can be saved any parameters which are needed. for example mendeley
    // verifier back from the display
    private Map<String, String> parameters;


    public ReferenceSystemMessage(int type, String httpCode, String message, String url) {
        this.type = type;
        this.httpCode = httpCode;
        this.message = message;
        this.url = url;
        parameters = new HashMap<String, String>();
    }


    public int getType() {
        return type;
    }


    public String getHttpCode() {
        return httpCode;
    }


    public String getMessage() {
        return message;
    }


    public String getUrl() {
        return url;
    }


    public Map<String, String> getParameters() {
        return parameters;
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof ReferenceSystemMessage) {
            return httpCode.equals(((ReferenceSystemMessage) obj).httpCode);
        }
        return false;
    }


    @Override
    public String asString() {
        return "ReferenceSystemMessage:" + httpCode + ";" + message + ";" + url;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((httpCode == null || message == null) ? 0 : (httpCode + message).hashCode());
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
        ReferenceSystemMessage other = (ReferenceSystemMessage) obj;

        if (type != other.type) {
            return false;
        }
        if (!httpCode.equals(other.httpCode)) {
            return false;
        }
        if (!message.equals(other.message)) {
            return false;
        }
        if (!url.equals(other.url)) {
            return false;
        }

        return true;
    }
}
