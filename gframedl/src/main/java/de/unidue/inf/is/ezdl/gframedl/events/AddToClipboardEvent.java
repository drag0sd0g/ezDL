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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;



/**
 * should be fired if a {@link DLObject} is sent to the clipboard
 */
public class AddToClipboardEvent extends GFrameEvent {

    private static final long serialVersionUID = 3311810775236580934L;

    private DLObject object;


    /**
     * @param eventSource
     *            event sender.
     * @param objectToView
     *            content to view
     */
    public static void fireClipboardEvent(Object eventSource, DLObject objectToView) {
        AddToClipboardEvent ev = new AddToClipboardEvent(eventSource, objectToView);
        Dispatcher.postEvent(ev);
    }


    /**
     * @param eventSource
     *            event sender.
     * @param objectToView
     *            content to view
     */
    public AddToClipboardEvent(Object eventSource, DLObject objectToView) {
        super(eventSource);
        object = objectToView;
    }


    /**
     * @return Object content to view
     */
    public DLObject getObject() {
        return object;
    }
}
