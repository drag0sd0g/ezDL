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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.med;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractBottomUpQueryConverter;



/**
 * Query converter for PubMed queries.
 * 
 * @author mjordan
 */
public class PubMedQueryConverter extends AbstractBottomUpQueryConverter {

    private static final String AND = " AND ";
    private static final String OR = " OR ";


    private String fieldQuery(String field, String value) {
        if ((field == null) || (value == null)) {
            return null;
        }
        return value + "[" + field + "]";
    }


    private String field(Field fieldCode) {
        String field = null;
        switch (fieldCode) {
            case AUTHOR: {
                field = "AUTHOR";
                break;
            }
            case TEXT: {
                field = "ALL";
                break;
            }
            case TITLE: {
                field = "TITL";
                break;
            }
            default: {
                field = null;
            }

        }

        return field;
    }


    private String fieldValue(Field field, String valueStr) {
        String value = null;

        switch (field) {
            case AUTHOR: {
                Person author = new Person(valueStr);
                value = author.getLastName();
                break;
            }
            default: {
                value = valueStr;
            }
        }
        return value;
    }


    @Override
    protected String convertProximity(QueryNodeProximity node) {
        return null;
    }


    @Override
    protected String fieldQueryEQ(boolean negated, Field field, String value) {
        final String fieldStr = field(field);
        final String valueStr = fieldValue(field, value);
        return fieldQuery(fieldStr, valueStr);
    }


    @Override
    protected String fieldQueryGT(boolean negated, Field field, String value) {
        return null;
    }


    @Override
    protected String fieldQueryGTE(boolean negated, Field field, String value) {
        return null;
    }


    @Override
    protected String fieldQueryLT(boolean negated, Field field, String value) {
        return null;
    }


    @Override
    protected String fieldQueryLTE(boolean negated, Field field, String value) {
        return null;
    }


    @Override
    protected String wildcardMulti() {
        return null;
    }


    @Override
    protected String wildcardSingle() {
        return null;
    }


    @Override
    protected String escapeToken(String token) {
        return token;
    }


    @Override
    protected String andOperator() {
        return AND;
    }


    @Override
    protected String orOperator() {
        return OR;
    }


    @Override
    protected String parenthesisOpen() {
        return "(";
    }


    @Override
    protected String parenthesisClosed() {
        return ")";
    }

}
