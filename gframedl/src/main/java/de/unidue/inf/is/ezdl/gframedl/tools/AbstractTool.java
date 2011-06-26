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

package de.unidue.inf.is.ezdl.gframedl.tools;

import java.util.List;

import javax.swing.Icon;
import javax.swing.TransferHandler.TransferSupport;

import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Desktop;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.RegistrateToolEvent;



public abstract class AbstractTool implements Tool {

    private static final long serialVersionUID = 5834506760456139664L;

    private OpenAction openAction;
    private MakeVisibleAction makeVisibleAction;
    private List<ToolView> views;


    public AbstractTool() {
        super();
        initialize();
    }


    @Override
    public final OpenAction getOpenAction() {
        return openAction;
    }


    @Override
    public final MakeVisibleAction getMakeVisibleAction() {
        return makeVisibleAction;
    }


    @Override
    public final ToolView getDefaultView() {
        if (views != null && !views.isEmpty()) {
            return views.get(0);
        }
        else {
            throw new IllegalStateException("no views");
        }
    }


    protected final Desktop getDesktop() {
        return ToolController.getInstance().getDesktop();
    }


    private void initialize() {
        openAction = new OpenAction(this);
        makeVisibleAction = new MakeVisibleAction(this);

        Dispatcher.postEvent(new RegistrateToolEvent(this, this));
    }


    public abstract List<ToolView> createViews();


    @Override
    public final int getViewCount() {
        return views == null ? 0 : views.size();
    }


    @Override
    public final List<ToolView> getViews() {
        if (views == null) {
            views = createViews();
            if (views == null) {
                throw new IllegalStateException("Views have not been created");
            }
        }
        return views;
    }


    /**
     * easy way to implement {@link #getSmallIcon()} and {@link #getBigIcon()}
     * from {@link Tool} interface
     * 
     * @return the icon for this tool
     */
    protected abstract IconsTuple getIcon();


    /**
     * using the {@link #getIcon()} method to get the icon for this tool.
     * 
     * @return null if no icon is given else the 22x22 icon.
     */
    @Override
    public final Icon getBigIcon() {
        if (getIcon() == null) {
            return null;
        }
        return getIcon().get22x22();
    }


    /**
     * using the {@link #getIcon()} method to get the icon for this tool.
     * 
     * @return null if no icon is given else the 16x16 icon
     */
    @Override
    public final Icon getSmallIcon() {
        if (getIcon() == null) {
            return null;
        }
        return getIcon().get16x16();
    }


    @Override
    public boolean canImportButtonDrop(TransferSupport support) {
        return false;
    }


    @Override
    public boolean importDataFromButtonDrop(TransferSupport support) {
        return false;
    }


    @Override
    public final void open() {
        ToolController.getInstance().getDesktop().getCurrentPerspective().openTool(this);
    }


    @Override
    public ToolState getToolState() {
        return ToolController.getInstance().getDesktop().getCurrentPerspective().getToolState(this);
    }


    public void addNewView(ToolView v) {
        views.add(v);
    }


    @Override
    public String getName() {
        return I18nSupport.getInstance().getLocString(getI18nPrefix() + "name");
    }


    @Override
    public String getToolTip() {
        return I18nSupport.getInstance().getLocString(getI18nPrefix() + "tooltip");
    }


    /**
     * Returns the prefix of this tools's keys in the language file.
     * 
     * @return the i18n prefix
     */
    protected abstract String getI18nPrefix();

}
