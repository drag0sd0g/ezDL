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

package de.unidue.inf.is.ezdl.dlbackend.coding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;



public class JBossSerializationStrategy implements BytesCodingStrategy {

    private Logger logger = Logger.getLogger(JBossSerializationStrategy.class);


    @Override
    public byte[] encode(Object object) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new JBossObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            return baos.toByteArray();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(oos);
        }
        return null;
    }


    @Override
    public Object decode(byte[] bytes) {
        ObjectInputStream ois = null;
        try {
            ois = new JBossObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(ois);
        }
        return null;
    }

}
