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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.FloatingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.ClosingUtils;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.Config;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.CloseViewEvent;
import de.unidue.inf.is.ezdl.gframedl.events.ToolStateEvent;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolState;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolViewState;



/**
 * Abstract class for custom perspectives. New perspectives should extend this
 * class.
 */
abstract class AbstractPerspective implements Perspective {

    private Logger logger = Logger.getLogger(AbstractPerspective.class);

    private Map<String, WeakReference<DockingWindow>> dynamicViews = new HashMap<String, WeakReference<DockingWindow>>();

    private RootWindow rootWindow;
    private ViewMap viewMap;
    private Action switchPerspectiveAction;


    @Override
    public final RootWindow setupRootWindow(boolean ignoreState) {
        if (!ignoreState && getPerspectiveFile().exists()) {
            rootWindow = new RootWindow(new EzDLViewSerializer());

            setTheme();
            addListenerToRootWindow();
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(getPerspectiveFile()));
                return setupStoredRootWindow(ois);

            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                return setupNewRootWindow();
            }
            finally {
                checkOpenedTools();
                ClosingUtils.close(ois);
            }

        }
        else {
            RootWindow rw = setupNewRootWindow();
            checkOpenedTools();
            return rw;
        }

    }


    private List<View> getViews(DockingWindow dockingWindow) {
        List<DockingWindow> dockingWindows = getDockingWindowTree(dockingWindow);
        List<View> result = new ArrayList<View>();
        for (DockingWindow dw : dockingWindows) {
            if (dw instanceof View) {
                result.add((View) dw);
            }
        }
        return result;
    }


    private void closeView(ToolView view, boolean dynamic) {
        Dispatcher.postEvent(new CloseViewEvent(this, view, dynamic));
    }


    private void addListenerToRootWindow() {
        rootWindow.addListener(new DockingWindowAdapter() {

            @Override
            public void windowClosing(final DockingWindow window) throws OperationAbortedException {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        checkOpenedTools();
                        checkClosedViews(window);
                    }
                });
            }


            @Override
            public void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        checkOpenedTools();
                    }
                });
            }


            @Override
            public void windowClosed(DockingWindow window) {
                // Workaround for docking framework bug
                for (View view : getViews(window)) {
                    rootWindow.removeView(view);
                }
            }
        });
    }


    private void checkClosedViews(final DockingWindow window) {
        List<DockingWindow> windows = getDockingWindowTreeLeafs(window);
        for (DockingWindow dockingWindow : windows) {
            if (dockingWindow instanceof EzDLView) {
                View view = ((View) dockingWindow);
                ToolView tv = (ToolView) view.getComponent();
                closeView(tv, view instanceof DynamicEzDLView);
            }
        }
    }


    private void checkOpenedTools() {
        List<Tool> tools = ToolController.getInstance().getTools();
        for (Tool tool : tools) {
            ToolState toolState = getToolState(tool);
            tool.getOpenAction().setEnabled(toolState != ToolState.OPENED);
            Dispatcher.postEvent(new ToolStateEvent(this, tool, toolState));

            List<ToolView> views = tool.getViews();

            for (ToolView view : views) {
                ToolViewState viewState = getToolViewState(view);
                view.getOpenViewAction().setEnabled(viewState != ToolViewState.OPENED);
            }
        }
    }


    private RootWindow setupStoredRootWindow(ObjectInputStream ois) throws IOException {
        rootWindow.read(ois);
        rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
        return rootWindow;
    }


    private RootWindow setupNewRootWindow() {
        viewMap = createViewMap();
        rootWindow = DockingUtil.createRootWindow(viewMap, new EzDLViewSerializer(), false);
        setTheme();
        addListenerToRootWindow();
        rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
        layoutRootWindow();
        return rootWindow;
    }


    private void setTheme() {
        DockingWindowsTheme theme = new ShapedGradientDockingTheme();
        // Apply theme
        rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());

        /*
         * Optional TitleBar properties
         * rootWindow.getRootWindowProperties().addSuperObject(
         * PropertiesUtil.createTitleBarStyleRootWindowProperties());
         */
    }


    /**
     * Layouts the root window.
     */
    protected abstract void layoutRootWindow();


    /**
     * Convenience method to retrieve the tool by the tool class.
     * 
     * @param clazz
     *            class of a tool
     * @return the tool
     */
    protected final Tool getTool(Class<? extends Tool> clazz) {
        Tool tool = ToolController.getInstance().getTool(clazz);
        if (tool == null) {
            throw new IllegalStateException("tool is not available!");
        }
        tool.getViews();
        return tool;
    }


    @Override
    public final void openTool(Tool tool) {
        for (ToolView view : tool.getViews()) {
            if (!view.isOpened()) {
                openToolView(view);
            }
        }
    }


    @Override
    public void openToolView(ToolView view) {
        DockingUtil.addWindow(new EzDLView(view), getRootWindow());
    }


    @Override
    public final ToolState getToolState(Tool tool) {
        int i = 0;
        if (tool.getViewCount() > 0) {
            List<ToolView> views = tool.getViews();
            for (ToolView view : views) {
                if (isToolViewOpened(view)) {
                    i++;
                }
            }
            if (i > 0 && i == tool.getViewCount()) {
                return ToolState.OPENED;
            }
            else if (i > 0 && i <= tool.getViewCount()) {
                return ToolState.PARTIALLY_OPENED;
            }
            else if (i == 0) {
                return ToolState.CLOSED;
            }
        }
        {
            return ToolState.CLOSED;
        }
    }


    @Override
    public final DockingWindow getViewWindow(ToolView toolView) {
        List<DockingWindow> dockingWindows = getDockingWindowTreeLeafs(getRootWindow());
        JPanel panel = toolView.getPanel();
        for (DockingWindow dockingWindow : dockingWindows) {
            if (dockingWindow instanceof View && ((View) dockingWindow).getComponent() == panel) {
                return dockingWindow;
            }
        }
        return null;
    }


    @Override
    public final ToolViewState getToolViewState(ToolView view) {
        if (isToolViewOpened(view)) {
            return ToolViewState.OPENED;
        }
        else {
            return ToolViewState.CLOSED;
        }
    }


    private final boolean isToolViewOpened(ToolView toolView) {
        List<DockingWindow> dockingWindows = getDockingWindowTreeLeafs(getRootWindow());
        JPanel panel = toolView.getPanel();
        for (DockingWindow dockingWindow : dockingWindows) {
            if (dockingWindow instanceof View && ((View) dockingWindow).getComponent() == panel) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns all docking windows at the leafs of a window tree.
     * 
     * @param dockingWindow
     *            a docking window
     * @return all docking windows at the leafs of a window tree
     */
    protected final List<DockingWindow> getDockingWindowTreeLeafs(DockingWindow dockingWindow) {
        List<DockingWindow> result = new ArrayList<DockingWindow>();
        if (dockingWindow.getChildWindowCount() == 0) {
            result.add(dockingWindow);
        }
        else {
            for (int i = 0; i < dockingWindow.getChildWindowCount(); i++) {
                DockingWindow childDockingWindow = dockingWindow.getChildWindow(i);
                result.addAll(getDockingWindowTreeLeafs(childDockingWindow));
            }
        }
        return result;
    }


    @Override
    public final void toFront(ToolView toolView) {
        JPanel panel = toolView.getPanel();
        for (DockingWindow dockingWindow : getDockingWindowTreeLeafs(getRootWindow())) {
            if (dockingWindow instanceof View && ((View) dockingWindow).getComponent() == panel) {
                dockingWindow.makeVisible();
            }
        }
    }


    @Override
    public final void close() {
        dockAllViews();
        closeAllDockingWindows();
        rootWindow.removeAll();
    }


    private void dockAllViews() {
        List<DockingWindow> dws = getDockingWindowTree(getRootWindow());
        for (DockingWindow dw : dws) {
            dw.dock();
        }
    }


    private void closeAllDockingWindows() {
        List<DockingWindow> dws = getDockingWindowTree(getRootWindow());
        for (DockingWindow dw : dws) {
            dw.close();
        }
    }


    @Override
    public final void storeState() {
        dockAllViews();
        closeAllDynamicViews();

        ObjectOutputStream oos = null;
        try {
            Config.getInstance().setUserProperty("perspective", getClass().getName());

            oos = new ObjectOutputStream(new FileOutputStream(getPerspectiveFile()));
            rootWindow.write(oos);
        }
        catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            ClosingUtils.close(oos);
        }

    }


    private void closeAllDynamicViews() {
        for (DockingWindow dw : getDockingWindowTreeLeafs(getRootWindow())) {
            if (dw instanceof DynamicEzDLView) {
                dw.close();
            }
        }
    }


    private File getPerspectiveFile() {
        return new File(Config.getInstance().getPerspectiveDir(), String.valueOf(getClass().getName().hashCode()));
    }


    /**
     * Returns all docking windows of a window tree.
     * 
     * @param dockingWindow
     *            a docking window
     * @return all docking windows of a window tree
     */
    protected final List<DockingWindow> getDockingWindowTree(DockingWindow dockingWindow) {
        List<DockingWindow> result = new ArrayList<DockingWindow>();
        if (dockingWindow.getChildWindowCount() == 0) {
            result.add(dockingWindow);
        }
        else {
            for (int i = 0; i < dockingWindow.getChildWindowCount(); i++) {
                DockingWindow childDockingWindow = dockingWindow.getChildWindow(i);
                result.add(childDockingWindow);
                result.addAll(getDockingWindowTree(childDockingWindow));
            }
        }
        return result;
    }


    @Override
    public final Action getSwitchPerspectiveAction() {
        if (switchPerspectiveAction == null) {
            switchPerspectiveAction = new SwitchPerspectiveAction(this);
        }
        return switchPerspectiveAction;
    }


    /**
     * Creates a view map containing all tools.
     * 
     * @return a view map containing all tools.
     */
    protected abstract ViewMap createViewMap();


    /**
     * Returns a view map containing all views.
     * 
     * @return a view map containing all views
     */
    protected final ViewMap getViewMap() {
        if (viewMap == null) {
            viewMap = createViewMap();
        }
        return viewMap;
    }


    /**
     * Returns the i-th view
     * 
     * @param i
     *            the index of the view
     * @return the i-th view
     */
    protected final View getView(int i) {
        return getViewMap().getView(i);
    }


    /**
     * Returns the root window.
     * 
     * @return the root window
     */
    @Override
    public final RootWindow getRootWindow() {
        return rootWindow;
    }


    @Override
    public boolean dynamicViewExists(String id) {
        return dynamicViews.containsKey(id);
    }


    @Override
    public DockingWindow getDynamicView(String id) {
        DockingWindow result = null;
        if (dynamicViewExists(id)) {
            result = dynamicViews.get(id).get();
        }
        return result;
    }


    @Override
    public void addDynamicView(Perspective p, String id, Component c, String name, Icon icon,
                    DockingWindow dockBehindWindow) {
        if (dockBehindWindow == null) {
            RootWindow root = p.getRootWindow();

            Dimension rSize = root.getSize();
            Dimension size = new Dimension(500, 550);

            Point origin = new Point(root.getLocationOnScreen().x + (rSize.width / 2) - (size.width / 2),
                            root.getLocationOnScreen().y + (rSize.height / 2) - (size.height / 2));

            FloatingWindow fw = root.createFloatingWindow(origin, size, new DynamicEzDLView(name, icon, c, id));

            fw.getTopLevelAncestor().setVisible(true);
            fw.getTopLevelAncestor().setBounds(origin.x, origin.y, size.width, size.height);
            c.requestFocus();
            dynamicViews.put(id, new WeakReference<DockingWindow>(fw));
        }
        else {
            DockingWindow fw = new DynamicEzDLView(name, icon, c, id);
            TabWindow tw = DockingUtil.getTabWindowFor(dockBehindWindow);
            tw.addTab(fw);
            dynamicViews.put(id, new WeakReference<DockingWindow>(fw));
        }
    }
}
