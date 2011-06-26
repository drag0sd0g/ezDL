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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.PerspectiveChangedEvent;



/**
 * Action for switching to a perspective.
 */
public class SwitchPerspectiveAction extends AbstractAction {

    private static final long serialVersionUID = -2348846908296091630L;

    private Perspective perspective;


    public SwitchPerspectiveAction(Perspective perspective) {
        super(perspective.getName());
        this.perspective = perspective;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        ToolController.getInstance().getDesktop().switchToPerspective(perspective, false);
        PerspectiveChangedEvent perspectiveChangedEvent = new PerspectiveChangedEvent(this, perspective);
        Dispatcher.postEvent(perspectiveChangedEvent);
    }


    public Perspective getPerspective() {
        return perspective;
    }

}
