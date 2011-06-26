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

package de.unidue.inf.is.ezdl.gframedl.tools.details;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import de.unidue.inf.is.ezdl.gframedl.tools.AbstractToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;



/**
 * One view of DetailTool.
 */
public class DetailToolView extends AbstractToolView {

    private static final long serialVersionUID = 6978293885193576663L;
    private Component viewComponent;
    private boolean fisDefaultView;


    /**
     * Constructor for DetailToolView
     * 
     * @param parentTool
     *            the DetailTool
     */
    public DetailToolView(Tool parentTool, Component viewComponent, boolean fisDefaultView) {
        super(parentTool);
        setViewComponent(viewComponent);
        this.fisDefaultView = fisDefaultView;
    }


    public boolean isDefaultView() {
        return this.fisDefaultView;
    }


    /**
     * Setter for a DetailView-Component.
     */
    public void setViewComponent(Component viewComponent) {
        this.removeAll();
        this.viewComponent = viewComponent;
        setLayout(new BorderLayout());
        if (viewComponent != null) {
            add(viewComponent, BorderLayout.CENTER);
        }

        this.viewComponent.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                e.getComponent().repaint();
            }
        });
    }


    /**
     * Getter for a DetailView-Component.
     */
    public Component getViewComponent() {
        if (!(viewComponent instanceof DetailViewContainer)) {
            return new DetailViewContainer(viewComponent);
        }
        else {
            return viewComponent;
        }
    }
}
