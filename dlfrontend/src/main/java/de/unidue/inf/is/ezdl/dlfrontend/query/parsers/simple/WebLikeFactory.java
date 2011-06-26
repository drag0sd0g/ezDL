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

package de.unidue.inf.is.ezdl.dlfrontend.query.parsers.simple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DefaultQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlfrontend.helper.FieldRegistry;
import de.unidue.inf.is.ezdl.dlfrontend.query.AbstractQueryFactory;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;



/**
 * Factory that converts queries to and from the WebLike syntax.
 * <p>
 * In this syntax, AND binds weaker than OR to facilitate facetted queries like
 * "man OR woman OR person AND famous OR popular" without having to use
 * parentheses.
 * 
 * @author mjordan
 */
public class WebLikeFactory extends AbstractQueryFactory {

    protected static final QueryNodeBool.NodeType OR = QueryNodeBool.NodeType.OR;
    protected static final QueryNodeBool.NodeType AND = QueryNodeBool.NodeType.AND;

    /**
     * The string used to indicate a phrase.
     */
    private static final String PHRASE_DELIMITER = "\"";
    /**
     * Determines how ranges of years are indicated. "-" would mean "1900-2000".
     */
    private static final String YEAR_RANGE_INDICATOR = "-";
    /**
     * Maximum distance for NEAR parameters that cannot be parsed.
     */
    private static final int DEFAULT_MAX_DIST = 5;

    /**
     * The single-character wildcard used in the grammar.
     */
    private static final String WILDCARD_SINGLE = "$";
    /**
     * The multi-character wildcard used in the grammar.
     */
    private static final String WILDCARD_MULTI = "#";


    /**
     * Creates a factory with the given field registry and default field code.
     * 
     * @param registry
     *            the registry to use
     * @param defaultFieldCode
     *            the default field code
     */
    public WebLikeFactory(FieldRegistry registry, Field defaultFieldCode) {
        super(registry, defaultFieldCode);
    }


    /**
     * Creates a factory with the given field registry and a default field code
     * of {@link Field#FIELDCODE_NONE}.
     * 
     * @param registry
     *            the registry to use
     */
    public WebLikeFactory(FieldRegistry registry) {
        super(registry, Field.FIELDCODE_NONE);
    }


    /**
     * Serializes a query tree recursively .
     */
    @Override
    public final String getTextForQueryNode(QueryNode node) {
        return getTextForQueryNode(node, getDefaultFieldCode());
    }


    /**
     * Serializes a query tree recursively .
     */
    @Override
    public final String getTextForQueryNode(QueryNode node, Field defaultFieldCode) {
        StringBuffer result = new StringBuffer();
        if (node == null) {
            return "";
        }
        try {
            if (node.isNegated()) {
                result.append("NOT ");
            }
            if (node instanceof QueryNodeCompare) {
                QueryNodeCompare cnode = ((QueryNodeCompare) node);
                String compare = getCompareString(defaultFieldCode, cnode);
                result.append(compare);
            }
            else if (node instanceof QueryNodeBool) {
                final QueryNodeBool boolNode = (QueryNodeBool) node;
                StringBuffer localTerm = new StringBuffer();
                QueryNodeBool up = node.getParent();
                for (QueryNode child : boolNode.children()) {
                    if (boolNode.getIndexOfChild(child) != 0) {
                        localTerm.append(' ').append(getBoolString(boolNode)).append(' ');
                    }

                    String childTerm = getTextForQueryNode(child, defaultFieldCode);

                    boolean subtreeHasToBeInParens = (child.getFieldCode() == Field.FIELDCODE_MIXED);
                    boolean subTreeNotAlreadyInParens = childTerm.charAt(0) != '(';
                    if (subtreeHasToBeInParens && subTreeNotAlreadyInParens) {
                        childTerm = "(" + childTerm + ")";
                    }

                    localTerm.append(childTerm);

                }

                localTerm = doStuff(up, boolNode, localTerm);
                result.append(localTerm);
            }
            else if (node instanceof QueryNodeProximity) {
                final Field nodeField = node.getFieldCode();
                final boolean explicitField = !nodeField.equals(defaultFieldCode);
                if (explicitField) {
                    final String field = getRegistry().getParserWord(nodeField);
                    result.append(field).append("={");
                }
                final QueryNodeProximity pNode = (QueryNodeProximity) node;
                result.append(pNode.getTerms()[0]);
                result.append(" /");
                result.append(pNode.getMaxDistance());
                result.append(' ');
                result.append(pNode.getTerms()[1]);
                if (explicitField) {
                    result.append('}');
                }
            }
        }
        catch (NoSuchFieldCodeException exception) {
            System.err.println("Error in MesgController.genOutput()");
            System.out.println(exception.getMessage());
            System.out.println("Alternative field code: " + exception.getAlternativeField());
        }
        return result.toString();
    }


