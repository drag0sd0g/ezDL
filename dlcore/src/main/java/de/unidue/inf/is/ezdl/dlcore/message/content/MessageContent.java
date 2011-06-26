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

import java.io.Serializable;



/**
 * MessageContent is the interface that has to be implemented by objects that
 * are to be passed inside a Message object.
 * <p>
 * It is important that data inside of MessageContent objects is either
 * immutable or has been copied from the source location, because no guarantee
 * is made as to when the data is actually delivered. If only references are
 * stored, the data the references refer to might be changed between creation of
 * the MessageContent object and sending it, resulting in unexpected and
 * unintended effects.
 */

public interface MessageContent extends Serializable {

}
