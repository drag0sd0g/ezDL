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

/**
 * An Event which updates some status display, for example the status bar in the
 * application frame.
 */
public class StatusEvent extends GFrameEvent {

    private static final long serialVersionUID = -507139570370536652L;

    private String text;


    public StatusEvent(Object eventSource) {
        super(eventSource);
    }


    public StatusEvent(Object eventSource, String s) {
        super(eventSource);
        setText(s);
    }


    public String getText() {
        return text;
    }


    public void setText(String newText) {
        text = newText;
    }


    @Override
    public String toString() {
        return text;
    }
}
