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

package de.unidue.inf.is.ezdl.dlcore.utils;

import junit.framework.Assert;

import org.junit.Test;



public class PersonNameMatcherRuleBaseTest {

    private PersonNameMatcherRuleBase rules = new PersonNameMatcherRuleBase();


    @Test
    public void testEquiv1() {
        Assert.assertTrue(rules.equivalent("Keith van Rijsbergen", "C.J. van Rijsbergen"));
        Assert.assertTrue(rules.equivalent("Keith van Rijsbergen", "C. J. van Rijsbergen"));
    }


    @Test
    public void testEquiv2() {
        Assert.assertTrue(rules.equivalent("Nicholas Belkin", "Nick Belkin"));
    }


    @Test
    public void testNotEquiv1() {
        Assert.assertFalse(rules.equivalent("Keith van Rijsbergen", "C.J. van ijsbergen"));
        Assert.assertFalse(rules.equivalent("Keith van Rijsbergen", "Nick Belkin"));
    }


    @Test
    public void testNotEquiv2() {
        Assert.assertFalse(rules.equivalent("Clark Kent", "A.J. Simon"));
    }

}