    protected StringBuffer doStuff(QueryNodeBool up, final QueryNodeBool boolNode, StringBuffer localTerm) {
        return localTerm;
    }


    private String getBoolString(QueryNodeBool node) {
        // In this method the Strings "AND" and "OR" are generated explicitly
        // instead of using QueryNodeBool.NodeType.toString()
        // because this thing here decided how to render the nodes,
        // not the toString() method of some enum that is just supposed
        // to be used for convenience and debugging.
        switch (node.getType()) {
            case AND: {
                return "AND";
            }
            case OR: {
                return "OR";
            }
            default: {
                throw new IllegalArgumentException("QueryNodeBool has unknown/unhandled type");
            }
        }
    }


    /**
     * Returns a string snippet that can get parsed into the passed QueryNode.
     * 
     * @param defaultFieldCode
     *            the default field code (if the query node has this field code,
     *            no field prefix (e.g. Author) is generated.
     * @param cnode
     *            the QueryNode
     * @return the string representation that complied to the syntax this
     *         factory can parse.
     * @throws NoSuchFieldCodeException
     */
    private String getCompareString(Field defaultFieldCode, QueryNodeCompare cnode) throws NoSuchFieldCodeException {
        String value = getValueString(cnode);

        StringBuffer compare = new StringBuffer();
        Field fieldCode = cnode.getFieldCode();
        Predicate relOp = cnode.getPredicate();
        Predicate defRelOp = QueryNodeCompare.DEFAULT_PREDICATE;
        if ((fieldCode != defaultFieldCode)) {
            compare.append(getRegistry().getParserWord(fieldCode)).append(relOp);
        }
        else if (!defRelOp.equals(relOp) && !":".equals(relOp)) {
            compare.append(relOp);
        }
        compare.append(value);
        return compare.toString();
    }


    private String getValueString(QueryNodeCompare cnode) {
        StringBuilder builder = new StringBuilder();
        if (cnode.hasWildcards()) {
            for (String token : cnode.getTokens()) {
                String outToken = null;
                if (QueryNodeCompare.isTokenWildcardSingle(token)) {
                    outToken = WILDCARD_SINGLE;
                }
                else if (QueryNodeCompare.isTokenWildcardMulti(token)) {
                    outToken = WILDCARD_MULTI;
                }
                else {
                    outToken = token;
                }
                builder.append(outToken);
            }
        }
        else {
            final String tokens = cnode.getTokensAsString();
            final boolean valueIsAPhrase = (tokens.indexOf(' ') != -1);
            if (valueIsAPhrase) {
                builder.append(PHRASE_DELIMITER);
            }
            builder.append(tokens);
            if (valueIsAPhrase) {
                builder.append(PHRASE_DELIMITER);
            }
        }
        return builder.toString();
    }


    @Override
    public synchronized Query parse(String query) throws RecognitionException, NoSuchFieldCodeException {
        return parse(query, getDefaultFieldCode());
    }


    @Override
    public synchronized Query parse(String query, Field defaultFieldCode) throws RecognitionException,
                    NoSuchFieldCodeException {
        WebLikeLexer lex = new WebLikeLexer(new ANTLRStringStream(query));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        WebLikeParser parser = new WebLikeParser(tokens);

        WebLikeParser.query_return ast = parser.query();
        QueryNode node = getQueryNode(ast.getTree(), defaultFieldCode);
        Query returnVal = new DefaultQuery(node);
        return returnVal;
    }


