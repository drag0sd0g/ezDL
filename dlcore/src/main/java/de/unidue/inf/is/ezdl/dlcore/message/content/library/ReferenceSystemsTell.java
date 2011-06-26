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

package de.unidue.inf.is.ezdl.dlcore.message.content.library;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystemList;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public class ReferenceSystemsTell implements MessageContent {

    private static final long serialVersionUID = -9193005638707143613L;

    private String sessionId;
    private ReferenceSystemList referenceSystems;


    public ReferenceSystemsTell(String sessionId, ReferenceSystemList referenceSystems) {
        this.sessionId = sessionId;
        this.referenceSystems = referenceSystems;
    }


    public ReferenceSystemList getReferenceSystems() {
        return referenceSystems;
    }


    @Override
    public String toString() {
        return ReferenceSystemsTell.class.getSimpleName();
    }


    public String getSessionId() {
        return sessionId;
    }
}
