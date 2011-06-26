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

package de.unidue.inf.is.ezdl.gframedl.tools.search;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.DocumentFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



/**
 * Tests if the {@link ResultItem} works correctly.
 * 
 * @author tbeckers
 */
public class ResultItemTest extends AbstractTestBase {

    private ResultItem resultItem;


    @Before
    public void init() {
        Document document = DocumentFactory.createDocument("Ein Titel-bla", 1234, "A B", "C D");
        ResultDocument d = new ResultDocument(document);
        d.addSource("Q");
        d.addSource("W");
        d.addSource("E");
        resultItem = new ResultItem(d, null);
    }


    /**
     * Test {@link ResultItem#toFilterString()}.
     */
    @Test
    public void testToFilterString() {
        Assert.assertEquals("Ein Titel-bla A B C D 1234 E Q W", resultItem.toFilterString());
    }

}
