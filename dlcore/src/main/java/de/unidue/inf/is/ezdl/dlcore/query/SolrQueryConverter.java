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

import java.util.Map;

import org.apache.lucene.queryParser.QueryParser;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;



/**
 * Query converter for Solr queries.
 * 
 * @author tbeckers
 */
public class SolrQueryConverter extends AbstractBottomUpQueryConverter {

    private static final String NOT = "NOT";
    private static final String AND = " AND ";
    private static final String OR = " OR ";

    private static final String WILDCARD_MULTI = "*";
    private static final String WILDCARD_SINGLE = "?";

    private static final String PARENTHESIS_CLOSED = ")";
    private static final String PARENTHESIS_OPEN = "(";

    /**
     * Mapping for fields.
     */
    private Map<Field, String> fieldMapping;


    /**
     * Constructor.
     */
    public SolrQueryConverter() {
        super();
    }


    /**
     * Sets the mapping from ezDL-internal fields ({@link Field}) to those in *
     * the target language.
     * <p>
     * This is only there for unit testing.
     * 
     * @param fieldMapping
     *            the field mapping
     */
    void setFieldMapping(Map<Field, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }


    /**
     * Constructor.
     * 
     * @param fieldMapping
     *            the mapping for fields.
     */
    public SolrQueryConverter(Map<Field, String> fieldMapping) {
        this.fieldMapping = fieldMapping;
    }


    private String compareQuery(Field field, Predicate predicate, String fieldValue, boolean negated) {
        String mappedField = field(field);
        String sign = sign(negated);
        String possiblyPhrasedFieldValue = phrase(fieldValue);

        switch (predicate) {
            case EQ: {
                return sign + mappedField + ":" + possiblyPhrasedFieldValue;
            }
            case GTE: {
                return sign + mappedField + ":[" + possiblyPhrasedFieldValue + " TO *]";
            }
            case LTE: {
                return sign + mappedField + ":[* TO " + possiblyPhrasedFieldValue + "]";
            }
            case GT: {
                return sign + mappedField + ":{" + possiblyPhrasedFieldValue + " TO *}";
            }
            case LT: {
                return sign + mappedField + ":{* TO " + possiblyPhrasedFieldValue + "}";
            }
            default: {
                throw new IllegalArgumentException("unexpected query syntax");
            }
        }
    }


    private static String sign(boolean negated) {
        return negated ? NOT + " " : "";
    }


    private static String phrase(String s) {
        if (s.matches(".*\\s+.*")) {
            return "\"" + s + "\"";
        }
        else {
            return s;
        }
    }


    private String field(Field field) {
        String mappedField = null;
        if (fieldMapping != null) {
            mappedField = fieldMapping.get(field);
        }
        if (mappedField == null) {
            mappedField = field.toString();
        }
        return mappedField;
    }


    private String proximityQuery(Field field, String value1, String value2, int maxDistance, boolean negated) {
        String possiblyPhrasedValue1 = phrase(value1);
        String possiblyPhrasedValue2 = phrase(value2);
        return sign(negated) + field(field) + ":\"" + possiblyPhrasedValue1 + " " + possiblyPhrasedValue2 + "\"~"
                        + maxDistance;
    }


    @Override
    protected String convertProximity(QueryNodeProximity node) {
        return proximityQuery(node.getFieldCode(), node.getTerms()[0], node.getTerms()[1], node.getMaxDistance(),
                        node.isNegated());
    }


    @Override
    protected String fieldQueryEQ(boolean negated, Field field, String value) {
        return compareQuery(field, Predicate.EQ, value, negated);
    }


    @Override
    protected String fieldQueryGT(boolean negated, Field field, String value) {
        return compareQuery(field, Predicate.GT, value, negated);
    }


    @Override
    protected String fieldQueryGTE(boolean negated, Field field, String value) {
        return compareQuery(field, Predicate.GTE, value, negated);
    }


    @Override
    protected String fieldQueryLT(boolean negated, Field field, String value) {
        return compareQuery(field, Predicate.LT, value, negated);
    }


    @Override
    protected String fieldQueryLTE(boolean negated, Field field, String value) {
        return compareQuery(field, Predicate.LTE, value, negated);
    }


    @Override
    protected String wildcardMulti() {
        return WILDCARD_MULTI;
    }


    @Override
    protected String wildcardSingle() {
        return WILDCARD_SINGLE;
    }


    @Override
    protected String escapeToken(String token) {
        return QueryParser.escape(token);
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
        return PARENTHESIS_OPEN;
    }


    @Override
    protected String parenthesisClosed() {
        return PARENTHESIS_CLOSED;
    }
}