    private QueryNode getQueryNode(Object ast, Field defaultFieldCode) throws RecognitionException,
                    NoSuchFieldCodeException {
        QueryNode node = null;
        if (ast instanceof CommonErrorNode) {
            throw new RecognitionException();
        }
        if (ast instanceof CommonTree) {
            CommonTree tree = (CommonTree) ast;

            Token token = tree.getToken();
            if (token != null) {
                final int tokenType = token.getType();
                switch (tokenType) {
                    case WebLikeParser.AND: {
                        QueryNodeBool boolNode = new QueryNodeBool(QueryNodeBool.NodeType.AND);
                        final List<QueryNode> subNodes = getChildNodes(defaultFieldCode, tree);
                        boolNode.setChildren(subNodes);
                        node = boolNode;
                        break;
                    }
                    case WebLikeParser.OR: {
                        QueryNodeBool boolNode = new QueryNodeBool(QueryNodeBool.NodeType.OR);
                        final List<QueryNode> subNodes = getChildNodes(defaultFieldCode, tree);
                        boolNode.setChildren(subNodes);
                        node = boolNode;
                        break;
                    }
                    case WebLikeParser.NOT: {
                        final List<QueryNode> subNodes = getChildNodes(defaultFieldCode, tree);
                        node = subNodes.get(0);
                        assert node != null;
                        node.setNegated(true);
                        break;
                    }
                    case WebLikeParser.EQL:
                    case WebLikeParser.GT:
                    case WebLikeParser.GTE:
                    case WebLikeParser.LT:
                    case WebLikeParser.LTE: {
                        Field field = Field.FIELDCODE_NONE;
                        List<String> tokens = null;
                        final List<QueryNode> subNodes = getChildNodes(defaultFieldCode, tree);
                        if (subNodes.size() == 1) {
                            // A node like "(> term)" was found (e.g. in year
                            // field ">2000")
                            field = defaultFieldCode;
                            tokens = ((QueryNodeCompare) subNodes.get(0)).getTokens();
                        }
                        else if (tree.getChildCount() == 2) {
                            // A node like "(> Field term)" was found
                            field = getFieldCodeFromSubNodeByPos(tree, 0);
                            tokens = ((QueryNodeCompare) subNodes.get(1)).getTokens();
                            assert tokens != null;
                        }

                        final String relStr = tree.getText();
                        final Predicate rel = calcPredicate(relStr);

                        if (Field.YEAR.equals(field)) {
                            if (tokens.size() == 1) {
                                node = handleYearRange(field, rel, tokens.get(0));
                            }
                        }
                        else {
                            node = new QueryNodeCompare(field, rel, null);
                            List<String> qnTokens = toQueryNodeTokens(tokens);
                            ((QueryNodeCompare) node).setTokens(qnTokens);
                        }

                        break;
                    }
                    case WebLikeParser.PHRASE: {
                        String phrase = "";
                        if (tree.getText() != null) {
                            phrase = tree.getText();
                        }
                        phrase = cleanPhrase(phrase);

                        node = new QueryNodeCompare(defaultFieldCode, phrase.trim());
                        break;
                    }
                    case WebLikeParser.PLACE_CHAR: {
                        if (tree.getChildCount() != 0) {
                            node = new QueryNodeCompare(defaultFieldCode, Predicate.EQ, null);
                            final QueryNodeCompare qnc = (QueryNodeCompare) node;
                            for (Object child : tree.getChildren()) {
                                if (child instanceof Tree) {
                                    final Tree childTree = (Tree) child;
                                    if (childTree.getType() == WebLikeParser.PLACE_CHAR) {
                                        qnc.addToken(QueryNodeCompare.WILDCARD_SINGLE);
                                    }
                                    else if (childTree.getType() == WebLikeParser.PLACE_CHARS) {
                                        qnc.addToken(QueryNodeCompare.WILDCARD_MULTIPLE);
                                    }
                                    else {
                                        String value = childTree.getText();
                                        assert value != null;
                                        qnc.addToken(value);
                                    }
                                }
                            }
                        }
                        else {
                            String value = tree.getText();
                            node = getCompareNode(defaultFieldCode, Predicate.EQ, value);
                            assert value != null;
                        }
                        break;
                    }
                    case WebLikeParser.NUMBER:
                    case WebLikeParser.TEXT: {
                        String value = tree.getText();
                        node = getCompareNode(defaultFieldCode, Predicate.EQ, value);
                        assert value != null;
                        break;
                    }
                    case WebLikeParser.BRACEOP: {
                        if (tree.getChildCount() == 2) {
                            Field field = getFieldCodeFromSubNodeByPos(tree, 0);
                            Object subQuery = tree.getChild(1);
                            node = getQueryNode(subQuery, field);
                        }
                        break;
                    }
                    case WebLikeParser.SLASH: {
                        if (tree.getChildCount() == 3) {
                            String term0 = tree.getChild(0).getText();
                            int maxDist;
                            try {
                                maxDist = Integer.parseInt(tree.getChild(1).getText());
                            }
                            catch (NumberFormatException e) {
                                // Should not happen because if this weren't a
                                // number the parser would have recognized it as
                                // a simple term instead of this NEAR operator.
                                maxDist = DEFAULT_MAX_DIST;
                            }
                            String term1 = tree.getChild(2).getText();
                            node = new QueryNodeProximity(term0, maxDist, term1);
                            node.setFieldCode(defaultFieldCode);
                        }
                        break;
                    }
                    default: {
                        // Never happens. And we don't care, if.
                    }
                }
            }
            else {
                final List<QueryNode> subNodes = getChildNodes(defaultFieldCode, tree);
                // Root node of the tree. According to our grammar, it has
                // two children, one of which is null, denoting the end of the
                // stream.
                if (subNodes.size() != 0) {
                    node = subNodes.get(0);
                }
                else {
                    node = null;
                }
            }
        }
        return node;
    }


