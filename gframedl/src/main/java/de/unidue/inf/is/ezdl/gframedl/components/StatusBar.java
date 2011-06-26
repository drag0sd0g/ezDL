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

package de.unidue.inf.is.ezdl.gframedl.components;

import java.util.EventObject;

import javax.swing.JLabel;

import org.jdesktop.swingx.JXStatusBar;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;
import de.unidue.inf.is.ezdl.gframedl.events.StatusEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;



public final class StatusBar extends JXStatusBar implements EventReceiver {

    private static final long serialVersionUID = 466097394917458082L;

    private ToolBar toolBar;
    private JLabel statusBarLabel;


    public StatusBar() {
        super();
        init();
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof StatusEvent) {
            StatusEvent status = (StatusEvent) ev;
            statusBarLabel.setText(status.getText());
            return true;
        }
        else if (ev instanceof ExitEvent) {
            statusBarLabel.setText(I18nSupport.getInstance().getLocString("desktop.exiting"));
            return true;
        }
        return false;
    }


    private void init() {
        toolBar = new ToolBar();
        statusBarLabel = new JLabel("ezDL");
        add(toolBar, JXStatusBar.Constraint.ResizeBehavior.FIXED);
        add(statusBarLabel, JXStatusBar.Constraint.ResizeBehavior.FILL);

        Dispatcher.registerInterest(this, StatusEvent.class);
        Dispatcher.registerInterest(this, ExitEvent.class);
    }


    public void addTool(Tool tool) {
        toolBar.addTool(tool);
    }


    public void removeTool(Tool tool) {
        toolBar.removeTool(tool);
    }
}
