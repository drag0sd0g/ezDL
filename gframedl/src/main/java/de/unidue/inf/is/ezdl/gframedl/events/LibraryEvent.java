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



/** Library Event. For example Add new Group */
public class LibraryEvent extends GFrameEvent {

    private static final long serialVersionUID = -4663362276689718883L;

    // some event types
    public static String ADD_GROUP = "ADD GROUP";
    public static String DELETE_GROUP = "DELETE GROUP";
    public static String SAVE_DOCUMENT = "SAVE DOCUMENT";
    public static String CHANGE_REFERENCESYSTEM = "CHANGE_REFERENCESYSTEM";
    public static String DELETE_REFERENCESYSTEM = "DELETE_REFERENCESYSTEM";
    public static String SEND_VERIFIER = "SEND_VERIFIER";
    public static String WORK_OFFLINE = "WORK_OFFLINE";
    public static String CHOOSE_REFERENCESYSTEM = "CHOOSE REFERENCESYSTEM";

    private DLObject object;
    private String type;


    /**
     * @param eventSource
     * @param object
     * @param type
     *            Type of LibraryEvent. for example. LibraryEvent.ADD_GROUP
     */
    public LibraryEvent(Object eventSource, DLObject object, String type) {
        super(eventSource);
        this.object = object;
        this.type = type;
    }


    public DLObject getObject() {
        return object;
    }


    /** Returns the type of library Event */
    public String getType() {
        return type;
    }
}