    private Predicate calcPredicate(final String relStr) {
        if (":".equals(relStr)) {
            return Predicate.EQ;
        }
        return Predicate.fromString(relStr);
    }


    private List<String> toQueryNodeTokens(List<String> tokens) {
        List<String> qnTokens = new ArrayList<String>();
        for (String token : tokens) {
            if (WILDCARD_SINGLE.equals(token)) {
                qnTokens.add(QueryNodeCompare.WILDCARD_SINGLE);
            }
            else if (WILDCARD_MULTI.equals(token)) {
                qnTokens.add(QueryNodeCompare.WILDCARD_MULTIPLE);
            }
            else {
                qnTokens.add(token);
            }
        }
        return qnTokens;
    }


    private Field getFieldCodeFromSubNodeByPos(final CommonTree tree, int pos) throws NoSuchFieldCodeException {
        Field field = Field.FIELDCODE_NONE;
        List<?> childASTs = tree.getChildren();
        Object sub = childASTs.get(pos);
        if (sub instanceof CommonTree) {
            final String code = ((CommonTree) sub).getText();
            assert code != null;
            field = getRegistry().getFieldNumberByParserWord(code);
        }
        return field;
    }


    private List<QueryNode> getChildNodes(Field defaultFieldCode, CommonTree tree) throws RecognitionException,
                    NoSuchFieldCodeException {
        List<QueryNode> subNodes = new LinkedList<QueryNode>();
        List<?> childASTs = tree.getChildren();
        if (childASTs != null) {
            for (Object childAST : childASTs) {
                QueryNode childNode = getQueryNode(childAST, defaultFieldCode);
                if (childNode != null) {
                    subNodes.add(childNode);
                }
            }
        }
        return subNodes;
    }


    @Override
    public String cleanPhrase(String text) {
        int pdLength = PHRASE_DELIMITER.length();
        if (text.startsWith(PHRASE_DELIMITER)) {
            text = text.substring(pdLength);
        }
        if (text.endsWith(PHRASE_DELIMITER)) {
            text = text.substring(0, text.length() - pdLength);
        }
        return text;
    }


    private QueryNode getCompareNode(Field field, Predicate rel, String value) throws RecognitionException {
        if (Field.YEAR.equals(field)) {
            return handleYearRange(field, rel, value);
        }

        return new QueryNodeCompare(field, rel, value);
    }


    private QueryNode handleYearRange(Field field, Predicate rel, String value) throws RecognitionException {
        QueryNode node = null;
        if (value.endsWith(YEAR_RANGE_INDICATOR)) {
            String year = value.substring(0, value.length() - 1);
            node = new QueryNodeCompare(field, Predicate.GTE, year);
        }
        else if (value.startsWith(YEAR_RANGE_INDICATOR)) {
            /*
             * It is pretty unlikely that something like "-2000" gets here
             * because that would be parsed as "NOT 2000" already in the parser.
             * But what do I know?
             */
            String year = value.substring(1, value.length());
            node = new QueryNodeCompare(field, Predicate.LTE, year);
        }
        else if (value.contains(YEAR_RANGE_INDICATOR)) {
            String[] years = value.split(YEAR_RANGE_INDICATOR);
            if (years.length == 2) {
                QueryNodeCompare node1 = new QueryNodeCompare(field, Predicate.GTE, years[0]);
                QueryNodeCompare node2 = new QueryNodeCompare(field, Predicate.LTE, years[1]);
                QueryNodeBool bool = new QueryNodeBool();
                bool.setType(NodeType.AND);
                bool.addChild(node1);
                bool.addChild(node2);
                node = bool;
            }
            else {
                throw new RecognitionException();
            }

        }
        else {
            node = new QueryNodeCompare(field, rel, value);
        }
        return node;
    }
}
