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

import javax.swing.JPanel;

import de.unidue.inf.is.ezdl.gframedl.ToolController;



public abstract class AbstractToolView extends JPanel implements ToolView {

    private static final long serialVersionUID = 2399524551936517386L;

    /**
     * The tool instance to which this view belongs.
     */
    private Tool parentTool;

    /**
     * A view remembers its index.
     */
    private int indexNumber = 0;

    private final OpenViewAction openViewAction;


    public AbstractToolView(Tool parentTool) {
        this.parentTool = parentTool;
        this.openViewAction = new OpenViewAction(parentTool, this);
    }


    public AbstractToolView(Tool parentTool, int index) {
        this.parentTool = parentTool;
        this.indexNumber = index;
        this.openViewAction = new OpenViewAction(parentTool, this);
    }


    @Override
    public String getToolViewName() {
        return parentTool.getName();
    }


    @Override
    public final int getIndexNumber() {
        return indexNumber;
    }


    @Override
    public final Tool getParentTool() {
        return parentTool;
    }


    protected final void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }


    @Override
    public JPanel getPanel() {
        return this;
    }


    @Override
    public final OpenViewAction getOpenViewAction() {
        return openViewAction;
    }


    @Override
    public final boolean isOpened() {
        return ToolController.getInstance().getDesktop().getCurrentPerspective().getToolViewState(this) == ToolViewState.OPENED;
    }


    @Override
    public final void toFront() {
        ToolController.getInstance().getDesktop().getCurrentPerspective().toFront(this);
    }

}
