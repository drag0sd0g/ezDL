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

package de.unidue.inf.is.ezdl.dlservices.search.handlers.ranking;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Explanation.IDFExplanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.SolrQueryConverter;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Ranker for {@link Document}s based on Lucene.
 * 
 * @author tbeckers
 */
@SuppressWarnings("deprecation")
public class LuceneRanker implements Ranker {

    private static final int MIN_NORM_RSV = 0;
    private static final int MAX_NORM_RSV = 1;

    /**
     * Selects if the improved handling for phrase matches is to be used. If
     * false, a matching phrase query for "map reduce" counts two matches and
     * ranks such documents much higher than those that match "mapreduce". If
     * true, phrases like "map reduce" are counted as one match.
     */
    private static final boolean IMPROVED_PHRASE_RANKING = true;

    private Logger logger = Logger.getLogger(LuceneRanker.class);

    private QueryConverter queryConverter;


    /**
     * This is a Similarity implementation that returns 1.0 for idf values. The
     * rationale behind this is that the ranking is done only on documents that
     * match the query so IDF ranks documents with more common terms lower even
     * if they are super-relevant.
     * <p>
     * Example: Let A and B denote spelling variants in document titles and let
     * the query be "A OR B". Assume a result list that contains 3 documents
     * with the A variant and 27 with the B variant. Due to the IDF weight the B
     * documents will rank very low and the A documents very high, regardless of
     * any other rules such as term position.
     * <p>
     * The idea behind IDF is to reduce the weight of terms that are very common
     * <em> in the complete collection</em> (e.g. stop words) and thus cannot be
     * used to distinguish between documents. But in our case we are only
     * looking at a small collection and only at terms that are relevant to the
     * user so we don't want to rank them low.
     * 
     * @author mjordan
     */
    private class IdfLessSimilarity extends DefaultSimilarity {

        private static final long serialVersionUID = -8747591341351143145L;


        private class IdfLessExplanation extends IDFExplanation {

            private static final long serialVersionUID = 1L;


            @Override
            public float getIdf() {
                return 1.0f;
            }


            @Override
            public String explain() {
                return "";
            }
        }


        private IdfLessExplanation ex = new IdfLessExplanation();


        @Override
        public float idf(int docFreq, int numDocs) {
            return 1.0f;
        }


        @Override
        public IDFExplanation idfExplain(Collection<Term> terms, Searcher searcher) throws IOException {
            return ex;
        }

    }


    private Similarity similarity = new IdfLessSimilarity();


    /**
     * Constructor.
     */
    public LuceneRanker() {
        queryConverter = new SolrQueryConverter();

        if (IMPROVED_PHRASE_RANKING) {
            Similarity.setDefault(similarity);
        }
    }


