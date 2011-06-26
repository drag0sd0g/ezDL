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

import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.ArticleType;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorListType;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorType;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorTypeSequence_type0;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.AuthorTypeSequence_type1;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.MedlineCitationType;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchPubmedServiceStub.PubmedArticleType;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.DocSumType;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.IdListType;
import gov.nih.nlm.ncbi.www.soap.eutils.EUtilsServiceStub.ItemType;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.commons.lang.NotImplementedException;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.OIDFactory;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.query.DocumentQuery;
import de.unidue.inf.is.ezdl.dlcore.data.query.Query;
import de.unidue.inf.is.ezdl.dlcore.data.query.tree.QueryNodeBool;
import de.unidue.inf.is.ezdl.dlcore.query.Filter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractWrapper;



/**
 * Wrapper for the SOAP interface to the Entrez API of PubMed.
 * <p>
 * PubMed is a medical and life sciences database.
 * <p>
 * PubMed has a special way to deal with authors. There are authors who are
 * natural persons and those who aren't (e.g. groups). These two types of
 * authors are handled differently in PubMed. This wrapper does not care about
 * group authors and just drops them. This is due to the fact that the
 * developers believe that a scientific publication features every "real" author
 * in the authors list anyway and group authors are merely additional
 * information about the collective name of the group of authors. Example: a
 * paper might have authors Page, Brin, Google founders. In this list,
 * "Google founders" is just the name of the group Page and Brin, not an
 * additional author.
 * 
 * @author mjordan
 */
