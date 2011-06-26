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

package de.unidue.inf.is.ezdl.dlcore.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class HashTest extends AbstractTestBase {

    private Hash hash;


    @Before
    public void init() {
        hash = new Hash();
    }


    @Test
    public void test() {
        check("abc", "a9993e364706816aba3e25717850c26c9cd0d89d");
        check("05484ddfgdhdh87sdgsdfgk_(//&744878454", "d98df388399ca4d7dad2e0c366db55cfc15133b6");
        check("üöäßßß??´´öê#éè", "718eb4fc2513eaeeee9d79551ad2c1216c8c886f");
        check("", "da39a3ee5e6b4b0d3255bfef95601890afd80709");
    }


    private void check(String string, String sha1String) {
        String sha1 = hash.sha1(string);
        Assert.assertEquals(sha1String, sha1);
    }

}
