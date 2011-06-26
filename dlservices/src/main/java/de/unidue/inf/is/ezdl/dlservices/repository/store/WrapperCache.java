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

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlbackend.ServiceNames;
import de.unidue.inf.is.ezdl.dlbackend.agent.Agent;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersAsk;
import de.unidue.inf.is.ezdl.dlcore.message.content.AvailableWrappersTell;
import de.unidue.inf.is.ezdl.dlcore.misc.TimeoutException;



/**
 * Helper class to cache and discover wrappers from backend.
 * 
 * @author markus
 */

public class WrapperCache {

    private Logger logger = Logger.getLogger(WrapperCache.class);

    /**
     * The threshold for an old cache in milliseconds.
     */
    private static final int LAST_ACCESS_THRESHOLD_MS = 10000;

    /**
     * A map which caches all available wrappers. The wrapper service is key and
     * the category is the value.
     */
    private Map<String, String> wrapperMap;
    /**
     * Creation date of the cache.
     */
    private long timestamp;

    /**
     * The reference to the agent.
     */
    private Agent agent;


    WrapperCache(Agent agent) {
        this.agent = agent;
        updateWrapperMap();
    }


    /**
     * A method to determine whether the cache is still current.
     * 
     * @return True for to old and false for young enough.
     */
    private boolean isTooOld() {
        final long currentDate = System.currentTimeMillis();
        if ((currentDate - timestamp) > LAST_ACCESS_THRESHOLD_MS) {
            return true;
        }
        return false;

    }


    /**
     * Clear the cache.
     */
    private void cacheClear() {
        if (wrapperMap != null) {
            wrapperMap.clear();
        }
    }


    /**
     * Setter for the current timestamp.
     * 
     * @param timestamp
     *            The timestamp to set.
     */
    private void setTimestamp() {
        timestamp = System.currentTimeMillis();
    }


    /**
     * A method to update the WrapperCache.
     * 
     * @param agent
     *            A agent to ask for new wrappers.
     */
    private void updateWrapperMap() {
        try {
            cacheClear();
            wrapperMap = calcWrapperMap();
            setTimestamp();
        }
        catch (TimeoutException e) {
            logger.error(e.getMessage(), e);
        }

    }


    /**
     * A method to find out the category of a wrapper and return a list of
     * available wrapper with the same category.
     * 
     * @param wrapper
     *            We want to know all wrappers with the same category like this
     *            wrapper.
     * @return Return a list of wrapper.
     */
    List<String> filteredCategoryWrapper(String wrapper) {

        if (isTooOld()) {
            updateWrapperMap();
        }

        if (wrapperMap.containsKey(wrapper)) {
            LinkedList<String> keysToRemove = new LinkedList<String>();
            String wrapperCategory = wrapperMap.get(wrapper);
            final Set<Entry<String, String>> entries = wrapperMap.entrySet();
            for (Entry<String, String> wrapperEntry : entries) {
                if (!wrapperEntry.getValue().equals(wrapperCategory)) {
                    keysToRemove.add(wrapperEntry.getKey());
                }
            }
            for (String key : keysToRemove) {
                wrapperMap.remove(key);
            }
        }
        return new ArrayList<String>(wrapperMap.keySet());

    }


    /**
     * Construct a message and send a ask for all available wrappers.
     * 
     * @param agent
     *            The agent we want to ask.
     * @return Map of all available wrappers with key as wrapper path and the
     *         wrapper category as value.
     * @throws TimeoutException
     *             If the ask crate an timeout.
     */
    private Map<String, String> calcWrapperMap() throws TimeoutException {
        final HashMap<String, String> wrapperList = new HashMap<String, String>();
        final String agentName = agent.agentName();
        final String directoryName = agent.getDirectoryName();
        final String nextRequestID = agent.getNextRequestID();
        final AvailableWrappersAsk content = new AvailableWrappersAsk(Locale.getDefault());
        final Message message = new Message(agentName, directoryName, content, nextRequestID);
        final Message tell = agent.ask(message);
        final AvailableWrappersTell availableWrapper = (AvailableWrappersTell) tell.getContent();
        for (FrontendWrapperInfo wInfo : availableWrapper.getWrapperInfos()) {
            wrapperList.put(ServiceNames.getServiceNameForDL(wInfo.getId()), wInfo.getCategoryId());
        }
        logger.info("Map of wrapper: " + wrapperList);
        return wrapperList;
    }

}
