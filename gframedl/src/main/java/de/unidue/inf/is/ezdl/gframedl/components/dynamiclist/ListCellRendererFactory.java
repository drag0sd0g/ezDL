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

package de.unidue.inf.is.ezdl.gframedl.components.dynamiclist;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.renderer.DefaultListRenderer;



/**
 * This class is used to provide different list cell renderer's for different
 * element classes in a JList.
 * 
 * @author R. Tipografov
 */
public class ListCellRendererFactory implements ListCellRenderer {

    private ListCellRenderer defaultListRenderer;
    private Map<Class<?>, ListCellRenderer> listCellRenderers;


    /**
     * Constructor.
     */
    public ListCellRendererFactory() {
        defaultListRenderer = new DefaultListRenderer();
        listCellRenderers = new HashMap<Class<?>, ListCellRenderer>();
    }


    /**
     * Remove a renderer for a class.
     * 
     * @param clazz
     */
    public void removeRenderer(Class<?> clazz) {
        if (listCellRenderers.containsKey(clazz)) {
            listCellRenderers.remove(clazz);
        }
    }


    /**
     * Remove all renderer's.
     */
    public void removeAllRenderers() {
        listCellRenderers.clear();
    }


    /**
     * Add a renderer for an item class.
     * 
     * @param clazz
     * @param lcr
     */
    public void addRenderer(Class<?> clazz, ListCellRenderer lcr) {
        listCellRenderers.put(clazz, lcr);
    }


    /**
     * Set the default renderer.
     * 
     * @param lcr
     */
    public void setDefaultRenderer(ListCellRenderer lcr) {
        defaultListRenderer = lcr;
    }


    @Override
    public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
        if (listCellRenderers.containsKey(arg1.getClass())) {
            return listCellRenderers.get(arg1.getClass()).getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
        }
        else {
            return defaultListRenderer.getListCellRendererComponent(arg0, arg1, arg2, arg3, arg4);
        }
    }
}
