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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.EventReceiver;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.CloseViewEvent;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;
import de.unidue.inf.is.ezdl.gframedl.events.GFrameEvent;
import de.unidue.inf.is.ezdl.gframedl.events.RegistrateToolEvent;
import de.unidue.inf.is.ezdl.gframedl.events.RegistrateViewEvent;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspective;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspectives;
import de.unidue.inf.is.ezdl.gframedl.perspectives.ResetPerspectiveAction;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;



public final class MenuBar extends JMenuBar implements EventReceiver {

    private static final long serialVersionUID = -3681954905832156175L;

    private JMenu fileMenu;
    private JMenu toolMenu;
    private JMenu perspectiveMenu;
    private JMenu helpMenu;

    private Map<String, JMenu> toolViewMenus;
    private Map<ToolView, JMenuItem> toolViewMenuItems;

    private I18nSupport i18n = I18nSupport.getInstance();


    public MenuBar() {
        super();
        init();
    }


    private void init() {
        add(getFileMenu());
        add(getToolMenu());
        add(getPerspectiveMenu());
        add(getHelpMenu());

        toolViewMenus = new HashMap<String, JMenu>();
        toolViewMenuItems = new HashMap<ToolView, JMenuItem>();

        Dispatcher.registerInterest(this, RegistrateToolEvent.class);
        Dispatcher.registerInterest(this, RegistrateViewEvent.class);
        Dispatcher.registerInterest(this, CloseViewEvent.class);
    }


    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu(i18n.getLocString("menu.file"));
            JMenuItem exit = new JMenuItem(i18n.getLocString("menu.file.exit"));
            exit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    GFrameEvent gfe = new ExitEvent("");
                    Dispatcher.postEvent(gfe);
                }

            });
            fileMenu.add(exit);
        }
        return fileMenu;
    }


    private JMenu getToolMenu() {
        if (toolMenu == null) {
            toolMenu = new JMenu(i18n.getLocString("menu.tools"));
        }
        return toolMenu;
    }


    private JMenu getPerspectiveMenu() {
        if (perspectiveMenu == null) {
            perspectiveMenu = new JMenu(i18n.getLocString("menu.perspectives"));
            perspectiveMenu.add(new ResetPerspectiveAction());
            perspectiveMenu.addSeparator();
            ButtonGroup buttonGroup = new ButtonGroup();
            for (Perspective perspective : Perspectives.getPerspectives()) {
                JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(perspective.getSwitchPerspectiveAction());
                perspectiveMenu.add(menuItem);
                buttonGroup.add(menuItem);
                if (ToolController.getInstance().getDesktop().getCurrentPerspective() == perspective) {
                    menuItem.setSelected(true);
                }
            }
        }
        return perspectiveMenu;
    }


    @Override
    public JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(i18n.getLocString("menu.help"));
            JMenuItem about = new JMenuItem(i18n.getLocString("menu.help.about"));
            about.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog(ToolController.getInstance().getDesktop().getApplication().getWindow())
                                    .setVisible(true);
                }

            });

            helpMenu.add(about);
        }
        return helpMenu;
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof RegistrateToolEvent) {
            handleRegistrateToolEvent((RegistrateToolEvent) ev);
        }
        else if (ev instanceof RegistrateViewEvent) {
            handleRegistrateViewEvent((RegistrateViewEvent) ev);
        }
        else if (ev instanceof CloseViewEvent) {
            if (((CloseViewEvent) ev).isDynamic()) {
                handleCloseViewEvent((CloseViewEvent) ev);
            }
        }

        return true;
    }


    private void handleRegistrateToolEvent(RegistrateToolEvent ev) {
        List<ToolView> views = ev.getTool().getViews();
        if (views.size() > 0) {
            toolViewMenus.put(ev.getTool().getName(), new JMenu(ev.getTool().getName()));
            toolMenu.add(toolViewMenus.get(ev.getTool().getName()));

            for (ToolView view : views) {
                JMenuItem menuItem = new JMenuItem(view.getToolViewName());
                menuItem.setAction(view.getOpenViewAction());
                toolViewMenuItems.put(view, menuItem);
                toolViewMenus.get(ev.getTool().getName()).add(menuItem);
            }
        }
    }


    private void handleRegistrateViewEvent(RegistrateViewEvent ev) {

        ToolView view = ev.getView();
        JMenuItem menuItem = new JMenuItem(view.getToolViewName());
        menuItem.setAction(view.getOpenViewAction());
        toolViewMenuItems.put(view, menuItem);
        toolViewMenus.get(ev.getTool().getName()).add(menuItem);
    }


    private void handleCloseViewEvent(CloseViewEvent ev) {
        ToolView view = ev.getView();
        JMenu menu = toolViewMenus.get(ev.getTool().getName());
        JMenuItem mi = toolViewMenuItems.get(view);
        if (mi != null) {
            menu.remove(mi);
        }
    }
}
