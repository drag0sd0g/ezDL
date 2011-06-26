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

package de.unidue.inf.is.ezdl.gframedl.tools.search.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlcore.message.content.CancelSearchNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;



/**
 * Sends a message to tell the backend to send the results already found
 * immediately, not waiting for the other DLs to send theirs.
 * 
 * @author mjordan
 */
public final class ShowNowAction extends AbstractAction {

    private static final long serialVersionUID = 4021229030018855304L;

    public static final String I18N_PREFIX = "ezdl.tools.search.busyOverlay.";

    private ToolController tc;


    public ShowNowAction() {
        super();
        tc = ToolController.getInstance();
        putValue(Action.NAME, I18nSupport.getInstance().getLocString(I18N_PREFIX + "showNow"));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        setEnabled(false);
        SearchTool searchTool = tc.getTool(SearchTool.class);
        String requestIdToCancel = searchTool.getCurrentSearch().requestId;
        BackendEvent ask = new BackendEvent(this);
        ask.setContent(new CancelSearchNotify(requestIdToCancel, true));
        Dispatcher.postEvent(ask);
    }

}
