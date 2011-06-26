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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.citeseer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractBottomUpQueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;



/**
 * QueryConverter for the CiteseerWrapper.
 * 
 * @author tacke
 */
public class CiteseerQueryConverter extends AbstractBottomUpQueryConverter {

    private static final String AND = " AND ";
    private static final String OR = " OR ";


    public CiteseerQueryConverter() {
        super();
    }


    @Override
    public String convert(Query query) {
        String cond = super.convert(query);
        if (cond != null) {
            String yearClause = extractYearClause(query.getTree());
            if (yearClause != null) {
                cond = "(" + cond + ")" + yearClause;
            }
        }

        return cond;
    }


    /**
     * Extracts the year range from the query node and converts it to the format
     * that CiteSeer understands.
     * 
     * @param queryNode
     * @return String yearRange
     */
    String extractYearClause(QueryNode queryNode) {

        YearRangeConverter converter = new YearRangeConverter();
        YearRange range = converter.convertYearRange(queryNode);

        if (range.minYear == null && range.maxYear == null) {
            return null;
        }
        else if (range.minYear == null && range.maxYear != null) {
            return " AND year:[" + 1900 + " TO " + range.maxYear + "]";
        }
        else if (range.minYear != null && range.maxYear == null) {
            Date now = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(now);
            String nowYear = Integer.toString(cal.get(Calendar.YEAR));

            return " AND year:[" + range.minYear + " TO " + nowYear + "]";
        }
        else if (range.minYear.equals(range.maxYear)) {
            return " AND year:" + range.minYear;
        }
        else if (range.minYear < range.maxYear) {
            return " AND year:[" + range.minYear + " TO " + range.maxYear + "]";
        }
        else {
            return null;
        }
    }


    private String field(Field fieldCode) {
        String field = null;
        switch (fieldCode) {
            case AUTHOR: {
                field = "author";
                break;
            }
            case TEXT: {
                field = "abstract";
                break;
            }
            case TITLE: {
                field = "title";
                break;
            }
                // case DOI: {
                // field = "DOI";
                // break;
                // }
            default: {
                field = null;
            }

        }

        return field;
    }


    private String fieldValue(Field fieldCode, String tokens) {
        String value = null;

        switch (fieldCode) {
            case AUTHOR: {
                Person author = new Person(tokens);
                value = author.asString();
                break;
            }
            default: {
                value = tokens;
            }
        }
        return value;
    }


    @Override
    protected String convertProximity(QueryNodeProximity node) {
        StringBuilder out = new StringBuilder();
        out.append(fieldQueryEQ(false, node.getFieldCode(), node.getTerms()[0]));
        out.append(andOperator());
        out.append(fieldQueryEQ(false, node.getFieldCode(), node.getTerms()[1]));
        return out.toString();
    }


    @Override
    protected String fieldQueryEQ(boolean negated, Field field, String value) {
        final String fieldStr = field(field);
        final String valueStr = fieldValue(field, value);
        if ((fieldStr != null) && (valueStr != null)) {
            return fieldStr + ":(" + valueStr + ")";
        }
        return null;
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
