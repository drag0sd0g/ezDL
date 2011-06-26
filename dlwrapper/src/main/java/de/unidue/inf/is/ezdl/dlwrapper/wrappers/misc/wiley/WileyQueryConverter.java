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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.wiley;

import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;



/**
 * {@link QueryConverter} for the {@link WileyWrapper}.
 * 
 * @author mjordan
 */
public class WileyQueryConverter implements QueryConverter {

    @Override
    public String convert(Query query) {
        QueryNode queryNode = query.getTree();

        if (queryNode instanceof QueryNodeBool) {
            final QueryNodeBool clist = (QueryNodeBool) queryNode;
            final List<QueryNode> children = clist.children();
            if (children.size() == 1) {
                queryNode = children.get(0);
            }
        }

        String queryStr = null;
        Field commonField = queryNode.getFieldCode();
        if (commonField != Field.FIELDCODE_MIXED) {
            queryStr = generateSingleFieldQuery(0, commonField, queryNode);
        }
        else {
            List<Field> childFields = getChildFields(queryNode);
            for (Field childField : childFields) {
                if (childField == null) {
                    // at least one child has a mixed-field sub-tree
                    queryStr = getQueryApproximation(queryNode);
                    return queryStr;
                }
            }
            // all children are one-field sub-trees
            queryStr = getFieldedQuery(queryNode);
        }
        return queryStr;
    }


    String getFieldedQuery(QueryNode queryNode) {
        final Field field = queryNode.getFieldCode();
        if (queryNode instanceof QueryNodeBool) {
            final QueryNodeBool clist = (QueryNodeBool) queryNode;
            final List<QueryNode> children = clist.children();
            final String op = getFieldedOperator(clist.getType());
            final StringBuilder query = new StringBuilder();
            boolean firstPart = true;
            int pos = 0;
            for (QueryNode child : children) {
                Field childField = child.getFieldCode();
                if (childField != null) {
                    final String subQuery = generateSingleFieldQuery(pos, childField, child);
                    if (subQuery != null) {
                        if (!firstPart) {
                            query.append("&searchRowCriteria[").append(pos - 1).append("].booleanConnector=")
                                            .append(op).append('&');
                        }
                        query.append(subQuery);
                        firstPart = false;
                        pos++;
                    }
                }
            }
            return query.toString();
        }
        else {
            return generateSingleFieldQuery(0, field, queryNode);
        }
    }


    String getQueryApproximation(QueryNode queryNode) {
        throw new UnsupportedOperationException("Not implemented");
    }


    String generateSingleFieldQuery(int pos, Field field, QueryNode queryNode) {
        final String query = generateSingleFieldQuery(queryNode);
        final String fieldName = fieldName(field);
        if (fieldName != null) {
            final String fieldNameClause = "searchRowCriteria[" + pos + "].fieldName=" + fieldName;
            final String queryClause = "searchRowCriteria[" + pos + "].queryString=" + query;
            return queryClause + "&" + fieldNameClause;
        }
        else {
            return null;
        }
    }


    private String fieldName(Field field) {
        switch (field) {
            case AUTHOR:
                return "author";
            case TITLE:
                return "document-title";
            case TEXT:
                return "all-fields";
            default:
                return null;
        }
    }


    String generateSingleFieldQuery(QueryNode queryNode) {
        if (queryNode instanceof QueryNodeBool) {
            StringBuilder query = new StringBuilder();
            final QueryNodeBool clist = (QueryNodeBool) queryNode;
            final List<QueryNode> children = clist.children();
            final String op = getOperator(clist.getType());

            boolean firstChild = true;
            for (QueryNode child : children) {
                String subQuery = generateSingleFieldQuery(child);
                if (child instanceof QueryNodeBool) {
                    subQuery = "(" + subQuery + ")";
                }

                if (!firstChild) {
                    query.append(' ').append(op).append(' ');
                }
                query.append(subQuery);
                firstChild = false;
            }
            return query.toString();
        }
        else if (queryNode instanceof QueryNodeCompare) {
            QueryNodeCompare bc = (QueryNodeCompare) queryNode;
            return bc.getTokensAsString();
        }
        else if (queryNode instanceof QueryNodeProximity) {
            QueryNodeProximity bc = (QueryNodeProximity) queryNode;
            return bc.getTerms()[0] + getFieldedOperator(NodeType.OR) + bc.getTerms()[1];
        }
        else {
            throw new UnsupportedOperationException("QueryNode subclass " + queryNode.getClass()
                            + " is not implemented.");
        }
    }


    private String getOperator(NodeType type) {
        switch (type) {
            case OR: {
                return "OR";
            }
            case AND: {
                return "AND";
            }
            default: {
                return null;
            }
        }
    }


    private String getFieldedOperator(NodeType type) {
        switch (type) {
            case OR: {
                return "or";
            }
            case AND: {
                return "and";
            }
            default: {
                return null;
            }
        }
    }


    List<Field> getChildFields(QueryNode queryNode) {
        final LinkedList<Field> fields = new LinkedList<Field>();
        if (queryNode instanceof QueryNodeBool) {
            QueryNodeBool list = (QueryNodeBool) queryNode;
            final List<QueryNode> children = list.children();
            for (QueryNode child : children) {
                final Field childField = child.getFieldCode();
                fields.add(childField);
            }
        }
        else {
            final Field field = queryNode.getFieldCode();
            fields.add(field);
        }
        return fields;
    }

}
