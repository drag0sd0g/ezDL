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

package de.unidue.inf.is.ezdl.gframedl.tools.relations;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.Icon;

import net.infonode.docking.DockingWindow;
import de.unidue.inf.is.ezdl.dlfrontend.dispatcher.Dispatcher;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.events.SeeRelationsEvent;
import de.unidue.inf.is.ezdl.gframedl.perspectives.Perspective;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailTool;



/**
 * This tool generates a visual representation of relationships beetween
 * results.
 * 
 * @author RT
 */
public final class RelationsTool extends AbstractTool {

    private static final long serialVersionUID = 167027352569906725L;


    public RelationsTool() {
        initialize();
    }


    private void initialize() {
        Dispatcher.registerInterest(this, SeeRelationsEvent.class);
    }


    @Override
    public boolean handleEzEvent(EventObject ev) {
        if (ev.getSource() == this) {
            return false;
        }
        else if (ev instanceof SeeRelationsEvent) {
            handleRelationsEvent((SeeRelationsEvent) ev);
        }
        return true;
    }


    private void handleRelationsEvent(SeeRelationsEvent ev) {
        Perspective p = ToolController.getInstance().getDesktop().getCurrentPerspective();
        ToolView detailView = ToolController.getInstance().getTool(DetailTool.class).getDefaultView();
        RelationsView rv = new RelationsView(this);
        String viewName = I18nSupport.getInstance().getLocString("ezdl.relations.relationsGraph");
        Icon icon = Icons.RELATION_TOOL.get16x16();
        if ((detailView != null) && (detailView.isOpened())) {
            DockingWindow w = p.getViewWindow(detailView);
            p.addDynamicView(p, "", rv, viewName, icon, w);
        }
        else {
            p.addDynamicView(p, "", rv, viewName, icon, null);
        }
        rv.showRelations(ev.getContent());
    }


    @Override
    public List<ToolView> createViews() {
        return new ArrayList<ToolView>();
    }


    @Override
    protected String getI18nPrefix() {
        return null;
    }


    @Override
    protected IconsTuple getIcon() {
        return null;
    }

}
