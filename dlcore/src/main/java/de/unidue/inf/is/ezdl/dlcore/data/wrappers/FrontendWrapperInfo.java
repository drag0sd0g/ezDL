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

import javax.swing.Icon;
import javax.swing.ImageIcon;



/**
 * Information about a wrapper (in the user's term "DL") in the language the
 * user requested.
 */
public class FrontendWrapperInfo implements Serializable {

    private static final long serialVersionUID = 5570848443944634130L;

    /**
     * The DL ID, e.g. "acm".
     */
    private String id;
    /**
     * The category id, e.g. "cs".
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
     * The remote service's small icon. The icon can be used in user interfaces.
     * This is lazily created.
     */
    private Icon smallIcon;

    /**
     * The remote service's small icon. The icon can be used in user interfaces.
     * This is lazily created.
     */
    private Icon largeIcon;
    /**
     * The category of the remote service some wrapper connects to. E.g.
     * "computer science", "psychology", ..
     */
    private String category;
    /**
     * Description of the wrapper.
     */
    private String description;
    /**
     * Proposed timeout for the wrapper in seconds.
     */
    private int proposedTimeoutSec;


    public FrontendWrapperInfo() {
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    public String getRemoteName() {
        return remoteName;
    }


    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getProposedTimeoutSec() {
        return proposedTimeoutSec;
    }


    public void setProposedTimeoutSec(int proposedTimeoutSec) {
        this.proposedTimeoutSec = proposedTimeoutSec;
    }


    /**
     * Sets the data for the small icon.
     * 
     * @param smallIconData
     *            the smallIconData to set
     * @see #getSmallIcon()
     */
    public void setSmallIconData(byte[] smallIconData) {
        this.smallIconData = smallIconData;
    }


    /**
     * Sets the data for the large icon.
     * 
     * @param largeIconData
     *            the largeIconData to set
     * @see #getLargeIcon()
     */
    public void setLargeIconData(byte[] largeIconData) {
        this.largeIconData = largeIconData;
    }


    /**
     * @return the small icon or null if the data has been set using
     *         {@link #setSmallIconData(byte[])}. Else null.
     */
    public Icon getSmallIcon() {
        if (smallIcon == null && smallIconData != null) {
            smallIcon = new ImageIcon(smallIconData);
        }
        return smallIcon;
    }


    /**
     * @return the large icon if the data has been set using
     *         {@link #setLargeIconData(byte[])}. Else null
     */
    public Icon getLargeIcon() {
        if (largeIcon == null && largeIconData != null) {
            largeIcon = new ImageIcon(largeIconData);
        }
        return largeIcon;
    }


    @Override
    public String toString() {
        return id + " " + remoteName + " " + category + " " + description + "(" + proposedTimeoutSec + "s)";
    }

}
