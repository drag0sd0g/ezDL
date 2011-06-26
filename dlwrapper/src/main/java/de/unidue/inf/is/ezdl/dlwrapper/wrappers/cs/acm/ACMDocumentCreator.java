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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.utils.DocumentCreator;



/**
 * Creates ezDL {@link Document} objects from {@link ToolkitAPI} output for the
 * {@link ACMWrapper}.
 * <p>
 * The implementation is merely a renaming to have consistency between wrappers.
 * 
 * @author mjordan
 */
public class ACMDocumentCreator extends DocumentCreator {

    private static final String[] QUERY_PARTS_TO_REMOVE = {
                    "CFID", "CFTOKEN"
    };


    @Override
    protected URL cleanUrl(URL detailUrl) {
        final String query = detailUrl.getQuery();
        if (query == null) {
            return detailUrl;
        }
        final String cleanQuery = removeQueryParts(query);
        URL out;
        try {
            String urlStr = detailUrl.toString();
            int pos = urlStr.indexOf('?');
            urlStr = urlStr.substring(0, pos);
            out = new URL(urlStr + "?" + cleanQuery);
        }
        catch (MalformedURLException e) {
            out = null;
        }

        return out;
    }


    String removeQueryParts(String query) {
        StringBuilder out = new StringBuilder();

        String[] parts = query.split("&");
        boolean first = true;
        for (String part : parts) {
            boolean found = false;
            for (String qp : QUERY_PARTS_TO_REMOVE) {
                if (part.startsWith(qp)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (!first) {
                    out.append('&');
                }
                else {
                    first = false;
                }
                out.append(part);
            }
        }

        return out.toString();
    }


    @Override
    protected void handleDetailLink(Document doc, Object detailLink) {
        super.handleDetailLink(doc, detailLink);
    }
}
