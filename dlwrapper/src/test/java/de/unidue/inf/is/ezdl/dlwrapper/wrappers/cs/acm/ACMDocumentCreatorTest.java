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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.acm;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;



public class ACMDocumentCreatorTest {

    ACMDocumentCreator creator = new ACMDocumentCreator();


    @Test
    public void testCleanUrl0() throws MalformedURLException {
        URL url = new URL("http://portal.acm.org");
        Assert.assertEquals("", "http://portal.acm.org", creator.cleanUrl(url).toString());
    }


    @Test
    public void testCleanUrl1() throws MalformedURLException {
        URL url = new URL("http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294&coll=GUIDE&dl=GUIDE&"//
                        + "CFID=86246997&CFTOKEN=90854599");
        Assert.assertEquals("", "http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294&coll=GUIDE&dl=GUIDE", creator.cleanUrl(url).toString());
    }


    @Test
    public void testCleanUrl2() throws MalformedURLException {
        URL url = new URL("http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294&coll=GUIDE&dl=GUIDE");
        Assert.assertEquals("", "http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294&coll=GUIDE&dl=GUIDE", creator.cleanUrl(url).toString());
    }


    @Test
    public void testCleanUrl3() throws MalformedURLException {
        URL url = new URL("http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294");
        Assert.assertEquals("", "http://portal.acm.org/citation.cfm?"//
                        + "id=567292.567294", creator.cleanUrl(url).toString());
    }

}
