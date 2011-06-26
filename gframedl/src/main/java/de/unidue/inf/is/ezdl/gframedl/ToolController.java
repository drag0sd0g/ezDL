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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.data.User;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;
import de.unidue.inf.is.ezdl.gframedl.export.ExportBackgroundTool;
import de.unidue.inf.is.ezdl.gframedl.tools.Tool;
import de.unidue.inf.is.ezdl.gframedl.tools.clipboard.ClipboardTool;
import de.unidue.inf.is.ezdl.gframedl.tools.details.DetailTool;
import de.unidue.inf.is.ezdl.gframedl.tools.extraction.ExtractionTool;
import de.unidue.inf.is.ezdl.gframedl.tools.library.LibraryTool;
import de.unidue.inf.is.ezdl.gframedl.tools.queryhistory.QueryHistoryTool;
import de.unidue.inf.is.ezdl.gframedl.tools.relations.RelationsTool;
import de.unidue.inf.is.ezdl.gframedl.tools.search.SearchTool;



/**
 * The ToolController is a central class of the GFrameDL UI. It is a singleton
 * which is responsible for managing the collection of instantiated tools.<br>
 * It knows all available tools of the desktop and should be used to perform
 * actions on all tools.
 */
public final class ToolController {

    private static ToolController instance;

    private Map<Class<?>, Tool> toolsMap;
    private Desktop desktop;
    private List<Tool> tools;


    private ToolController() {
        if (instance != null) {
            throw new IllegalStateException("ToolController already instantiated");
        }
    }


    public Desktop getDesktop() {
        return desktop;
    }


    public void addTool(Tool tool) {
        tool.getViews();
        getTools().add(tool);
        getToolsMap().put(tool.getClass(), tool);
    }


    public String getSessionId() {
        return desktop.getApplication().getSessionId();
    }


    public Map<Class<?>, Tool> getToolsMap() {
        if (toolsMap == null) {
            toolsMap = new HashMap<Class<?>, Tool>();
        }
        return toolsMap;
    }


    public List<Tool> getTools() {
        if (tools == null) {
            tools = new ArrayList<Tool>();
        }
        return tools;
    }


    public User getUserInfo() {
        return desktop.getApplication().getUser();
    }


    @SuppressWarnings("unchecked")
    public <T extends Tool> T getTool(Class<T> c) {
        return (T) getToolsMap().get(c);
    }


    public Tool getToolAsTool(Class<?> c) {
        return getToolsMap().get(c);
    }


    void setDesktop(Desktop newDesktop) {
        desktop = newDesktop;
    }


    public SecurityInfo getSecurityInfo() {
        return desktop.getApplication().getSecurityInfo();
    }


    void initTools() {
        addTool(new SearchTool());
        addTool(new QueryHistoryTool());
        addTool(new DetailTool());
        addTool(new ExtractionTool());
        addTool(new ClipboardTool());
        addTool(new LibraryTool());
        addTool(new RelationsTool());
        new ExportBackgroundTool();
    }


    // access the singleton
    public static ToolController getInstance() {
        synchronized (ToolController.class) {
            if (instance == null) {
                instance = new ToolController();
            }
        }
        return instance;
    }

}
