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

package de.unidue.inf.is.ezdl.gframedl.export;

import java.util.EventObject;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.ExportEvent;



/**
 * This "background tool" starts the {@link ExportDialog} if it receives the
 * {@link ExportEvent}.
 * 
 * @author mjordan
 */
public final class ExportBackgroundTool implements EventReceiver {

    public static final String I18N_PREFIX = "ezdl.tools.export.";


    /**
     * Creates a new instance.
     */
    public ExportBackgroundTool() {
        Dispatcher.registerInterest(this, ExportEvent.class);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof ExportEvent) {
            ExportEvent event = (ExportEvent) ev;
            List<Object> documentList = event.getContent();

            if (documentList != null) {
                UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(), "export");
                userLogNotify.addParameter("doccount", documentList.size());
                Dispatcher.postEvent(new BackendEvent(this, userLogNotify));

                if (!documentList.isEmpty()) {
                    new ExportDialog(documentList, ToolController.getInstance().getDesktop().getApplication()
                                    .getWindow());
                }
            }
        }
        return false;
    }

}
