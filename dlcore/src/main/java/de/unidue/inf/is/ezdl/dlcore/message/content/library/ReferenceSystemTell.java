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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/** Updates(Parameters) ReferenceSystem is send back to GUI */
public class ReferenceSystemTell implements MessageContent {

    private static final long serialVersionUID = 1202681903534289888L;

    private String sessionId;
    private ReferenceSystem referenceSystem;


    public ReferenceSystemTell(String sessionId, ReferenceSystem referenceSystem) {
        this.sessionId = sessionId;
        this.referenceSystem = referenceSystem;
    }


    @Override
    public String toString() {
        return ReferenceSystemTell.class.getSimpleName();
    }


    public String getSessionId() {
        return sessionId;
    }


    public ReferenceSystem getReferenceSystem() {
        return referenceSystem;
    }
}
