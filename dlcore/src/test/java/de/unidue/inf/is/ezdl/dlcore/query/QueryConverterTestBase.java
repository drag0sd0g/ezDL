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
package de.unidue.inf.is.ezdl.dlcore.query;

import org.junit.Assert;
import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.QueryFactory;



/**
 * This class should be the base class for tests of {@link QueryConverter}
 * implementations.
 * <p>
 * It makes sure that all features of the query representation are tested
 * against a converter.
 * 
 * @author mjordan
 */
public abstract class QueryConverterTestBase extends AbstractTestBase {

    protected QueryConverter converter;


    public class AllFeaturesConfig {

        public String queryStr;
        public Field field1;
        public Field field2;
        public int year;
    }


    protected AllFeaturesConfig getAllFeaturesQueryConfig() {
        AllFeaturesConfig config = new AllFeaturesConfig();
        config.queryStr = "(4=information OR NOT 4=retrieval) AND 31=2000 AND 62:[term1, term2]/3 AND 4=abc$de#";
        config.field1 = Field.TITLE;
        config.field2 = Field.ABSTRACT;
        config.year = 2000;
        return config;
    }


    @Test
    public void testConvertAllFeaturesQuery() {
        final AllFeaturesConfig config = getAllFeaturesQueryConfig();
        final Query q = QueryFactory.getAllFeaturesQuery(config.field1, config.field2, config.year).getQuery();
        final String queryStr = converter.convert(q);
        Assert.assertEquals("query: " + q, config.queryStr, queryStr);
    }

}
