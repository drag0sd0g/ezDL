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

package de.unidue.inf.is.ezdl.gframedl.tools;

import java.util.List;

import javax.swing.Icon;
import javax.swing.TransferHandler.TransferSupport;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;



public interface Tool extends EventReceiver {

    public OpenAction getOpenAction();


    public MakeVisibleAction getMakeVisibleAction();


    public ToolView getDefaultView();


    public List<ToolView> getViews();


    public int getViewCount();


    public String getName();


    public Icon getSmallIcon();


    public Icon getBigIcon();


    public boolean canImportButtonDrop(TransferSupport support);


    public boolean importDataFromButtonDrop(TransferSupport support);


    public void open();


    public ToolState getToolState();


    /**
     * Returns an internationalized tool tip that describes what happens when
     * the user drags an object over the icon in the status bar.
     * 
     * @return the tool tip text ready for display
     */
    public String getToolTip();

}
