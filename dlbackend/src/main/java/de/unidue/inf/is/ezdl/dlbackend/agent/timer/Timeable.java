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

package de.unidue.inf.is.ezdl.dlbackend.agent.timer;

import de.unidue.inf.is.ezdl.dlbackend.message.Message;



/**
 * Implementors of Timeable are able to receive wakup calls from {@link Timer}.
 */
public interface Timeable {

    /**
     * Receives string messages from the timer. This is used to signal which
     * timer fired.
     * 
     * @param message
     *            the string message to hand over
     */
    void wakeup(String message);


    /**
     * Receives Messages from the timer.
     * 
     * @param message
     *            the Message to hand over
     */
    void wakeup(Message message);
}
