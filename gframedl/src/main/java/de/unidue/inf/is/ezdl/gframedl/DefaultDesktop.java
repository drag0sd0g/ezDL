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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;
import de.unidue.inf.is.ezdl.dlcore.message.content.TextMessageNotify;
import de.unidue.inf.is.ezdl.dlfrontend.comm.BackendEvent;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.components.MenuBar;
import de.unidue.inf.is.ezdl.gframedl.components.StatusBar;
import de.unidue.inf.is.ezdl.gframedl.events.ExitEvent;
import de.unidue.inf.is.ezdl.gframedl.events.OnlineToggle;
import de.unidue.inf.is.ezdl.gframedl.events.ToolStateEvent;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspective;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspectives;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolState;



final class DefaultDesktop implements Desktop {

    private static final long serialVersionUID = 4617937564355083189L;

    private Logger logger = Logger.getLogger(DefaultDesktop.class);

    private Application application;

    private JRootPane rootPane;
    private JPanel contentPane;
    private MenuBar menuBar;
    private StatusBar statusBar;

    private Perspective currentPerspective;
    private Component dockingComponent;

    private ToolController tc;


    public DefaultDesktop(Application app) {
        initialize(app);
    }


    private void exit() {
        logger.info("Going offline");
        currentPerspective.storeState();
    }


    @Override
    public Application getApplication() {
        return application;
    }


    @Override
    public JRootPane getDektopRootPane() {
        return rootPane;
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof ExitEvent) {
            exit();
        }

        else if (ev instanceof ToolStateEvent) {
            ToolStateEvent toolStateEvent = (ToolStateEvent) ev;
            updateToolBar(toolStateEvent.getTool(), toolStateEvent.getToolState());
        }

        else if (ev instanceof BackendEvent) {
            BackendEvent ae = (BackendEvent) ev;
            MessageContent messageContent = ae.getContent();
            if (messageContent instanceof TextMessageNotify) {
                TextMessageNotify motdMessage = (TextMessageNotify) messageContent;
                List<TextMessageNotify> motdList = new ArrayList<TextMessageNotify>();
                motdList.add(motdMessage);
                new MOTD(motdList, getApplication().getWindow());
            }
        }

        return true;
    }


    private void initialize(Application app) {
        this.application = app;

        JFrame.setDefaultLookAndFeelDecorated(true);
        tc = ToolController.getInstance();
        tc.setDesktop(this);

        initializeGui();

        Dispatcher.registerInterest(this, ExitEvent.class);
        Dispatcher.registerInterest(this, OnlineToggle.class);
        Dispatcher.registerInterest(this, ToolStateEvent.class);
        Dispatcher.registerInterest(this, BackendEvent.class);
    }


    private void initializeGui() {
        String perspectiveClassName = Config.getInstance().getUserProperty("perspective");
        if (perspectiveClassName != null) {
            currentPerspective = Perspectives.perspectiveForClassName(perspectiveClassName);
        }
        if (currentPerspective == null) {
            currentPerspective = Perspectives.getPerspectives().get(0);
        }

        rootPane = new JRootPane();
        contentPane = new JPanel();
        statusBar = new StatusBar();
        menuBar = new MenuBar();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(statusBar, BorderLayout.SOUTH);

        rootPane.setContentPane(contentPane);
        rootPane.setJMenuBar(menuBar);
    }


    /**
     * Callback method for Tool initialization. Is being called on Ancestor
     * Event, when Desktop is shown.
     */
    @Override
    public void initTools() {
        ToolController.getInstance().initTools();

        dockingComponent = currentPerspective.setupRootWindow(false);
        contentPane.add(dockingComponent, BorderLayout.CENTER);

        getApplication().setVisible(true);
    }


    private void updateToolBar(Tool tool, ToolState toolState) {
        if (toolState != ToolState.CLOSED) {
            statusBar.addTool(tool);
        }
        else {
            statusBar.removeTool(tool);
        }
    }


    @Override
    public void switchToPerspective(Perspective perspective, boolean ignoreState) {
        currentPerspective.storeState();
        currentPerspective.close();
        currentPerspective = perspective;
        contentPane.remove(dockingComponent);
        dockingComponent = perspective.setupRootWindow(ignoreState);
        contentPane.add(dockingComponent, BorderLayout.CENTER);
        contentPane.validate();
        contentPane.repaint();
    }


    @Override
    public Perspective getCurrentPerspective() {
        return currentPerspective;
    }

}