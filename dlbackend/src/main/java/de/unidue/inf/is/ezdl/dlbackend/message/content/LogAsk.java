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

package de.unidue.inf.is.ezdl.dlbackend.message.content;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



public class LogAsk implements MessageContent {

    private static final long serialVersionUID = -3435462022316025214L;


    @Override
    public String toString() {
        return "LogAsk";
    }


    @Override
    public boolean equals(Object o) {
        // Since there is no state in this object, two LogAsk objects are always
        // equal.
        return (o instanceof LogAsk);
    }


    @Override
    public int hashCode() {
        // Since there is no state in this object, the hashCode is constant.
        return 1;
    }
}
