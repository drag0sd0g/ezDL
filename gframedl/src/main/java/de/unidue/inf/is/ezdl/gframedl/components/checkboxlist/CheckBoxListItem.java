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

package de.unidue.inf.is.ezdl.gframedl.components.checkboxlist;

import javax.swing.ImageIcon;

import de.unidue.inf.is.ezdl.gframedl.components.Filterable;
import de.unidue.inf.is.ezdl.gframedl.tools.search.WrapperChoiceView;



/**
 * Encapsulates an item in the list of data sources in {@link WrapperChoiceView}
 * .
 * 
 * @author tbeckers
 * @author tacke
 */
public class CheckBoxListItem implements Filterable {

    private String label;
    private String value;
    private String description;

    private ImageIcon icon;

    private boolean selected;


    /**
     * Creates a {@link CheckBoxListItem} with the path to the wrapper service.
     * 
     * @param label
     * @param path
     */
    public CheckBoxListItem(String label, String path) {
        this.label = label;
        this.value = path;
    }


    /**
     * Creates a {@link CheckBoxListItem} with the path to the wrapper service
     * and makes sure the referenced wrapper is selected in the frontend.
     * 
     * @param label
     *            (short name)
     * @param path
     *            to the wrapper service
     * @param selected
     */
    public CheckBoxListItem(String label, String path, boolean selected) {
        this.label = label;
        this.value = path;
        this.selected = selected;
    }


    /**
     * Creates a {@link CheckBoxListItem} with all available data and makes sure
     * the referenced wrapper is selected in the frontend.
     * 
     * @param label
     *            (short name)
     * @param path
     *            to the wrapper service
     * @param description
     *            (detailed description of the data source)
     * @param selected
     */
    public CheckBoxListItem(String label, String path, String description) {
        this.label = label;
        this.value = path;
        this.description = description;
    }


    /**
     * Creates a {@link CheckBoxListItem} with all available data and makes sure
     * the referenced wrapper is selected in the frontend.
     * 
     * @param label
     *            (short name)
     * @param path
     *            to the wrapper service
     * @param description
     *            (detailed description of the data source)
     * @param selected
     */
    public CheckBoxListItem(String label, String path, String description, boolean selected) {
        this.label = label;
        this.value = path;
        this.selected = selected;
        this.description = description;
    }


    /**
     * Creates a {@link CheckBoxListItem} with all available pieces of
     * information.
     * 
     * @param label
     *            (short name)
     * @param path
     *            to the wrapper service
     * @param description
     *            (detailed description of the data source)
     * @param icon
     */
    public CheckBoxListItem(String label, String path, String description, ImageIcon icon) {
        this.label = label;
        this.value = path;
        this.icon = icon;
        this.description = description;
    }


    /**
     * Returns if the item is currently selected.
     * 
     * @return selected
     */
    public boolean isSelected() {
        return selected;
    }


    /**
     * Change if the item should be selected or not.
     * 
     * @param isSelected
     */
    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }


    /**
     * Returns the path to the wrapper service.
     * 
     * @return path
     */
    public String getValue() {
        return value;
    }


    /**
     * Returns a detailed description of the data source behind the wrapper
     * service.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Returns the icon for the referenced Wrapper.
     * 
     * @return icon
     */
    public ImageIcon getIcon() {
        return icon;
    }


    @Override
    public String toString() {
        return label;
    }


    @Override
    public String toFilterString() {
        return label;
    }

}
