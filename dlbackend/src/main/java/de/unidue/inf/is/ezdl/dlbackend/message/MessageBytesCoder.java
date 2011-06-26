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

package de.unidue.inf.is.ezdl.dlbackend.message;

import de.unidue.inf.is.ezdl.dlbackend.coding.BytesCoder;
import de.unidue.inf.is.ezdl.dlbackend.coding.JBossSerializationStrategy;



public final class MessageBytesCoder {

    private static MessageBytesCoder instance;

    private BytesCoder coder;


    private MessageBytesCoder() {
        coder = new BytesCoder(new JBossSerializationStrategy());
    }


    public byte[] encode(Message message) {
        return coder.encode(message);
    }


    public Message decode(byte[] data) {
        return (Message) coder.decode(data);
    }


    public synchronized static MessageBytesCoder getInstance() {
        if (instance == null) {
            instance = new MessageBytesCoder();
        }
        return instance;
    }

}
