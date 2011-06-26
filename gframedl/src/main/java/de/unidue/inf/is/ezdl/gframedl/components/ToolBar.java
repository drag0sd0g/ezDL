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

import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.gframedl.tools.MakeVisibleAction;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;



public final class ToolBar extends JToolBar {

    private static final long serialVersionUID = -2507626545659061721L;

    private Set<Tool> actions = new HashSet<Tool>();


    public ToolBar() {
        super();
        setBorderPainted(false);
        setFloatable(false);
    }


    public void addTool(Tool tool) {
        actions.add(tool);
        setButtons();
    }


    public void removeTool(Tool tool) {
        actions.remove(tool);
        setButtons();
    }


    private void setButtons() {
        removeAll();
        for (final Tool tool : actions) {
            final MakeVisibleAction action = tool.getMakeVisibleAction();
            JButton button = new JButton(action);
            button.setHideActionText(true);

            final String toolTip = tool.getToolTip();
            if (!StringUtils.isEmpty(toolTip)) {
                button.setToolTipText(toolTip);
            }

            button.setIcon((Icon) action.getValue(Action.SMALL_ICON));
            button.setTransferHandler(new TransferHandler() {

                private static final long serialVersionUID = -3509091704882401610L;


                @Override
                public boolean canImport(TransferSupport support) {
                    return action.getTool().canImportButtonDrop(support);
                }


                @Override
                public boolean importData(TransferSupport support) {
                    return action.getTool().importDataFromButtonDrop(support);
                }
            });
            add(button);
        }
        validate();
        repaint();
    }
}
