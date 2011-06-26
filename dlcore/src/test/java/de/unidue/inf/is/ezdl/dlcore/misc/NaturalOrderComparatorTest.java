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

package de.unidue.inf.is.ezdl.dlcore.misc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class NaturalOrderComparatorTest extends AbstractTestBase {

    private NaturalOrderComparator comparator;


    @Before
    public void init() {
        comparator = new NaturalOrderComparator();
    }


    @Test
    public void test() {
        String[] data = new String[] {
                        "a1", "a2", "a3", "a4", "a5", "a7a", "a2222"
        };
        List<String> list = Arrays.asList(Arrays.copyOf(data, data.length));
        Collections.shuffle(list);
        Collections.sort(list, comparator);
        Assert.assertEquals(Arrays.asList(data), list);
    }

}
