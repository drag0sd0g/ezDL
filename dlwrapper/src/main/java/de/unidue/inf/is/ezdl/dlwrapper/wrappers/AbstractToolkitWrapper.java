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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.unidue.inf.is.ezdl.dlcore.query.QueryConverter;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.HttpClientCrawler;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;



/**
 * Has some logic additional to the {@link AbstractWrapper} to deal with the
 * {@link ToolkitAPI}.
 * <p>
 * Please note that query strings do not have to be converted by a
 * {@link QueryConverter} implementation because this is already done in the
 * {@link HttpClientCrawler}.
 * <p>
 * The implementation in this class is very generic so that arbitrary types of
 * toolkit wrappers can be implemented using this as a base class. There is a
 * special version that implements the general case where there is a query form
 * to use for searching, a (possibly multi-paged) result list and a detail page
 * for an item.
 * 
 * @see AbstractBasicToolkitWrapper
 */
public abstract class AbstractToolkitWrapper extends AbstractWrapper {

    /**
     * Properties key for the proxy host name.
     */
    public static final String PROXY_PORT_KEY = "toolkit.proxy.port";
    /**
     * Properties key for the proxy port.
     */
    public static final String PROXY_HOST_KEY = "toolkit.proxy.host";
    /**
     * Where to find the config XML files for the wrappers.
     */
    private static final String WRAPPER_CONFIG_PATH = "/wrapperconfigs/";

    /**
     * Reference to the toolkit that handles Document queries.
     */
    private ToolkitAPI toolkit;
    /**
     * If {@link #maxErrorCounter} errors occur during the processing of one
     * query, the wrapper aborts processing the query and throws a
     * {@link WrapperEmergencyException}.
     */
    private int maxErrorCounter;
    /**
     * The number of errors in the current query run.
     */
    private int errorCounter = 0;


    /**
     * Constructor.
     * 
     * @param toolkit
     *            the toolkit to use
     */
    public AbstractToolkitWrapper(ToolkitAPI toolkit) {
        this.toolkit = toolkit;
    }


    /**
     * Initializes the {@link ToolkitAPI} with a config defined by the given
     * URL.
     * 
     * @param url
     *            the URL that points to the toolkit config
     */
    protected void initToolkit(String configFilename, int maxToolkitErrors) {
        URL configURL = this.getClass().getResource(WRAPPER_CONFIG_PATH + configFilename);

        toolkit.setConfigFile(configURL);
        toolkit.setMaxErrorCounter(maxToolkitErrors);
        toolkit.resetErrorCounter();

        final Properties props = getProperties();
        String proxyHost = props.getProperty(PROXY_HOST_KEY);
        String proxyPortStr = props.getProperty(PROXY_PORT_KEY);
        if (proxyHost != null) {
            int proxyPort = Integer.parseInt(proxyPortStr);
            getLogger().debug("Setting proxy to " + proxyHost + ":" + proxyPort);
            toolkit.setProxy(proxyHost, proxyPort);
        }

        initLogDir(toolkit);
    }


    /**
     * Executes the toolkit, returning a List of Maps with the results. This can
     * be used for open queries where many documents are crawled.
     * 
     * @param toolkitProps
     *            the properties to define what the toolkit is supposed to do
     * @return the list of maps with the result. Each list item is a Map between
     *         key Strings and value objects, depending on the passed
     *         tookitProps.
     */
    @SuppressWarnings("rawtypes")
    protected List toolkitExecuteList(Map<String, Object> toolkitProps) {
        List followData = (List) toolkit.execute(toolkitProps);
        if (toolkit.getErrorCounter() != 0) {
            logError("Toolkit reported errors");
        }
        return followData;
    }


    /**
     * Executes the toolkit, returning a single Map with the results. This can
     * be used for detail queries where only one document is crawled.
     * 
     * @param toolkitProps
     *            the properties to define what the toolkit is supposed to do
     * @return the Map between key Strings and value objects, depending on the
     *         passed tookitProps.
     */
    @SuppressWarnings("rawtypes")
    protected Map toolkitExecuteMap(Map<String, Object> toolkitProps) {
        Map followData = (Map) toolkit.execute(toolkitProps);
        if (toolkit.getErrorCounter() != 0) {
            logError("Toolkit reported errors");
        }
        return followData;
    }


    /**
     * Executes the toolkit, returning a single Object with the results.
     * 
     * @param toolkitProps
     *            the properties to define what the toolkit is supposed to do
     * @return the Object returned by the toolkit
     */
    protected Object toolkitExecute(Map<String, Object> toolkitProps) {
        Object followData = toolkit.execute(toolkitProps);
        if (toolkit.getErrorCounter() != 0) {
            logError("Toolkit reported errors");
        }
        return followData;
    }


    private void initLogDir(ToolkitAPI toolkit) {
        final Properties props = getProperties();
        final String log = props.getProperty("log");

        if ("on".equals(log)) {
            toolkit.setLogDir(new File(props.getProperty("logdir")));
            String logDirName = props.getProperty("logdir");
            String wrappername = getAgentName();
            if (logDirName != null) {
                getLogger().info("Yes, logging in " + wrappername + ": " + logDirName);
                toolkit.setLogDir(new File(logDirName));
            }
            else {
                getLogger().info("Yes, no logging in " + wrappername);
                toolkit.setLogDir(null);
            }
        }
    }


    /**
     * Sets the maximum number of errors tolerated before a
     * {@link WrapperEmergencyException} is thrown.
     * 
     * @param maxErrors
     *            the maximum number of errors
     */
    protected void setMaxErrors(int maxErrors) {
        this.maxErrorCounter = maxErrors;
    }


    /**
     * Logs an error and increases the error counter.
     * 
     * @param message
     *            the message to log
     */
    protected void logError(String message) {
        getLogger().error(message);
        increaseErrorCounter();
    }


    /**
     * Logs an error with an exception and increases the error counter.
     * 
     * @param message
     *            the message to log
     * @param e
     *            the exception to log
     */
    protected void logError(String message, Throwable e) {
        getLogger().error(message, e);
        increaseErrorCounter();
    }


    /**
     * Increases the error counter. If the error counter exceeds
     * {@link #maxErrorCounter} a {@link WrapperEmergencyException} is thrown.
     * 
     * @throws WrapperEmergencyException
     */
    protected void increaseErrorCounter() {
        errorCounter++;
        if (errorCounter > maxErrorCounter) {
            throw new WrapperEmergencyException();
        }
    }


    /**
     * Returns the error counter for testing.
     * 
     * @return the error counter
     */
    public int getErrorCounter() {
        return errorCounter;
    }

}