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

package de.unidue.inf.is.ezdl.gframedl.tools.search.panels.searchprogressoverlay;

import java.awt.Dimension;
import java.util.Observable;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXBusyLabel;

import de.unidue.inf.is.ezdl.gframedl.components.SpinnerPainter;



/**
 * A class to encapsulate all information for a busy effect of one wrapper.
 * These informations will be used by @ BusyWrapperCreator} to generate a panel
 * for each wrapper.
 * 
 * @author markus
 */
class SearchProgressSpinnerItem extends Observable {

    /**
     * Number of results.
     */
    private Integer resultCount;
    /**
     * ID
     */
    private String wrapperID;
    /**
     * Busy Label which shows the spinner animation.
     */
    private JXBusyLabel spinnerLabel;
    /**
     * Defines the type of the entry. True for normal Spinner and false for the
     * total results.
     */
    private boolean totalState;
    /**
     * The icon of the wrapper.
     */
    private Icon icon;
    /**
     * The Name of the wrapper.
     */
    private String wrapperName;


    /**
     * A constructor for a normal spinner entry.
     * 
     * @param id
     *            The ID of the wrapper.
     * @param busyPainter
     *            A {@link SpinnerPainter} which draw a spinner.
     * @param name
     *            The name of the wrapper.
     * @param icon
     *            A icon for the wrapper.
     */
    SearchProgressSpinnerItem(String id, SpinnerPainter busyPainter, String name, Icon icon) {
        this.wrapperID = id;
        this.spinnerLabel = new JXBusyLabel(new Dimension(50, 50));
        this.spinnerLabel.setBusyPainter(busyPainter);
        this.spinnerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.icon = icon;
        this.wrapperName = name;
    }


    /**
     * A constructor for the total results in the overlay.
     * 
     * @param id
     *            The ID of the wrapper.
     * @param busyPainter
     *            A {@link SpinnerPainter} which draw a spinner.
     */

    public SearchProgressSpinnerItem(String id, SpinnerPainter busyPainter) {
        this.wrapperID = id;
        this.spinnerLabel = new JXBusyLabel(new Dimension(50, 50));
        this.spinnerLabel.setBusyPainter(busyPainter);
        this.spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.totalState = true;
        this.resultCount = 0;
    }


    public JXBusyLabel getSpinner() {
        return spinnerLabel;
    }


    public Integer getResultCount() {
        return resultCount;
    }


    public void setResultCount(Integer count) {
        this.resultCount = count;
        updateState();
    }


    public String getWrapperID() {
        return wrapperID;
    }


    public boolean isTotalState() {
        return totalState;
    }


    /**
     * Update the panel which contains these informations with a observer
     * pattern.
     */
    public void updateState() {
        setChanged();
        notifyObservers(resultCount);
    }


    public Icon getIcon() {
        return icon;
    }


    public String getWrapperName() {
        return wrapperName;
    }

}
