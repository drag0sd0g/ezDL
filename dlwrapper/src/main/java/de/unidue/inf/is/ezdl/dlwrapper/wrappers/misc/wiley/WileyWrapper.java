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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.importer.RISImporter;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.utils.AbstractYearRangeParameterConverter;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractTwoStepToolkitWrapper;



/**
 * Wrapper for the Wiley online library.
 * 
 * @author mjordan
 */
public class WileyWrapper extends AbstractTwoStepToolkitWrapper {

    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "wiley";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "web");
    /**
     * Wiley's base URL.
     */
    private static final String WILEY_BASE_URL = "http://onlinelibrary.wiley.com";
    /**
     * The URL to post search queries to.
     */
    private static final String SEARCH_URL = WILEY_BASE_URL + "/advanced/search/results";
    /**
     * The URL for RIS export queries.
     */
    private static final String EXPORT_URL = WILEY_BASE_URL + "/documentcitationdownloadformsubmit";
    /**
     * The RIS importer.
     */
    private RISImporter importer = new RISImporter();
    /**
     * The year range converter.
     */
    private static final AbstractYearRangeParameterConverter YRC = new AbstractYearRangeParameterConverter() {

        @Override
        protected String startAndEndYearGiven(String startYear, String endYear) {
            return "dateRange=between&startYear=" + startYear + "&endYear=" + endYear;
        }


        @Override
        protected String onlyStartYearGiven(String startYear, String nowYear) {
            return "dateRange=between&startYear=" + startYear + "&endYear=" + nowYear;
        }


        @Override
        protected String onlyEndYearGiven(String endYear, String nowYear) {
            return "dateRange=between&startYear=1000&endYear=" + endYear;
        }


        @Override
        protected String noYearGiven(String nowYear) {
            return "dateRange=allDates&startYear=&endYear=";
        }
    };


    /**
     * Creates a new wrapper with a standard {@link ToolkitAPI}.
     */
    public WileyWrapper() {
        super(new ToolkitAPI());
    }


    /**
     * Creates a new wrapper with the given {@link ToolkitAPI}.
     * 
     * @param toolkit
     *            a toolkit to use
     */
    public WileyWrapper(ToolkitAPI toolkit) {
        super(toolkit);
    }


    @SuppressWarnings("rawtypes")
    @Override
    protected Document createDocumentFromMap(Map ht) {
        Document doc = super.createDocumentFromMap(ht);

        return doc;
    }


    @Override
    protected URL documentDetailsUrl(String detailInfo) throws MalformedURLException {
        return null;
    }


    @Override
    protected String getTookitConfigFileDetails() {
        return null;
    }


    @Override
    protected String getTookitConfigFileQuery() {
        return "wiley_search.xml";
    }


    @Override
    protected WrapperMode getWrapperMode() {
        return WrapperMode.WHOLE_QUERY;
    }


    @Override
    protected String getYearRangeParameter(YearRange yearRange, Date now) {
        return YRC.getYearRangeParameter(yearRange, now);
    }


    @Override
    protected String initialSearchUrl() {
        return SEARCH_URL;
    }


    @Override
    protected QueryConverter newQueryConverter() {
        return new WileyQueryConverter();
    }


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
        return ServiceNames.getServiceNameForDL(DL_ID);
    }


    @Override
    protected void performStepTwo(StoredDocumentList result) {
        if (result.size() != 0) {
            String exRes = getExtractionResults(result);
            processExtractionResults(result, exRes);
        }
    }


    private String getExtractionResults(StoredDocumentList result) {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("url", EXPORT_URL);
        List<String> dois = new LinkedList<String>();
        for (StoredDocument stored : result) {
            Document doc = stored.getDocument();
            final String encodedDOI = encode((String) doc.getFieldValue(Field.DOI));
            if (encodedDOI != null) {
                dois.add(encodedDOI);
            }
        }
        props.put("dois", dois);
        initToolkit("wiley_step2.xml", 5);
        InputStream res = (InputStream) toolkitExecute(props);
        final BufferedInputStream bis = new BufferedInputStream(res);
        return IOUtils.readInputStreamAsString(bis);
    }


    private String encode(String doi) {
        try {
            return URLEncoder.encode(doi, "utf8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }


    private void processExtractionResults(StoredDocumentList result, String exRes) {
        int preambleEnd = exRes.indexOf('\n', 0);
        preambleEnd = exRes.indexOf('\n', preambleEnd + 1);
        preambleEnd = exRes.indexOf('\n', preambleEnd + 1);
        exRes = exRes.substring(preambleEnd + 1);

        String[] risItems = exRes.split("ER  -\n\r?\n");

        for (String risItem : risItems) {
            Document doc = importer.convert(risItem.trim());
            if (doc != null) {
                final Object docDOI = doc.getFieldValue(Field.DOI);
                URLList urls = new URLList();
                try {
                    urls.add(new URL(WILEY_BASE_URL + "/doi/" + docDOI + "/abstract"));
                    doc.setDetailURLs(urls);
                }
                catch (MalformedURLException e) {
                }

                StoredDocument inList = result.findDocument(Field.DOI, docDOI);
                if (inList != null) {
                    StoredDocument stored = new StoredDocument(doc);
                    stored.addSource(new SourceInfo(getSourceID(), (String) docDOI));
                    result.add(stored);
                    result.remove(inList);
                }
            }
        }
    }

}
