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

package de.unidue.inf.is.ezdl.dlcore.analysis.stopwords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;



public class DefaultStopwordFilterTest extends AbstractTestBase {

    private DefaultStopwordFilter filter;


    @Before
    public void init() {
        filter = new DefaultStopwordFilter();
    }


    @Test
    public void testGermanFilter() {
        check(Arrays.asList("der", "System", "die", "aber", "Benutzer"), Arrays.asList("System", "Benutzer"),
                        Locale.GERMAN);
    }


    @Test
    public void testEnglishFilter() {
        check(Arrays.asList("the", "system", "a", "than", "user"), Arrays.asList("system", "user"), Locale.ENGLISH);
    }


    private void check(List<String> terms, List<String> termsWithoutStopwords, Locale locale) {
        Assert.assertEquals(new ArrayList<String>(termsWithoutStopwords),
                        filter.filter(new ArrayList<String>(terms), locale));
    }

}
