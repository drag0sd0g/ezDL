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
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionTool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionType;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.ExtractionAction;



/**
 * A {@link GFrameEvent} containing a list of {@link Document}s and a type that
 * determines what should be extracted from the list.
 * 
 * @see ExtractionAction
 * @see ExtractionTool
 * @author tacke
 */
public class ExtractionEvent extends GFrameEvent {

    private static final long serialVersionUID = -9004474010560386619L;

    private ExtractionType type;
    private List<?> payload;


    /**
     * Constructor.
     * 
     * @param eventSource
     *            event sender.
     * @param payload
     *            content to be extracted
     */
    public ExtractionEvent(Object eventSource, ExtractionType type, List<?> payload) {
        super(eventSource);
        this.type = type;
        this.payload = payload;
    }


    /**
     * Returns the type of the {@link ExtractionEvent}.
     * 
     * @see ExtractionAction
     * @return type
     */
    public ExtractionType getType() {
        return this.type;
    }


    /**
     * Returns the enclosed {@link Document}s.
     * 
     * @return documents
     */
    public List<Object> getContent() {
        return new ArrayList<Object>(this.payload);
    }


    /**
     * Fires a new {@link ExtractionEvent}, calling Dispatcher.postEvent(ev)
     * 
     * @see ExtractionAction
     * @param eventSource
     *            event sender.
     * @param type
     * @param payload
     *            ezDL documents
     */
    public static void fireExtractionEvent(Object eventSource, ExtractionType type, List<?> payload) {
        ExtractionEvent ev = new ExtractionEvent(eventSource, type, payload);
        Dispatcher.postEvent(ev);
    }
}
