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

/**
 * This is sent to ask if the receiver is alive. The receiver answers with
 * {@link AliveTell} if it is alive. Else it answers with an error message. Just
 * kidding. It doesn't answer at all, because it is dead. So the sender should
 * set a Timer to wait for the answer and consider the receiver dead if the
 * timer fires before the receiver answers.
 * 
 * @author mjordan
 */
public class AliveAsk implements MessageContent {

    private static final long serialVersionUID = -4915067257071654811L;


    @Override
    public String toString() {
        return "{AliveAsk}";
    }


    @Override
    public boolean equals(Object o) {
        return (o instanceof AliveAsk);
    }
}
