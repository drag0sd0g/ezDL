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

package de.unidue.inf.is.ezdl.dlbackend.wrappers;

import java.io.Serializable;
import java.util.Date;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * Contains information about the source of a {@link Document}.
 * 
 * @author mjordan
 */
public class SourceInfo implements Serializable {

    private static final long serialVersionUID = 1315745517821295651L;

    /**
     * The ID of the source of a document, e.g. the ID of the digital library
     * the document was found in.
     */
    private SourceID sourceId;
    /**
     * Some information that a wrapper could use to retrieve the details of the
     * document within the digital library identified by the {@link #sourceId}.
     */
    private String detailsInfo;
    /**
     * Information as to when the last access was made that delivered details
     * for the object annotated with this object.
     */
    private Date detailTimestamp;


    /**
     * Constructor.
     * 
     * @param sourceID
     *            the ID of the document's source
     * @param detailsInfo
     *            information useable to retrieve the document's details
     * @param timestamp
     *            the timestamp of the access
     */
    public SourceInfo(SourceID sourceID, String detailsInfo, Date timestamp) {
        this.sourceId = sourceID;
        this.detailsInfo = detailsInfo;
        if (timestamp != null) {
            this.detailTimestamp = new Date(timestamp.getTime());
        }
    }


    /**
     * Constructor.
     * <p>
     * The {@link #detailTimestamp} is set to null to indicate that no detail
     * request has been logged, yet.
     * 
     * @param sourceID
     *            the ID of the document's source
     * @param detailsInfo
     *            information usable to retrieve the document's details
     */
    public SourceInfo(SourceID sourceID, String detailsInfo) {
        this.sourceId = sourceID;
        this.detailsInfo = detailsInfo;
        this.detailTimestamp = null;
    }


    /**
     * @return the sourceID
     */
    public SourceID getSourceID() {
        return sourceId;
    }


    /**
     * @return the details
     */
    public String getDetailsInfo() {
        return detailsInfo;
    }


    /**
     * Sets the detail timestamp. No sanity check is done whatsoever.
     * 
     * @param date
     *            the new timestamp. Might be null.
     */
    public void setDetailTimestamp(Date date) {
        this.detailTimestamp = new Date(date.getTime());
    }


    /**
     * @return the detailTimestamp that indicates the last time a detail request
     *         has been made for the object annotated by this object
     */
    public Date getDetailTimestamp() {
        if (detailTimestamp != null) {
            return new Date(detailTimestamp.getTime());
        }
        else {
            return null;
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((detailsInfo == null) ? 0 : detailsInfo.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SourceInfo other = (SourceInfo) obj;
        if (detailsInfo == null) {
            if (other.detailsInfo != null) {
                return false;
            }
        }
        else if (!detailsInfo.equals(other.detailsInfo)) {
            return false;
        }
        if (sourceId == null) {
            if (other.sourceId != null) {
                return false;
            }
        }
        else if (!sourceId.equals(other.sourceId)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "{SourceInfo (" + sourceId + ") " + detailsInfo + "@" + detailTimestamp + "}";
    }


    /**
     * Returns if the given reference to a SourceInfo object is valid.
     * 
     * @param source
     *            the SourceInfo object to check
     * @return true, if the reference is valid, else false.
     */
    public static boolean isValid(SourceInfo source) {
        if (source == null) {
            return false;
        }

        final SourceID sid = source.getSourceID();
        if (sid == null) {
            return false;
        }

        final String dlId = sid.getDL();
        if (dlId == null) {
            return false;
        }

        final String api = sid.getApi();
        if (api == null) {
            return false;
        }

        final String details = source.getDetailsInfo();
        if (details == null) {
            return false;
        }

        return true;
    }

}
