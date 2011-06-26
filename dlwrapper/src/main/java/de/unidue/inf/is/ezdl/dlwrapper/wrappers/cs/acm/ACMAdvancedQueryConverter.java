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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.AbstractBottomUpQueryConverter;



/**
 * Query converter for ACM advanced queries.
 * <p>
 * Year searches are apparently not supported for papers (only for
 * "publications", whatever that means).
 * <p>
 * Special characters do not have to be converted to URL encoding because that
 * is done in the HttpClientCrawler before the connection is established.
 * <p>
 * {@link QueryNodeProximity} is converted to an AND clause for the two terms
 * because ACM DL does not support proximity searches.
 * 
 * @author mjordan
 */
public class ACMAdvancedQueryConverter extends AbstractBottomUpQueryConverter {

    private static final String NOT = "not ";
    private static final String AND = "AND";
    private static final String OR = "OR";


    /**
     * Constructor.
     */
    public ACMAdvancedQueryConverter() {
        super();
    }


    private String field(Field fieldCode) {
        String field = null;
        switch (fieldCode) {
            case AUTHOR: {
                field = "Author";
                break;
            }
            case TEXT: {
                field = "Abstract";
                break;
            }
            case TITLE: {
                field = "Title";
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


    private String sign(boolean negated) {
        return negated ? NOT : "";
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
            return "(" + sign(negated) + fieldStr + ":" + valueStr + ")";
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
        return "";
    }


    @Override
    protected String wildcardSingle() {
        return "";
    }


    @Override
    protected String escapeToken(String token) {
        if (token.contains(" ")) {
            return "\"" + token + "\"";
        }
        return token;
    }


    @Override
    protected String andOperator() {
        return " " + AND + " ";
    }


    @Override
    protected String orOperator() {
        return " " + OR + " ";
    }


    @Override
    protected String parenthesisOpen() {
        return "(";
    }


    @Override
    protected String parenthesisClosed() {
        return ")";
    }


    @Override
    protected boolean useParenthesesForChild(QueryNodeBool node, NodeType childType) {
        return (childType != null);
    }
}
