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

package de.unidue.inf.is.ezdl.gframedl.perspectives;

import java.awt.Component;

import javax.swing.Icon;



/**
 * Rrepresents a view created at runtime (used by DetailTool).
 */
public class DynamicEzDLView extends EzDLView {

    private static final long serialVersionUID = 3984458990411179578L;

    private String id;


    public DynamicEzDLView(String title, Icon icon, Component component, String id) {
        super(title, icon, component);
        this.id = id;
    }


    public String getId() {
        return id;
    }
}
