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

import de.unidue.inf.is.ezdl.dlbackend.message.content.DocumentDetailsFillAsk;



/**
 * Class to identify the source of pieces of information.
 * <p>
 * The idea here is that some digital libraries only store certain parts of the
 * metadata of documents. DL 'A' might e.g. only store title, authors, year and
 * abstract while DL 'B' stores only title, authors, year and citation
 * information. So somewhere down the line (e.g. in the SearchAgent) these
 * pieces of information about a document is merged. Later, additional details
 * could be requested. Then, a wrapper might want to know where to find the
 * detail information about that document.
 * <p>
 * In this case, the RepositoryAgent would send a {@link DocumentDetailsFillAsk}
 * to the wrapper that basically says "please fill in the gaps" and the wrapper
 * can then take the source information from the document (see
 * {@link SourceInfo}) to find the details.
 * <p>
 * Now, maybe two different wrappers both wrap the same DL but using different
 * APIs. One might use a ToolkitAPI approach on the main user site of the DL
 * while the other uses a SOAP interface. In this case the information used to
 * retrieve the details might be different. So to distinguish between these two
 * different APIs, the {@link SourceID} also contains an identifier of the API.
 * 
 * @author mjordan
 */
public class SourceID implements Serializable {

    private static final long serialVersionUID = 3950503057893846122L;

    /**
     * The API ID to indicate access using a web page.
     */
    public static final String API_WEB = "web";
    /**
     * The digital library that the information is from.
     */
    private String dl;
    /**
     * The API that was used to retrieve the information.
     */
    private String api;


    /**
     * Constructor.
     * 
     * @param dl
     *            the digital library that the information is from
     * @param api
     *            the API that was used to retrieve the information
     */
    public SourceID(String dl, String api) {
        super();
        this.dl = dl;
        this.api = api;
    }


    /**
     * @return the digital library that the information is from
     */
    public String getDL() {
        return dl;
    }


    /**
     * @return the API that was used to retrieve the information
     */
    public String getApi() {
        return api;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((api == null) ? 0 : api.hashCode());
        result = prime * result + ((dl == null) ? 0 : dl.hashCode());
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
        SourceID other = (SourceID) obj;
        if (api == null) {
            if (other.api != null) {
                return false;
            }
        }
        else if (!api.equals(other.api)) {
            return false;
        }
        if (dl == null) {
            if (other.dl != null) {
                return false;
            }
        }
        else if (!dl.equals(other.dl)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "{SID " + dl + "/" + api + "}";
    }
}