    @Override
    public void rank(ResultDocumentList toRank, DocumentQuery documentQuery) {
        Directory directory = new RAMDirectory();

        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_31, new SimpleAnalyzer(
                            Version.LUCENE_31)));

            createIndex(toRank, indexWriter);
            calculateRSVs(directory, toRank, documentQuery);
            normalizeRSVs(toRank);
        }
        catch (CorruptIndexException e) {
            logger.error(e.getMessage(), e);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            if (indexWriter != null) {
                try {
                    indexWriter.close();
                }
                catch (CorruptIndexException e) {
                    logger.error(e.getMessage(), e);
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }


    /**
     * Normalizes RSV values
     * 
     * @param toRank
     */
    private void normalizeRSVs(ResultDocumentList toRank) {
        double maxRsv = 0;
        double minRsv = 0;

        for (ResultDocument document : toRank) {
            double rsv = document.getUnnormalizedRsv();
            if (rsv > maxRsv) {
                maxRsv = rsv;
            }
            if (rsv < minRsv) {
                minRsv = rsv;
            }
        }

        if (Math.abs(maxRsv - minRsv) <= 0.0000000001) {
            if (maxRsv > 1) {
                applySingleRsv(toRank, 1);
            }
            else if (maxRsv < 0) {
                applySingleRsv(toRank, 0);
            }
        }
        else {
            for (ResultDocument document : toRank) {
                double rsv = (document.getUnnormalizedRsv() - minRsv)
                                * ((MAX_NORM_RSV - MIN_NORM_RSV) / (maxRsv - minRsv)) + MIN_NORM_RSV;
                document.setRsv(rsv);
            }
        }
    }


    /**
     * Sets rsv value.
     * 
     * @param documentList
     *            the document list
     * @param rsv
     *            the rsv value
     */
    private void applySingleRsv(ResultDocumentList documentList, double rsv) {
        for (ResultDocument document : documentList) {
            document.setRsv(rsv);
        }
    }


    private void calculateRSVs(Directory directory, ResultDocumentList documentList, DocumentQuery documentQuery)
                    throws ParseException, CorruptIndexException, IOException {
        Query query = new QueryParser(Version.LUCENE_31,
                        de.unidue.inf.is.ezdl.dlcore.data.fields.Field.TEXT.toString(), new SimpleAnalyzer(
                                        Version.LUCENE_31)).parse(queryConverter.convert(documentQuery.getQuery()));
        IndexSearcher searcher = new IndexSearcher(directory, true);

        TopDocs topDocs = searcher.search(query, 1000);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            String oid = searcher.doc(scoreDoc.doc).get("oid");
            float score = scoreDoc.score;
            ResultDocument document = getDocumentByOid(documentList, oid);
            if (document != null) {
                document.setUnnormalizedRsv(score);
            }
        }
    }


    private ResultDocument getDocumentByOid(ResultDocumentList documentList, String oid) {
        for (ResultDocument document : documentList) {
            String oid2 = document.getOid();
            if (oid != null && oid2 != null && oid.equals(oid2)) {
                return document;
            }
        }
        return null;
    }


    private void createIndex(ResultDocumentList toRank, IndexWriter indexWriter) throws CorruptIndexException,
                    IOException {
        for (ResultDocument result : toRank) {
            Document document = result.getDocument();
            org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();

            StringBuilder sb = new StringBuilder();

            String oid = document.getOid();

            Field.Store store = Field.Store.NO;

            Field field;
            if (!StringUtils.isEmpty(oid)) {
                field = new Field("oid", oid, Field.Store.YES, Field.Index.NO);
                d.add(field);
                String title = document.getTitle();
                if (!StringUtils.isEmpty(title)) {
                    field = new Field(de.unidue.inf.is.ezdl.dlcore.data.fields.Field.TITLE.toString(), title, store,
                                    Field.Index.ANALYZED);
                    field.setOmitNorms(true);
                    field.setBoost(2.0f);
                    d.add(field);
                    sb.append(title);
                    sb.append(" ");
                }
                if (document instanceof TextDocument) {
                    String docAbstract = ((TextDocument) document).getAbstract();
                    if (!StringUtils.isEmpty(docAbstract)) {
                        field = new Field(de.unidue.inf.is.ezdl.dlcore.data.fields.Field.ABSTRACT.toString(),
                                        docAbstract, store, Field.Index.ANALYZED);
                        d.add(field);
                        sb.append(docAbstract);
                        sb.append(" ");
                    }
                }
                int year = document.getYear();
                if (year != 0) {
                    field = new Field(de.unidue.inf.is.ezdl.dlcore.data.fields.Field.YEAR.toString(),
                                    String.valueOf(year), store, Field.Index.NOT_ANALYZED);
                    d.add(field);
                    sb.append(" ");
                    sb.append(year);
                }
                PersonList authorList = document.getAuthorList();
                if (authorList != null) {
                    field = new Field(de.unidue.inf.is.ezdl.dlcore.data.fields.Field.AUTHOR.toString(),
                                    authorList.toString(), store, Field.Index.ANALYZED);
                    d.add(field);
                    sb.append(authorList.toString());
                }
                field = new Field(de.unidue.inf.is.ezdl.dlcore.data.fields.Field.TEXT.toString(), sb.toString()
                                .toString(), store, Field.Index.ANALYZED);
                d.add(field);

                indexWriter.addDocument(d);
            }
        }
        indexWriter.commit();
    }
}
