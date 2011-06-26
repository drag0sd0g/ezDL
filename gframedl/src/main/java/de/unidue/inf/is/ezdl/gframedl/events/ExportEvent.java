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

package de.unidue.inf.is.ezdl.gframedl.events;

import java.util.ArrayList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.ExtractionAction;



public class ExportEvent extends GFrameEvent {

    private static final long serialVersionUID = -9004474010560386619L;

    private List<Object> payload;


    /**
     * Constructor.
     * 
     * @param eventSource
     *            event sender.
     * @param payload
     *            content to be exported
     */
    public ExportEvent(Object eventSource, List<?> payload) {
        super(eventSource);
        this.payload = new ArrayList<Object>(payload);
    }


    /**
     * Returns the enclosed {@link Document}s.
     * 
     * @return documents
     */
    public List<Object> getContent() {
        return payload;
    }


    /**
     * Fires a new {@link ExportEvent}, calling Dispatcher.postEvent(ev)
     * 
     * @see ExtractionAction
     * @param eventSource
     *            event sender.
     * @param payload
     *            ezDL documents
     */
    public static void fireExportEvent(Object eventSource, List<?> payload) {
        ExportEvent ev = new ExportEvent(eventSource, payload);
        Dispatcher.postEvent(ev);
    }

}
