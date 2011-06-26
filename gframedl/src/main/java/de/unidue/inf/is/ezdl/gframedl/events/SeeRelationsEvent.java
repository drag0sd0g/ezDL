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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.tools.search.actions.ExtractionAction;



/**
 * Sends a selection of Documents to the RelationsTool.
 * 
 * @author RT
 */
public class SeeRelationsEvent extends GFrameEvent {

    private static final long serialVersionUID = -9004474010560386619L;

    private List<DLObject> payload;


    /**
     * Constructor.
     * 
     * @param eventSource
     *            event sender.
     * @param payload
     *            content to be viewed.
     */
    public SeeRelationsEvent(Object eventSource, List<? extends DLObject> payload) {
        super(eventSource);
        this.payload = new ArrayList<DLObject>(payload);
    }


    /**
     * Returns the enclosed {@link Document}s.
     * 
     * @return documents
     */
    public List<DLObject> getContent() {
        return payload;
    }


    /**
     * Fires a new {@link SeeRelationsEvent}, calling Dispatcher.postEvent(ev)
     * 
     * @see ExtractionAction
     * @param eventSource
     *            event sender.
     * @param payload
     *            ezDL documents
     */
    public static void fireRelationsEvent(Object eventSource, List<? extends DLObject> payload) {
        SeeRelationsEvent ev = new SeeRelationsEvent(eventSource, payload);
        Dispatcher.postEvent(ev);
    }

}
