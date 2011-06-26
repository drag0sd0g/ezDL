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

package de.unidue.inf.is.ezdl.dlcore.data.wrappers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * Keeps information about a wrapper.
 * 
 * @author mjordan
 */
public class WrapperInfo implements Serializable {

    private static final int PROPOSED_TIMEOUT_UNSET = -1;

    private static final long serialVersionUID = -1121833248686972734L;

    /**
     * The DL ID.
     */
    private String id;
    /**
     * The category id.
     */
    private String categoryId;
    /**
     * The name of the remote service, DL, search engine that some wrapper
     * connects to. This is used for displaying in the GUI. E.g. "BibDB intern"
     */
    private String remoteName;
    /**
     * The data of the remote service's small icon. The icon can be used in user
     * interfaces.
     */
    private byte[] smallIconData;
    /**
     * The data of the remote service's small icon. The icon can be used in user
     * interfaces.
     */
    private byte[] largeIconData;
    /**
     * The category of the remote service some wrapper connects to. E.g.
     * "computer science", "psychology", ..
     */
    private Map<Locale, String> category;
    /**
     * Description of the wrapper.
     */
    private Map<Locale, String> description;
    /**
     * The timeout in seconds that is proposed for searches using this wrapper.
     */
    private int proposedTimeoutSec;


    /**
     * Creates a WrapperInfo object whose field values are all null or 0. The
     * category and description maps are created and empty.
     */
    public WrapperInfo() {
        category = new HashMap<Locale, String>();
        description = new HashMap<Locale, String>();
        proposedTimeoutSec = PROPOSED_TIMEOUT_UNSET;
    }


    /**
     * Returns the displayable name of the remote resource that the wrapper
     * connects to.
     * 
     * @return the remoteName
     */
    public String getRemoteName() {
        return remoteName;
    }


    /**
     * Sets the displayable name of the remote resource that the wrapper
     * connects to.
     * 
     * @param remoteName
     *            the remoteName to set
     */
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }


    /**
     * Returns the ID of the category the wrapper belongs to.
     * 
     * @return the category ID
     */
    public String getCategoryId() {
        return categoryId;
    }


    /**
     * Sets Returns the ID of the category the wrapper belongs to.
     * 
     * @param categoryId
     *            the category ID
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    /**
     * Returns the internal ID of the wrapper that should never change over the
     * life time of the wrapper.
     * 
     * @return the wrapper ID
     */
    public String getId() {
        return id;
    }


    /**
     * Sets the internal ID of the wrapper that should never change over the
     * life time of the wrapper.
     * 
     * @param id
     *            the wrapper ID
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Returns the map between locales and corresponding long text category
     * names for the wrapper.
     * 
     * @return the category name translations
     */
    public Map<Locale, String> getCategory() {
        return category;
    }


    /**
     * Sets the map between locales and corresponding long text category names
     * for the wrapper.
     * 
     * @param category
     *            the category name translations
     */
    public void setCategory(Map<Locale, String> category) {
        this.category = category;
    }


    /**
     * Returns the map between locales and corresponding long text descriptions
     * for the wrapper.
     * 
     * @return the description translations
     */
    public Map<Locale, String> getDescription() {
        return description;
    }


    /**
     * Sets the map between locales and corresponding long text descriptions for
     * the wrapper.
     * 
     * @param description
     *            the description translations
     */
    public void setDescription(Map<Locale, String> description) {
        this.description = description;
    }


    /**
     * Returns the proposed minimum timeout in seconds to wait for the wrapper.
     * This can be used to indicate how fast a wrapper generally answers in
     * order to warn users.
     * 
     * @return the proposed timeout in seconds
     */
    public int getProposedMinimumTimeoutSec() {
        return proposedTimeoutSec;
    }


    /**
     * Sets the proposed minimum timeout in seconds to wait for the wrapper.
     * This can be used to indicate how fast a wrapper generally answers in
     * order to warn users.
     * 
     * @param proposedTimeoutSec
     *            the proposed timeout in seconds
     */
    public void setProposedMinimumTimeoutSec(int proposedTimeoutSec) {
        this.proposedTimeoutSec = proposedTimeoutSec;
    }


    /**
     * @return the smallIconData
     */
    public byte[] getSmallIconData() {
        return smallIconData;
    }


    /**
     * @param smallIconData
     *            the smallIconData to set
     */
    public void setSmallIconData(byte[] smallIconData) {
        this.smallIconData = smallIconData;
    }


    /**
     * @return the largeIconData
     */
    public byte[] getLargeIconData() {
        return largeIconData;
    }


    /**
     * @param largeIconData
     *            the largeIconData to set
     */
    public void setLargeIconData(byte[] largeIconData) {
        this.largeIconData = largeIconData;
    }


    @Override
    public String toString() {
        return "Wrapper for " + remoteName + " (in " + category + ") timeout " + proposedTimeoutSec + " secs";
    }


    /**
     * Returns true if the basic fields are set and the locale maps contain at
     * least 1 entry.
     * 
     * @return true, if the object has valid data, else false
     */
    public boolean isValid() {
        final boolean idOk = !StringUtils.isEmpty(id);
        final boolean categoryIdOk = !StringUtils.isEmpty(categoryId);
        final boolean remoteNameOk = !StringUtils.isEmpty(remoteName);
        final boolean catOk = (category.size() != 0);
        final boolean descOk = (description.size() != 0);
        final boolean timeoutOk = (proposedTimeoutSec != PROPOSED_TIMEOUT_UNSET);

        return (idOk && categoryIdOk && remoteNameOk && catOk && descOk && timeoutOk);
    }

}
