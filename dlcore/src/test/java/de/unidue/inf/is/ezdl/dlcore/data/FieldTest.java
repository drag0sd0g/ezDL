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

package de.unidue.inf.is.ezdl.dlcore.data;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



public class FieldTest {

    /**
     * Assures that there are no duplicate IDs in the {@link Field} enum.
     */
    @Test
    public void testField() {
        Set<Integer> ids = new HashSet<Integer>();
        for (Field field : Field.values()) {
            Integer id = field.asInt();
            if (ids.contains(id)) {
                Assert.fail("Duplicate ID " + id);
            }
            else {
                ids.add(id);
            }
        }
    }
}
