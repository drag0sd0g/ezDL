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
package de.unidue.inf.is.ezdl.dlcore.data.query.tree.transformation.flatten;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



public class FlattenerTest {

    @Test
    public void nodesAreEqualTestC1a() {
        final QueryNodeCompare a = new QueryNodeCompare(Field.TITLE, "a");
        final QueryNodeCompare b = new QueryNodeCompare(Field.TITLE, "a");
        final Flattener f = new Flattener(a);

        Assert.assertTrue("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualTestC1b() {
        final QueryNodeCompare a = new QueryNodeCompare(Field.TITLE, "a");
        final QueryNodeCompare b = new QueryNodeCompare(Field.TITLE, "b");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualTestC2b() {
        final QueryNodeCompare a = new QueryNodeCompare(Field.AUTHOR, "a");
        final QueryNodeCompare b = new QueryNodeCompare(Field.TITLE, "a");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualTestC3b() {
        final QueryNodeCompare a = new QueryNodeCompare(Field.AUTHOR, "a");
        a.setNegated(true);
        final QueryNodeCompare b = new QueryNodeCompare(Field.AUTHOR, "a");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP1a() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        final QueryNodeProximity b = new QueryNodeProximity("a", 2, "a");
        final Flattener f = new Flattener(a);

        Assert.assertTrue("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP1b() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        final QueryNodeProximity b = new QueryNodeProximity("a", 2, "b");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP2b() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        final QueryNodeProximity b = new QueryNodeProximity("b", 2, "a");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP3b() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        final QueryNodeProximity b = new QueryNodeProximity("a", 3, "a");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP4b() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        a.setNegated(true);
        final QueryNodeProximity b = new QueryNodeProximity("a", 2, "a");
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }


    @Test
    public void nodesAreEqualsTestP5b() {
        final QueryNodeProximity a = new QueryNodeProximity("a", 2, "a");
        a.setFieldCode(Field.AUTHOR);
        final QueryNodeProximity b = new QueryNodeProximity("a", 2, "a");
        b.setFieldCode(Field.TITLE);
        final Flattener f = new Flattener(a);

        Assert.assertFalse("", f.nodesAreEqual(a, b));
    }

}
