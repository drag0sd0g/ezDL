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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class HTMLUtilsTest extends AbstractTestBase {

    @Test
    public void testGetWrappedHTML() {
        Assert.assertEquals("Some long and boring text, that <br>should be wrapped at 30 characters <br>per line.",
                        HtmlUtils.getWrappedHTML("Some long and boring text, "
                                        + "that should be wrapped at 30 characters per line.", 30));
    }


    @Test
    public void testGetTagcloudHTML() {
        Map<String, Integer> tags = new HashMap<String, Integer>();
        tags.put("information", 20);
        tags.put("systems", 19);
        tags.put("database", 2);
        tags.put("retrieval", 10);
        tags.put("vector", 5);
        tags.put("space", 4);
        tags.put("model", 12);

        Assert.assertEquals("<html><font size=\"6\">systems</font> <font size=\"7\">information</font>"
                        + " <font size=\"4\">model</font> <font size=\"1\">vector</font> "
                        + "<font size=\"3\">retrieval</font> <font size=\"1\">space</font>"
                        + " <font size=\"1\">database</font> </html>", HtmlUtils.getTagcloudHTML(tags));
    }

}
