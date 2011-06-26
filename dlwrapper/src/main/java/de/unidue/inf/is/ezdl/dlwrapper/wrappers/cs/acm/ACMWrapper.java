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
 * Toolkit based Wrapper for ACM.
 */
public class ACMWrapper extends AbstractBasicToolkitWrapper {

    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("acm");
    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "acm";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "advancedweb");
    /**
     * The file name of the toolkit config for Document queries.
     */
    private static final String FILE_NAME_ACM_SEARCH_XML = "acm_search_adv.xml";
    /**
     * The file name of the toolkit config for detail requests.
     */
    private static final String FILE_NAME_ACM_METADATA_XML = "acm_details.xml";
    /**
     * The root URL of the ACM DL's web site.
     */
    private static final String ACM_ROOT_URL = "http://portal.acm.org/";
    /**
     * The search URL.
     */
    private static final String ACM_SEARCH_URL = ACM_ROOT_URL + "results.cfm";
    /**
     * What to return in {@link #getMaxConjunctionCount()}.
     */
    private static final int MAX_CONJUNCTIONS = 5;

    /**
     * The delegate that converts the {@link ToolkitAPI} output into an ezDL
     * document.
     */
    private static final ACMDocumentCreator CREATOR = new ACMDocumentCreator();


    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public ACMWrapper() {
        this(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public ACMWrapper(ToolkitAPI api) {
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
        return WrapperMode.CONJUNCTIONS;
    }


    @Override
    protected int getMaxConjunctionCount() {
        return MAX_CONJUNCTIONS;
    }


    @Override
    protected String getTookitConfigFileQuery() {
        return FILE_NAME_ACM_SEARCH_XML;
    }


    @Override
    protected String getTookitConfigFileDetails() {
        return FILE_NAME_ACM_METADATA_XML;
    }


    @Override
    protected String initialSearchUrl() {
        return ACM_SEARCH_URL;
    }


    @Override
    protected QueryConverter newQueryConverter() {
        return new ACMAdvancedQueryConverter();
    }


    @Override
    protected String getYearRangeParameter(YearRange range, Date now) {
        return "";
    }


    @Override
    protected Document createDocumentFromMap(Map<String, Object> ht) {
        return CREATOR.createDocumentFromMap(ht);
    }


    @Override
    protected URL documentDetailsUrl(String detailInfo) throws MalformedURLException {
        return new URL(ACM_ROOT_URL + detailInfo);
    }


    @Override
    protected boolean documentIsValid(StoredDocument stored) {
        return true;
    }

}
