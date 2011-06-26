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

package de.unidue.inf.is.ezdl.gframedl.tools.clipboard.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.actions.AbstractContentActivatedAction;
import de.unidue.inf.is.ezdl.gframedl.tools.clipboard.ClipboardListPanel;



/**
 * Action that clears the given Clipboard.
 * 
 * @author mjordan
 */
public class ClearClipboardAction extends AbstractContentActivatedAction {

    private static final long serialVersionUID = 1L;
    private ClipboardListPanel clipboard;


    public ClearClipboardAction(ClipboardListPanel clipboard) {
        super(clipboard);
        this.clipboard = clipboard;
        String name = I18nSupport.getInstance().getLocString("ezdl.tools.clipboard.clear");
        putValue(Action.NAME, name);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        clipboard.handleClearClipboard();
    }

}
