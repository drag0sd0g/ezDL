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

import java.util.List;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.ViewMap;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.clipboard.ClipboardTool;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailTool;
import de.unidue.inf.is.ezdl.gframedl.tools.queryhistory.QueryHistoryTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;



/**
 * The default perspective of ezDL.
 */
final class DefaultPerspective extends AbstractPerspective {

    @Override
    protected ViewMap createViewMap() {
        ViewMap viewMap = new ViewMap();

        Tool searchTool = getTool(SearchTool.class);
        ToolView queryView = searchTool.getDefaultView();
        viewMap.addView(0, new EzDLView(queryView));
        ToolView resultView = searchTool.getViews().get(1);
        viewMap.addView(1, new EzDLView(resultView));
        ToolView wrapperView = searchTool.getViews().get(2);
        viewMap.addView(2, new EzDLView(wrapperView));

        Tool queryHistoryTool = getTool(QueryHistoryTool.class);
        ToolView queryHistoryView = queryHistoryTool.getDefaultView();
        viewMap.addView(3, new EzDLView(queryHistoryView));

        Tool detailTool = getTool(DetailTool.class);
        ToolView detailToolView = detailTool.getDefaultView();
        viewMap.addView(4, new EzDLView(detailToolView));

        Tool clipboardTool = getTool(ClipboardTool.class);
        ToolView clipboardToolView = clipboardTool.getDefaultView();
        viewMap.addView(5, new EzDLView(clipboardToolView));

        return viewMap;
    }


    @Override
    protected void layoutRootWindow() {
        getRootWindow().setWindow(
                        new SplitWindow(true, new SplitWindow(false, .3f, getView(0), getView(1)), new SplitWindow(
                                        false, new SplitWindow(true, new SplitWindow(false, getView(2), getView(3)),
                                                        getView(5)), getView(4))));
    }


    @Override
    public void openToolView(ToolView view) {
        List<DockingWindow> dockingWindows = getDockingWindowTree(getRootWindow());
        for (DockingWindow dockingWindow : dockingWindows) {
            if (dockingWindow instanceof TabWindow) {
                ((TabWindow) dockingWindow).addTab(new EzDLView(view));
                return;
            }
        }
        super.openToolView(view);
    }


    @Override
    public String getName() {
        return I18nSupport.getInstance().getLocString("desktop.perspective.name.default");
    }

}
