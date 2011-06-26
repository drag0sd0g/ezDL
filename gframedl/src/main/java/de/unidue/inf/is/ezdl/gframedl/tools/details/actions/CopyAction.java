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

package de.unidue.inf.is.ezdl.gframedl.tools.details.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlcore.message.content.UserLogNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailView;



/**
 * Uses JEditorPane.copy() to copy the selected text to System-Clipboard.
 */
public class CopyAction extends AbstractAction {

    private static final long serialVersionUID = 1341962837749602030L;

    private DetailView detailView;
    private JEditorPane editorPane;


    /**
     * Constructor.
     */
    public CopyAction(JEditorPane editorPane, DetailView detailView) {
        super(I18nSupport.getInstance().getLocString("ezdl.actions.copyToClipboard"));
        this.editorPane = editorPane;
        this.detailView = detailView;
        putValue(Action.SMALL_ICON, Icons.COPY.get16x16());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (editorPane.getSelectionStart() == editorPane.getSelectionEnd()) {
            editorPane.requestFocusInWindow();
            editorPane.selectAll();
        }
        editorPane.copy();

        UserLogNotify userLogNotify = new UserLogNotify(ToolController.getInstance().getSessionId(),
                        "copydetailstosystemclipboard");
        DLObject object = detailView.getObject();
        if (object != null) {
            userLogNotify.addParameter("oid", object.getOid());
        }
        Dispatcher.postEvent(new BackendEvent(this, userLogNotify));
    }
}
