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

import javax.swing.Action;
import javax.swing.Icon;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolState;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolViewState;



/**
 * A perspective defines what tools are available on the desktop by default as
 * well as their positioning.
 */
public interface Perspective {

    /**
     * Return the name of a perspective.
     * 
     * @return the name
     */
    public String getName();


    /**
     * Setups and returns the actual component.
     * 
     * @param ignoreState
     *            if a previously stored state should be ignored
     * @return the actual component
     */
    public Component setupRootWindow(boolean ignoreState);


    /**
     * Opens a tool.
     * 
     * @param tool
     *            The tool that should be opened
     */
    public void openTool(Tool tool);


    /**
     * Opens a tool view. By default a new tool view is opened as a new tab
     * behind all other views.
     * 
     * @param view
     *            A tool view that should be opened
     */
    public void openToolView(ToolView view);


    /**
     * Returns the DockingWindow which encapsulates the View. Returns null if
     * the View is closed.
     * 
     * @param toolView
     * @return
     */
    public DockingWindow getViewWindow(ToolView toolView);


    /**
     * Returns the state of a tool.
     * 
     * @param tool
     *            A tool
     * @return the state of the tool
     */
    public ToolState getToolState(Tool tool);


    /**
     * Returns the state of a tool view
     * 
     * @param view
     *            A tool view
     * @return the state of the tool view
     */
    public ToolViewState getToolViewState(ToolView view);


    /**
     * Moves a tool view to the front.
     * 
     * @param toolView
     *            A tools view that should be moved to the front
     */
    public void toFront(ToolView toolView);


    /**
     * Returns the action that is reponsible for switching to this perspective.
     * 
     * @return the action that is reponsible for switching to this perspective
     */
    public Action getSwitchPerspectiveAction();


    /**
     * Closes this perspectives.
     */
    public void close();


    /**
     * Stores the state of this perspective.
     */
    public void storeState();


    /**
     * Returns the RootWindow.
     */
    public RootWindow getRootWindow();


    /**
     * Check's for existence of a DynamicView by id.
     */
    public boolean dynamicViewExists(String id);


    /**
     * Returns the DynamicView (FloatingWindow) with the given id = oid of the
     * contained object.
     */
    public DockingWindow getDynamicView(String id);


    /**
     * Add's a new DynamicView (only if a view with the given oid don't exist).
     * It can be referenced by getDynamicView method.
     * 
     * @param p
     *            the perspective where the view is added.
     * @param id
     *            the id of the DynamicView.
     * @param c
     *            the component encapsulated by the view.
     * @param name
     *            the title of the view.
     * @param icon
     *            the icon of the view.
     * @param dockBehindWindow
     *            this parameter indicates that the view should be opened in a
     *            new window, if dockBehindWindow is null. If dockBehindWindow
     *            is not null, the view is opened as docked tab behind the
     *            dockBehindWindow.
     */
    public void addDynamicView(Perspective p, String id, Component c, String name, Icon icon,
                    DockingWindow dockBehindWindow);
}
