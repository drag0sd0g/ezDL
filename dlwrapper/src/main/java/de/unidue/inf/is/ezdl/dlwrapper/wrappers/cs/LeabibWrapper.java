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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.SolrQueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;



/**
 * Toolkit based Wrapper for LeaBiB.
 */
public class LeabibWrapper extends AbstractBasicToolkitWrapper {

    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("leabib");
    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "leabib";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "csbib");
    /**
     * The file name of the toolkit config for Document queries.
     */
    private static final String FILE_NAME_LEABIB_SEARCH_XML = "leabib_search.xml";

    private static final String HT_KEY_YEAR = "year";
    private static final String HT_KEY_AUTHORS = "authors";
    private static final String HT_KEY_DETAILLINK = "detaillink";
    private static final String HT_KEY_ABSTRACT = "abstract";
    private static final String HT_KEY_TITLE = "title";

    private static final int RESULTS_PER_PAGE = 200;
    private static final String TOOLKIT_PROP_START = "start";


    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public LeabibWrapper() {
        this(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public LeabibWrapper(ToolkitAPI api) {
        super(api);
    }


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    public SourceID getSourceID() {
        return SOURCE_ID;
    }


    @Override
    protected WrapperMode getWrapperMode() {
        return WrapperMode.WHOLE_QUERY;
    }


    @Override
    protected String getTookitConfigFileQuery() {
        return FILE_NAME_LEABIB_SEARCH_XML;
    }


    @Override
    protected String getTookitConfigFileDetails() {
        return null;
    }


    @Override
    protected String initialSearchUrl() {
        return "http://liinwww.ira.uka.de/csbib/Misc/LEABiB/index";
    }


    @Override
    protected QueryConverter newQueryConverter() {
        Map<Field, String> fieldMapping = new HashMap<Field, String>();
        fieldMapping.put(Field.TITLE, "ti");
        fieldMapping.put(Field.YEAR, "yr");
        fieldMapping.put(Field.AUTHOR, "au");
        fieldMapping.put(Field.TEXT, "ti");
        return new SolrQueryConverter(fieldMapping);
    }


    @Override
    protected String getYearRangeParameter(YearRange range, Date now) {
        return "";
    }


    @Override
    protected Map<String, Object> calcToolkitProps(String query, String addRange) {
        Map<String, Object> props = super.calcToolkitProps(query, addRange);
        props.put(TOOLKIT_PROP_START, "1");
        return props;
    }


    @Override
    protected void betweenPages(Map<String, Object> toolkitProps, int page, URL nextPage) {
        super.betweenPages(toolkitProps, page, nextPage);
        toolkitProps.remove(TOOLKIT_PROP_START);
        toolkitProps.put(TOOLKIT_PROP_START, "" + (RESULTS_PER_PAGE * page + 1));
    }


    @SuppressWarnings({
                    "unchecked", "rawtypes"
    })
    @Override
    protected Document createDocumentFromMap(Map ht) {
        getLogger().debug("Map: " + ht);
        TextDocument d = new TextDocument();

        d.setTitle((String) ht.get(HT_KEY_TITLE));
        d.setAbstract((String) ht.get(HT_KEY_ABSTRACT));

        URL detailLink = (URL) ht.get(HT_KEY_DETAILLINK);
        if (detailLink != null) {
            URLList urls = new URLList();
            urls.add(detailLink);
            d.setDetailURLs(urls);
        }

        Object authors = ht.get(HT_KEY_AUTHORS);
        if (authors instanceof List<?>) {
            List<String> authorsList = (List<String>) authors;
            PersonList authorList = new PersonList();
            for (String author : authorsList) {
                Person a = new Person(author);
                authorList.add(a);
            }
            d.setAuthorList(authorList);
        }

        /* we also get journal or conference data... */

        try {
            d.setYear(Integer.parseInt((String) ht.get(HT_KEY_YEAR)));
        }
        catch (NumberFormatException e) {
            d.setYear(Integer.MIN_VALUE);
        }

        getLogger().debug("Document: " + d);
        return d;
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        for (StoredDocument stored : incomplete) {
            setDetailTimestampToCurrent(stored);
        }
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        // TODO: implement this.
        return true;
    }


    @Override
    protected URL documentDetailsUrl(String detailInfo) throws MalformedURLException {
        return null;
    }

}
