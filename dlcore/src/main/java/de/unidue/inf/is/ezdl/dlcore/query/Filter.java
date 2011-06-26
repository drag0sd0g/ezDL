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

import java.util.List;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.analysis.stemmers.Stemmer;
import de.unidue.inf.is.ezdl.dlcore.analysis.stemmers.StemmerFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNode;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool.NodeType;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeCompare.Predicate;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeProximity;
import de.unidue.inf.is.ezdl.dlcore.utils.PersonNameMatcher;



/**
 * This class Filter contains methods to filter metadataitems based on the a
 * query. Since not all wrapper give correct answer, we have to re-check the
 * answers.
 * 
 * @author mjordan
 **/
public class Filter {

    protected static final Logger logger = Logger.getLogger(Filter.class);

    /**
     * Conjunction
     */
    private QueryNodeBool conjunction = null;


    private class Walker extends DepthFirstQueryWalker<Boolean> {

        public Walker(QueryNodeVisitor<Boolean> visitor) {
            super(visitor);
        }

    }


    private class Visitor implements QueryNodeVisitor<Boolean> {

        private Document document;


        public Visitor(Document document) {
            this.document = document;
        }


        private Boolean convertSubresults(QueryNodeBool node, List<Boolean> subresults) {
            final boolean abortResult = (node.isOfType(NodeType.OR) ? true : false);
            boolean result = (node.isOfType(NodeType.AND));

            for (Boolean subresult : subresults) {
                if ((subresult != null) && (subresult.booleanValue() == abortResult)) {
                    return abortResult;
                }
            }
            return result;
        }


        @Override
        public Boolean visit(QueryNodeCompare compare) {
            return processField(document, compare);
        }


        @Override
        public Boolean visit(QueryNodeProximity proximity) {
            // TODO Auto-generated method stub
            return null;
        }


        @Override
        public Boolean visitAnd(QueryNodeBool node, List<Boolean> subresults) {
            return convertSubresults(node, subresults);
        }


        @Override
        public Boolean visitOr(QueryNodeBool node, List<Boolean> subresults) {
            return convertSubresults(node, subresults);
        }

    }


    /**
     * Empty constructor for testing.
     */
    Filter() {
    }


    /**
     * @param conjunction
     */
    public Filter(QueryNode node) {
        if (node instanceof QueryNodeBool) {
            this.conjunction = (QueryNodeBool) node;
        }
        else {
            final QueryNodeBool root = new QueryNodeBool();
            root.addChild(node);
            this.conjunction = root;
        }
    }


    /**
     * Initialize a filter with a document query
     * 
     * @param documentQuery
     *            The actual query
     **/
    public Filter(DocumentQuery documentQuery) {
        this(documentQuery.getQuery().getTree());
    }


    private boolean process(QueryNode conjunction, Document document) {
        final Walker walker = new Walker(new Visitor(document));
        return walker.calculate(conjunction);
    }


    private boolean processField(Document document, QueryNodeCompare base) {
        boolean test = true;

        switch (base.getFieldCode()) {
            case AUTHOR: {
                test = processAuthor(document, base);
                break;
            }
            case TITLE: {
                test = processTitle(document, base);
                break;
            }
            case TEXT: {
                test = processText(document, base);
                break;
            }
            case YEAR: {
                test = processYear(document, base);
                break;
            }
            default: {
                return true;
            }

        }
        return test;
    }


    private boolean processYear(Document document, QueryNodeCompare base) {
        if (base.hasWildcards()) {
            throw new IllegalArgumentException("Year clause must not have wildcards");
        }
        boolean test = true;
        String year;

        year = base.getTokensAsString();
        int iYear = 0;
        try {
            iYear = Integer.valueOf(year).intValue();
        }
        catch (NumberFormatException nfe) {
            return false;
        }

        switch (base.getPredicate()) {
            case EQ: {
                test = (document.getYear() == iYear);
                break;
            }
            case LTE: {
                test = (document.getYear() <= iYear);
                break;
            }
            case LT: {
                test = (document.getYear() < iYear);
                break;
            }
            case GTE: {
                test = (document.getYear() >= iYear);
                break;
            }
            case GT: {
                test = (document.getYear() > iYear);
                break;
            }
            default: {
                test = true;
            }
        }
        return test;
    }


    private boolean processText(Document document, QueryNodeCompare base) {
        return true;
    }


    private boolean processTitle(Document document, QueryNodeCompare base) {
        boolean test = true;
        if (base.getPredicate().equals(Predicate.EQ)) {
            String terms = base.getTokensAsString();
            test = testForTitlePhrase(document.getTitle(), terms);
            if (base.isNegated()) {
                test = !test;
            }
        }
        return test;
    }


    private boolean processAuthor(Document document, QueryNodeCompare base) {
        boolean test = true;
        if (base.getPredicate().equals(Predicate.EQ)) {
            final String author = base.getTokensAsString();
            if (!author.isEmpty()) {
                test = testForAuthorname(document.getAuthorList(), author);
                if (base.isNegated()) {
                    test = !test;
                }
            }

        }
        return test;
    }


    public boolean check(List<Document> dl) {
        for (Document document : dl) {
            if (!check(document)) {
                return false;
            }
        }
        return true;
    }


    public boolean check(Document document) {
        boolean result = false;
        if (conjunction == null) {
            logger.error("No query given");
            return false;
        }
        else if (conjunction != null) {
            result = process(conjunction, document);
        }
        return result;
    }


    /**
     * Here we test if the author name is contained in the document.
     * 
     * @param AuthorList
     *            authorList
     * @param String
     *            authorName
     **/
    boolean testForAuthorname(PersonList authorList, String authorName) {

        Person queryAuthor = new Person(authorName);

        for (Person author : authorList) {
            if (PersonNameMatcher.instance().specialEquals(author, queryAuthor)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Tests if the query term is contained in the title string of the document.
     * Here we can get more fuzzy if we do stemming of the terms.
     * 
     * @param String
     *            title
     * @param String
     *            queryTerms
     **/

    boolean testForTitleSingle(String title, String query) {
        if (title == null) {
            return false;
        }

        String titleTerms[] = splitAndStemTerms(title);
        String queryTerms[] = splitAndStemTerms(query);

        for (String queryTerm : queryTerms) {
            boolean matches = false;
            for (String titleTerm : titleTerms) {
                if (queryTerm.equals(titleTerm)) {
                    matches = true;
                }
            }
            if (!matches) {
                return false;
            }
        }
        return true;
    }


    boolean testForTitlePhrase(String title, String query) {
        if (title == null) {
            return false;
        }

        String titleTerms[] = splitAndStemTerms(title);
        String queryTerms[] = splitAndStemTerms(query);

        String stemmedTitle = concat(titleTerms);
        String stemmedQuery = concat(queryTerms);

        return stemmedTitle.matches(".*" + stemmedQuery + ".*");
    }


    private String concat(String[] queryTerms) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; (i < queryTerms.length); i++) {
            if (i != 0) {
                out.append(' ');
            }
            out.append(queryTerms[i]);
        }
        return out.toString();
    }


    private String[] splitAndStemTerms(String text) {
        String terms[] = text.split(" ");

        Stemmer stemmer = StemmerFactory.newAutoStemmer(text);

        for (int i = 0; i < terms.length; i++) {
            String term = terms[i].toLowerCase();
            terms[i] = stemmer.stem(term);
        }

        return terms;
    }

}
