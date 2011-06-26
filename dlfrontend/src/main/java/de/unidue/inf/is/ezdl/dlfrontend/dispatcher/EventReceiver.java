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

package de.unidue.inf.is.ezdl.dlfrontend.dispatcher;

import java.util.EventObject;



/**
 * The Interface EventReceiver must be implemented by every potential listener.
 * The generic "java.util.EventObject" may be downcast to the known subtype.
 * Return-type should indicate, whether the event was "successfully" handled...
 * for some sensible value of "successful"
 */
public interface EventReceiver {

    boolean handleEzEvent(EventObject ev);
}
