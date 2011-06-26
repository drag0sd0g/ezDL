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

import java.util.Collections;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;



/**
 * Should be fired, if user selects an object which is to be viewed in
 * DetailTool
 */
public class DetailEvent extends GFrameEvent {

    public enum OpenMode {
        DEFAULT, NEW_TAB, NEW_WINDOW
    }


    private static final long serialVersionUID = 3311810775236580934L;

    private DLObject object;
    private OpenMode openMode;
    private List<String> highlightStrings;


    /**
     * Creates new DetailView instance and calls Dispatcher.postEvent()
     * 
     * @param eventSource
     *            event sender.
     * @param objectToView
     *            content to view
     * @param mode
     *            open mode
     * @param highlightStrings
     *            strings to highlight
     */
    public static void fireDetailEvent(Object eventSource, DLObject objectToView, OpenMode mode,
                    List<String> highlightStrings) {
        DetailEvent ev = new DetailEvent(eventSource, objectToView, mode, highlightStrings);
        Dispatcher.postEvent(ev);
    }


    /**
     * Creates new DetailView instance and calls Dispatcher.postEvent()
     * 
     * @param eventSource
     *            event sender.
     * @param objectToView
     *            content to view
     * @param mode
     *            open mode
     */
    public static void fireDetailEvent(Object eventSource, DLObject objectToView, OpenMode mode) {
        DetailEvent ev = new DetailEvent(eventSource, objectToView, mode, Collections.<String> emptyList());
        Dispatcher.postEvent(ev);
    }


    /**
     * @param eventSource
     *            event sender.
     * @param objectToView
     *            content to view
     * @param highlightStrings
     *            strings to highlight
     */
    public DetailEvent(Object eventSource, DLObject objectToView, OpenMode mode, List<String> highlightStrings) {
        super(eventSource);
        object = objectToView;
        openMode = mode;
        this.highlightStrings = highlightStrings;
    }


    /**
     * @return Object content to view
     */
    public DLObject getObject() {
        return object;
    }


    public OpenMode getOpenMode() {
        return openMode;
    }


    public List<String> getHighlightStrings() {
        return highlightStrings;
    }
}
