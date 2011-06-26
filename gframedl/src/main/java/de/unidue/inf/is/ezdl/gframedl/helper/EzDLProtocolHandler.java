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

package de.unidue.inf.is.ezdl.gframedl.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.events.ExecuteInternalQueryEvent;
import de.unidue.inf.is.ezdl.gframedl.events.InternalLinkEvent;



/**
 * This class handles internal links that are embedded in text panes
 */
public class EzDLProtocolHandler extends URLStreamHandler {

    public static final String INTERNAL_QUERY = "ask_query";


    protected EzDLProtocolHandler() {
        super();
    }


    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return null;
    }


    /**
     * map the link to an InternalLinkEvent and delegate
     * 
     * @param String
     *            link
     */
    public void handleLink(URL link) {
        String type = link.getHost();
        String query;
        try {
            query = URLDecoder.decode(link.getQuery(), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            query = link.getQuery();
        }
        InternalLinkEvent linkEvent = null;

        if (type.startsWith(INTERNAL_QUERY)) {
            linkEvent = new ExecuteInternalQueryEvent(this, query);
        }

        if (linkEvent != null) {
            Dispatcher.postEvent(linkEvent);
        }
    }
}
