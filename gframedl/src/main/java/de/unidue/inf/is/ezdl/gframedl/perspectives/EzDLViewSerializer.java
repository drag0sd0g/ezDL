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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.gframedl.ToolController;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;



/**
 * A custom view serializer for tool views.
 */
class EzDLViewSerializer implements ViewSerializer {

    /**
     * This class contains all data that is required for proper serialization
     * and deserialization.
     */
    static class ViewInfo implements Serializable {

        private static final long serialVersionUID = -3129995276227734445L;

        private String toolClass;
        private int viewId;


        public ViewInfo(String toolClass, int viewId) {
            super();
            this.toolClass = toolClass;
            this.viewId = viewId;
        }


        public String getToolClass() {
            return toolClass;
        }


        public int getViewId() {
            return viewId;
        }

    }


    private Logger logger = Logger.getLogger(EzDLViewSerializer.class);


    @SuppressWarnings("unchecked")
    @Override
    public View readView(ObjectInputStream in) throws IOException {
        try {
            ViewInfo viewInfo = (ViewInfo) in.readObject();
            Class<?> clazz = Class.forName(viewInfo.getToolClass());
            if (Tool.class.isAssignableFrom(clazz)) {
                Tool tool = ToolController.getInstance().getTool((Class<? extends Tool>) clazz);
                int viewId = viewInfo.getViewId();
                if (tool == null || viewId >= tool.getViews().size() || viewId < 0) {
                    return null;
                }
                ToolView toolView = tool.getViews().get(viewId);
                return new EzDLView(toolView);
            }
            else {
                throw new IOException("serialized class is not a tool view");
            }
        }
        catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new IOException("tool view class not found", e);
        }
    }


    @Override
    public void writeView(View view, ObjectOutputStream out) throws IOException {
        if (!isDynamicView(view)) {
            Component component = view.getComponent();
            if (component instanceof ToolView) {
                ToolView toolView = (ToolView) component;
                int index = toolView.getIndexNumber();
                ViewInfo viewInfo = new ViewInfo(toolView.getParentTool().getClass().getName(), index);
                out.writeObject(viewInfo);
            }
            else {
                throw new IllegalStateException("class is not a tool view");
            }

        }
    }


    /**
     * Indicates if a View is a DynamicView.
     */
    private boolean isDynamicView(View v) {
        return v instanceof DynamicEzDLView;
    }

}
