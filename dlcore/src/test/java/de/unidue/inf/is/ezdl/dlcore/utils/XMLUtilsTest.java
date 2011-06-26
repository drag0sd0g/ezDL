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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class XMLUtilsTest extends AbstractTestBase {

    private static final String XML_DEFECTIVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><defectivedocumentquery /></dllist>< attributename=\"free-text\" attributevalue=\"test\" predicate=\"=\" sign=\"true\"/></documentquery>";
    private static final String XML3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><documentquery maxduration=\"10000\" nocache=\"true\" requestid=\"FakeId\" returntype=\"\" sessionid=\"OFFLINE\"><dllist><dl path=\"/wrapper/dl/lacostir/amazon\"/></dllist><basecondition attributename=\"free-text\" attributevalue=\"test\" predicate=\"=\" sign=\"true\"/></documentquery>";
    private static final String XML1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><documentquery><dllist><dl path=\"/wrapper/dl/lacostir/amazon\"/></dllist><basecondition attributename=\"free-text\" attributevalue=\"test\" predicate=\"=\" sign=\"true\"/></documentquery>";
    private static final String XML_SHORT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><documentquery/>";
    private static final String XML_NOT_AT_ALL1 = "blabla";
    private static final String XML_NOT_AT_ALL2 = "no xml really";
    private static final String XML_WITH_MISSING_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE book SYSTEM \"abcdefgh.dtd\"><a>Test</a>";


    @Before
    public void init() {
    }


    @Test
    public void testGetRootElementName() {
        getRootElementName("documentquery", XML1);
        getRootElementName("documentquery", XML_SHORT);
        getRootElementName("documentquery", XML3);
        getRootElementName("defectivedocumentquery", XML_DEFECTIVE);
        getRootElementName("", XML_NOT_AT_ALL1);
        getRootElementName("", XML_NOT_AT_ALL2);
    }


    @Test
    public void testXmlDoc() {
        Assert.assertNotNull(XmlUtils.xmlDoc(XML1));
        String error = "The markup in the document following the root element must be well-formed.";
        getLogger().debug("The following statement will print " + error);
        Assert.assertNull(XmlUtils.xmlDoc(XML_DEFECTIVE));
    }


    @Test
    public void testXmlWithMissingDTD() {
        Assert.assertNotNull(XmlUtils.xmlDoc(XML_WITH_MISSING_DTD, true));
        Assert.assertNull(XmlUtils.xmlDoc(XML_WITH_MISSING_DTD, false));
    }


    private void getRootElementName(String rootName, String xml) {
        Assert.assertEquals(rootName, XmlUtils.getRootElementName(xml));
    }

}
