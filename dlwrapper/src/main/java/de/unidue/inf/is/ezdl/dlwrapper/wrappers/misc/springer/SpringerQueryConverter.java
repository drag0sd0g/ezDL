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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.springer;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractBottomUpQueryConverter;



/**
 * @author at
 */
public class SpringerQueryConverter extends AbstractBottomUpQueryConverter {

    private static final String AND = " AND ";
    private static final String OR = " OR ";


    public SpringerQueryConverter() {
        super();
    }


    private String fieldQuery(String field, String predicate, String value) {
        if ((field == null) || (predicate == null) || (value == null)) {
            return null;
        }

        return field + predicate + "(" + value + ")";
    }


    private String field(Field fieldCode) {
        String field = null;

        switch (fieldCode) {
            case AUTHOR: {
                field = "au";
                break;
            }
            case TEXT: {
                field = "ab";
                break;
            }
            case TITLE: {
                field = "ti";
                break;
            }
            case DOI: {
                field = "doi";
                break;
            }

            default: {
                field = null;
            }

        }

        return field;
    }


    private String fieldValue(Field fieldCode, String valueStr) {
        String value = null;

        switch (fieldCode) {
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
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    protected String fieldQueryEQ(boolean negated, Field field, String value) {
        final String fieldStr = field(field);
        final String valueStr = fieldValue(field, value);
        return fieldQuery(fieldStr, ":", valueStr);
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
