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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.ieee;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceID;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.utils.AbstractYearRangeParameterConverter;
import de.unidue.inf.is.ezdl.dlwrapper.wrappers.AbstractBasicToolkitWrapper;



/**
 * Toolkit based Wrapper for ACM.
 */
public class IEEEWrapper extends AbstractBasicToolkitWrapper {

    private static final String SERVICE_NAME = ServiceNames.getServiceNameForDL("ieee");
    /**
     * The id of the wrapped DL.
     */
    private static final String DL_ID = "ieee";
    /**
     * The source ID.
     */
    private static final SourceID SOURCE_ID = new SourceID(DL_ID, "xadvweb");
    /**
     * Properties key to define the maximum tolerable number of errors per query
     * before the wrapper shuts itself down.
     */
    public static final String MAX_ERRORS_KEY = "maxErrors";
    /**
     * The file name of the toolkit config for Document queries.
     */
    private static final String FILE_NAME_IEEE_SEARCH_XML = "ieee_search_adv.xml";
    /**
     * The delegate that converts the {@link ToolkitAPI} output into an ezDL
     * document.
     */
    private static final IEEEDocumentCreator CREATOR = new IEEEDocumentCreator();
    /**
     * The year range converter.
     */
    private static final AbstractYearRangeParameterConverter YRC = new AbstractYearRangeParameterConverter() {

        @Override
        protected String startAndEndYearGiven(String startYear, String endYear) {
            return startYear + "_" + endYear + "_Publication_Year";
        }


        @Override
        protected String onlyStartYearGiven(String startYear, String nowYear) {
            return startYear + "_" + nowYear + "_Publication_Year";
        }


        @Override
        protected String onlyEndYearGiven(String endYear, String nowYear) {
            return "1900_" + endYear + "_Publication_Year";
        }


        @Override
        protected String noYearGiven(String nowYear) {
            return null;
        }
    };


    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public IEEEWrapper() {
        this(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public IEEEWrapper(ToolkitAPI api) {
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
    protected int getMaxConjunctionCount() {
        return Integer.MAX_VALUE;
    }


    @Override
    protected String getTookitConfigFileQuery() {
        return FILE_NAME_IEEE_SEARCH_XML;
    }


    @Override
    protected String getTookitConfigFileDetails() {
        return null;
    }


    @Override
    protected String initialSearchUrl() {
        return "http://www.ieeexplore.ieee.org/search/searchresult.jsp";
    }


    @Override
    protected QueryConverter newQueryConverter() {
        return new IEEEAdvancedQueryConverter();
    }


    @Override
    protected String getYearRangeParameter(YearRange range, Date date) {
        return YRC.getYearRangeParameter(range, date);
    }


    @Override
    protected Document createDocumentFromMap(Map<String, Object> ht) {
        return CREATOR.createDocumentFromMap(ht);
    }


    @Override
    public void askDetails(StoredDocumentList incomplete) {
        // Not much on the IEEE details page that we'd like to know
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
