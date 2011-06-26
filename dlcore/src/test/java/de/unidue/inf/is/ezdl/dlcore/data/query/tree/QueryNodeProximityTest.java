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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * Tests {@link QueryNodeCompare}.
 * 
 * @author mjordan
 */
public class QueryNodeProximityTest extends AbstractTestBase {

    @Test
    public void testConstructor1() {
        final QueryNodeProximity comp = new QueryNodeProximity("term1", 2, "term2");
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("max dist", 2, comp.getMaxDistance());
        Assert.assertEquals("terms", "[term1, term2]", Arrays.asList(comp.getTerms()).toString());
    }


    @Test
    public void testConstructor2() {
        final QueryNodeProximity comp = new QueryNodeProximity("term2", 3, "term4");
        Assert.assertEquals("field code", Field.FIELDCODE_NONE, comp.getFieldCode());
        Assert.assertEquals("parent", null, comp.getParent());
        Assert.assertEquals("negated", false, comp.isNegated());
        Assert.assertEquals("max dist", 3, comp.getMaxDistance());
        Assert.assertEquals("terms", "[term2, term4]", Arrays.asList(comp.getTerms()).toString());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor3() {
        new QueryNodeProximity("term2", -1, "term4");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor4() {
        new QueryNodeProximity("term2", 0, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor5() {
        new QueryNodeProximity(null, 0, "term4");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor6() {
        new QueryNodeProximity(null, 0, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor7() {
        new QueryNodeProximity(null, -1, null);
    }


    @Test
    public void testSetTerms1() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setTerms(new String[] {
                        "xx", "yy"
        });
        Assert.assertEquals("[xx, yy]", Arrays.asList(comp.getTerms()).toString());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetTerms2a() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setTerms(new String[] {
                        null, "yy"
        });
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetTerms2b() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setTerms(new String[] {
                        "xx", null
        });
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetTerms2c() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setTerms(new String[] {
                        null, null
        });
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetTerms3a() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setTerms(null);
    }


    @Test
    public void testSetMaxDistance1() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setMaxDistance(2);
        Assert.assertEquals(2, comp.getMaxDistance());
    }


    @Test
    public void testSetMaxDistance2() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setMaxDistance(0);
        Assert.assertEquals(0, comp.getMaxDistance());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetMaxDistance3() {
        final QueryNodeProximity comp = new QueryNodeProximity("a", 1, "b");
        comp.setMaxDistance(-1);
        Assert.assertEquals(-1, comp.getMaxDistance());
    }

}
