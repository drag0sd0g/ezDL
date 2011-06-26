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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;



/**
 * Toolkit based wrapper for CiteSeerX.
 * 
 * @author tacke
 */
public class CiteseerWrapper extends AbstractBasicToolkitWrapper {

    /**
     * Internal service name for the digital library.
     */
    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("citeseer");
    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "citeseer";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "web");
    /**
     * The file name of the toolkit config for Document queries.
     */
    private static final String FILE_NAME_SEARCH_XML = "citeseer_search.xml";
    /**
     * The file name of the toolkit config for detail requests.
     */
    private static final String FILE_NAME_METADATA_XML = "citeseer_details.xml";
    /**
     * Root URL for CiteSeerX.
     */
    private static final String ROOT_URL = "http://citeseerx.ist.psu.edu";
    /**
     * Base URL for the CiteSeerX search interface.
     */
    private static final String SEARCH_URL = ROOT_URL + "/search";
    /**
     * The delegate that converts the {@link ToolkitAPI} output into an ezDL
     * document.
     */
    private static final CiteseerDocumentCreator CREATOR = new CiteseerDocumentCreator();


    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public CiteseerWrapper() {
        super(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public CiteseerWrapper(ToolkitAPI toolkit) {
        super(toolkit);
    }


    @Override
    protected QueryConverter newQueryConverter() {
        return new CiteseerQueryConverter();
    }


    @Override
    protected Document createDocumentFromMap(Map<String, Object> ht) {
        return CREATOR.createDocumentFromMap(ht);
    }


    @Override
    protected URL documentDetailsUrl(String detailInfo) throws MalformedURLException {
        return new URL(ROOT_URL + detailInfo);
    }


    @Override
    protected WrapperMode getWrapperMode() {
        return WrapperMode.WHOLE_QUERY;
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        return true;
    }


    @Override
    protected String getTookitConfigFileDetails() {
        return FILE_NAME_METADATA_XML;
    }


    @Override
    protected String getTookitConfigFileQuery() {
        return FILE_NAME_SEARCH_XML;
    }


    @Override
    protected String initialSearchUrl() {
        return SEARCH_URL;
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
    protected String getYearRangeParameter(YearRange yearRange, Date now) {
        return null;
    }


    @Override
    protected Map<String, Object> calcToolkitProps(String query, String yearRange) {
        Map<String, Object> props = super.calcToolkitProps(query, yearRange);
        props.put("rooturl", ROOT_URL);
        return props;
    }
}
