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

package de.unidue.inf.is.ezdl.gframedl.tools.details;

import java.awt.Container;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import net.infonode.docking.DockingWindow;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.DLObject;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent;
import de.unidue.inf.is.ezdl.gframedl.events.DetailEvent.OpenMode;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspective;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolState;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolViewState;
import de.unidue.inf.is.ezdl.gframedl.tools.details.views.WelcomeScreenDetailView;



/**
 * The DetailTool is responsible for handling DetailEvent.
 */
public class DetailTool extends AbstractTool {

    public static final String I18N_PREFIX = "ezdl.tools.detail.";


    /**
     * Constructor.
     */
    public DetailTool() {
        initialize();
    }


    private void initialize() {
        Dispatcher.registerInterest(this, DetailEvent.class);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev instanceof DetailEvent) {
            handleDetailEvent((DetailEvent) ev);
        }
        return true;
    }


    private void handleDetailEvent(DetailEvent ev) {
        DLObject object = ev.getObject();
        if (object != null) {
            showDetails(object, ev.getOpenMode(), ev.getHighlightStrings());
        }
    }


    private void showDetails(DLObject o, OpenMode openMode, List<String> highlightStrings) {
        DetailToolView view = null;
        DetailViewContainer dvc = null;
        DetailView dv = null;
        Perspective p = ToolController.getInstance().getDesktop().getCurrentPerspective();
        if (openMode == DetailEvent.OpenMode.NEW_WINDOW) {
            dvc = DetailViewFactory.getDetailViewFor(o, highlightStrings);
            dv = dvc.getDetailView();
            view = new DetailToolView(this, dvc, false);
            p.addDynamicView(p, o.getOid(), view, dv.getTabName(), dv.getIcon(), null);
        }
        if (openMode == DetailEvent.OpenMode.NEW_TAB) {
            view = (DetailToolView) getDefaultView();
            if (p.getToolViewState(view) == ToolViewState.CLOSED) {
                showDetails(o, DetailEvent.OpenMode.DEFAULT, highlightStrings);
            }
            else {
                DockingWindow w = p.getViewWindow(view);
                dvc = DetailViewFactory.getDetailViewFor(o, highlightStrings);
                dv = dvc.getDetailView();
                view = new DetailToolView(this, dvc, false);
                p.addDynamicView(p, o.getOid(), view, dv.getTabName(), dv.getIcon(), w);
            }
        }
        if (openMode == DetailEvent.OpenMode.DEFAULT) {
            view = (DetailToolView) getDefaultView();
            view.toFront();
            dvc = (DetailViewContainer) (view.getViewComponent());
            dv = dvc.getDetailView();
            if ((dv.getObject() == null) || (dv.getObject().getOid() == null)
                            || (!dv.getObject().getOid().equals(o.getOid()))) {
                view.setViewComponent(DetailViewFactory.getDetailViewFor(o, highlightStrings));
                final Container topLevelAncestor = view.getTopLevelAncestor();
                if (topLevelAncestor != null) {
                    topLevelAncestor.setVisible(true);
                    topLevelAncestor.repaint();
                }
            }
        }
        if (getToolState() == ToolState.CLOSED) {
            open();
        }
    }


    @Override
    public List<ToolView> createViews() {
        DetailToolView view = new DetailToolView(this, new WelcomeScreenDetailView(), true);
        List<ToolView> al = new ArrayList<ToolView>();
        al.add(view);
        return al;
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.DETAIL_TOOL.toIconsTuple();
    }


    @Override
    protected String getI18nPrefix() {
        return I18N_PREFIX;
    }
}