public class PubMedWrapper extends AbstractWrapper {

    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("pubmed");
    /**
     * The URL prefix for detail links.
     */
    private static final String DETAIL_URL_PREFIX = "http://www.ncbi.nlm.nih.gov/pubmed/";
    /**
     * The database ID for the whole pubmed index.
     */
    private static final String MEDLINE_LIBRARY = "pubmed";
    /**
     * How many documents to retrieve maximally.
     */
    private static final String MAX_RESULTS = "500";
    /**
     * If set to true, group authors returned from PubMed are treated as persons
     * and, thus, modeled as {@link Person} instances in the author list. These
     * authors then only have a lastname set and no firstname.
     * <p>
     * If set to false, group authors are skipped.
     */
    private static final boolean TREAT_GROUPS_LIKE_PERSONS = false;
    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "pubmed";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "entrez");
    /**
     * The converter that knows about queries to PubMed.
     */
    private final PubMedQueryConverter converter = new PubMedQueryConverter();
    /**
     * The converter for year ranges in queries.
     */
    private final YearRangeConverter yrConverter = new YearRangeConverter();


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        return true;
    }


    @Override
    public SourceID getSourceID() {
        return SOURCE_ID;
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    protected StoredDocumentList process(QueryNodeBool conjunction) {
        throw new NotImplementedException("This wrapper processes whole queries");
    }


    @Override
    protected StoredDocumentList process(DocumentQuery query) {
        final Query q = query.getQuery();
        final String queryStr = converter.convert(q);
        final YearRange yearRange = yrConverter.convertYearRange(q);
        Filter filter = new Filter(query);
        return search(queryStr, yearRange, filter);
    }


    private StoredDocumentList search(String query, YearRange yearRange, Filter filter) {
        StoredDocumentList out = new StoredDocumentList();
        try {
            final EUtilsServiceStub service = new EUtilsServiceStub();
            final EUtilsServiceStub.ESearchRequest req = new EUtilsServiceStub.ESearchRequest();

            req.setDb(MEDLINE_LIBRARY);
            req.setTerm(query);
            req.setRetMax(MAX_RESULTS);

            if (yearRange.minYear != null) {
                req.setMindate(yearRange.minYear.toString());
            }
            if (yearRange.maxYear != null) {
                req.setMaxdate(yearRange.maxYear.toString());
            }

            final EUtilsServiceStub.ESearchResult res = service.run_eSearch(req);

            final IdListType resultList = res.getIdList();
            if (resultList != null) {
                final String[] idList = resultList.getId();
                if (idList != null) {
                    out = retrieveSummaries(filter, idList);
                }
            }
        }
        catch (RemoteException e) {
            getLogger().error("Error accessing PubMed", e);
            throw new WrapperTemporaryException();
        }

        return out;
    }


    private StoredDocumentList retrieveSummaries(Filter filter, String... ids) {
        final StoredDocumentList out = new StoredDocumentList();

        try {
            final EUtilsServiceStub service = new EUtilsServiceStub();
            final EUtilsServiceStub.ESummaryRequest req = new EUtilsServiceStub.ESummaryRequest();
            req.setDb(MEDLINE_LIBRARY);

            final String idListStr = getIdList(ids);
            req.setId(idListStr);

            final EUtilsServiceStub.ESummaryResult res = service.run_eSummary(req);

            final DocSumType[] summaries = res.getDocSum();
            if (summaries != null) {
                for (DocSumType summary : summaries) {
                    final String id = summary.getId();
                    final TextDocument doc = createDocument(id, summary);
                    if (filter.check(doc)) {
                        final StoredDocument stored = new StoredDocument(doc);
                        stored.addSource(new SourceInfo(getSourceID(), id));
                        out.add(stored);
                    }
                }
            }
        }
        catch (RemoteException e) {
            getLogger().error("Problem with access to PubMed", e);
            throw new WrapperTemporaryException();
        }

        return out;
    }


    private TextDocument createDocument(final String id, DocSumType summary) {
        final TextDocument doc = new TextDocument();

        final ItemType[] items = summary.getItem();
        if (items == null) {
            return doc;
        }

        for (ItemType item : items) {
            final String itemName = item.getName();
            final String itemContent = item.getItemContent();

            if (checkField("Title", itemName, itemContent)) {
                doc.setTitle(itemContent);
            }
            else if ("AuthorList".equals(itemName)) {
                final PersonList personList = getAuthors(item);
                doc.setAuthorList(personList);
            }
            else if (checkField("PubDate", itemName, itemContent)) {
                int year = getYear(itemContent);
                doc.setYear(year);
            }
            else if (checkField("FullJournalName", itemName, itemContent)) {
                doc.setFieldValue(Field.JOURNAL, itemContent);
            }
            else if (checkField("Pages", itemName, itemContent)) {
                doc.setFieldValue(Field.PAGES, itemContent);
            }
            else if (checkField("Volume", itemName, itemContent)) {
                doc.setFieldValue(Field.VOLUME, itemContent);
            }
            else if (checkField("Issue", itemName, itemContent)) {
                doc.setFieldValue(Field.NUMBER, itemContent);
            }
            else if (checkField("Issue", itemName, itemContent)) {
                doc.setFieldValue(Field.ISSN, itemContent);
            }
            else if (checkField("DOI", itemName, itemContent)) {
                doc.setFieldValue(Field.DOI, itemContent);
            }
        }

        addURLforId(doc, id);
        return doc;
    }


    private int intFromString(final String itemContent) {
        int issue;
        try {
            issue = Integer.parseInt(itemContent.replaceAll("[^0-9]", ""));
        }
        catch (NumberFormatException e) {
            issue = 0;
        }
        return issue;
    }


    private void addURLforId(TextDocument doc, final String id) {
        final String detailURL = DETAIL_URL_PREFIX + id;
        try {
            doc.addDetailURL(new URL(detailURL));
        }
        catch (MalformedURLException e) {
            getLogger().error("URL erroneous: " + detailURL, e);
        }
    }


    @SuppressWarnings("unused")
    private PersonList getAuthors(ItemType item) {
        final ItemType[] subItems = item.getItem();
        final PersonList personList = new PersonList();
        if (subItems == null) {
            return personList;
        }

        for (ItemType author : subItems) {
            if ("Author".equals(author.getName())) {
                final Person p = authorFromString(author.getItemContent());
                personList.add(p);
            }
            else if (TREAT_GROUPS_LIKE_PERSONS && "CollectiveName".equals(author.getName())) {
                final Person p = new Person("", author.getItemContent());
                personList.add(p);
            }
        }
        return personList;
    }


    private boolean checkField(final String expected, final String itemName, final String itemContent) {
        return expected.equals(itemName) && (itemContent != null);
    }


    /**
     * Returns a {@link Person} object for a name in the given string.
     * <p>
     * Package-visible basically only to make it testable.
     * 
     * @param authorStr
     *            the name to convert
     * @return the Person object for the name
     */
    Person authorFromString(String authorStr) {
        Person author;
        final int splitPos = authorStr.lastIndexOf(' ');
        if (splitPos != -1) {
            String firstName = authorStr.substring(splitPos, authorStr.length()).trim();
            String lastName = authorStr.substring(0, splitPos).trim();
            author = new Person(firstName, lastName);
        }
        else {
            author = new Person(authorStr);
        }
        return author;
    }


    /**
     * Returns a year for the given string.
     * <p>
     * Package-visible basically only to make it testable.
     * 
     * @param dateStr
     *            the date string given. E.g. 2004 or 2005/06/01 or 01/02/2004
     * @return the year found in all the garbage or
     *         {@link Document#YEAR_INVALID} if no valid four-digit year is
     *         found in the input
     */
    int getYear(String dateStr) {
        int year = Document.YEAR_INVALID;
        final String[] parts = dateStr.split("[^0-9]+");

        for (String part : parts) {
            if (part.length() == 4) {
                year = intFromString(part);
            }
        }

        return year;
    }


    private String getIdList(String... ids) {
        final StringBuilder idList = new StringBuilder();
        for (String id : ids) {
            if (idList.length() != 0) {
                idList.append(',');
            }
            idList.append(id);
        }
        final String idListStr = idList.toString();
        return idListStr;
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {

        try {
            final EFetchPubmedServiceStub service = new EFetchPubmedServiceStub();
            final EFetchRequest req = new EFetchRequest();
            final String idListStr = getIdList(incomplete);
            req.setId(idListStr);

            final EFetchResult res = service.run_eFetch(req);

            final PubmedArticleType[] articles = res.getPubmedArticleSet().getPubmedArticle();
            if (articles != null) {
                for (PubmedArticleType article : articles) {

                    final MedlineCitationType citation = article.getMedlineCitation();
                    final TextDocument doc = new TextDocument();
                    doc.setTitle(citation.getArticle().getArticleTitle());

                    doc.setYear(getYear(citation));

                    doc.setAbstract(getAbstract(citation));
                    doc.setAuthorList(getAuthors(citation.getArticle()));
                    addURLforId(doc, article.getMedlineCitation().getPMID());
                    final StoredDocument tmpStored = new StoredDocument(doc);
                    final String oid = OIDFactory.calcOid(doc);
                    doc.setOid(oid);
                    final StoredDocument incompleteStored = findOid(incomplete, oid);
                    if (incompleteStored != null) {
                        setDetailTimestampToCurrent(incompleteStored);
                        incompleteStored.merge(tmpStored);
                    }
                }
            }
        }
        catch (RemoteException e) {
            getLogger().error("Error accessing PubMed", e);
            throw new WrapperTemporaryException();
        }
    }


    private StoredDocument findOid(StoredDocumentList incomplete, String oid) {
        for (StoredDocument candidate : incomplete) {
            if (oid.equals(candidate.getOid())) {
                return candidate;
            }
        }
        return null;
    }


    private String getAbstract(MedlineCitationType citation) {
        String abstr = null;

        try {
            abstr = citation.getArticle().getAbstract().getAbstractText();
        }
        catch (NullPointerException e) {
            // No abstract, apparently
        }
        return abstr;
    }


    private int getYear(MedlineCitationType citation) {
        final ArticleType article = citation.getArticle();
        int year;
        try {
            String yearStr = article.getJournal().getJournalIssue().getPubDate().getPubDateTypeSequence_type1()
                            .getYear();
            year = intFromString(yearStr);
        }
        catch (NullPointerException e) {
            getLogger().error("NPE during year parsing", e);
            year = Document.YEAR_INVALID;
        }
        catch (NumberFormatException e) {
            getLogger().error("NFE during year parsing", e);
            year = Document.YEAR_INVALID;
        }

        return year;
    }


    private PersonList getAuthors(ArticleType article) {
        if (article != null) {
            return getAuthors(article.getAuthorList());
        }
        else {
            return new PersonList();
        }
    }


    private PersonList getAuthors(AuthorListType authorList) {
        final PersonList out = new PersonList();
        final AuthorType[] authorArray = authorList.getAuthor();
        if (authorArray == null) {
            return out;
        }

        for (AuthorType author : authorArray) {
            final AuthorTypeSequence_type0 naturalAuthor = author.getAuthorTypeSequence_type0();

            if (naturalAuthor != null) {
                final Person p = new Person(naturalAuthor.getForeName(), naturalAuthor.getLastName());
                out.add(p);
            }
            else if (TREAT_GROUPS_LIKE_PERSONS) {
                final AuthorTypeSequence_type1 groupAuthor = author.getAuthorTypeSequence_type1();
                final Person p = new Person("", groupAuthor.getCollectiveName());
                out.add(p);
            }
        }

        return out;
    }


    private String getIdList(StoredDocumentList incomplete) {
        final StringBuilder idList = new StringBuilder();
        for (StoredDocument stored : incomplete) {
            if (idList.length() != 0) {
                idList.append(',');
            }
            final String id = getDetailInfo(stored);
            idList.append(id);
        }
        final String idListStr = idList.toString();
        return idListStr;
    }

}
