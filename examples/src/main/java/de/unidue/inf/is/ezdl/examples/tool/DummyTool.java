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
package de.unidue.inf.is.ezdl.examples.tool;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.gframedl.Icons;
import de.unidue.inf.is.ezdl.gframedl.IconsTuple;
import de.unidue.inf.is.ezdl.gframedl.tools.AbstractTool;
import de.unidue.inf.is.ezdl.gframedl.tools.ToolView;



/* <class> */
public class DummyTool extends AbstractTool {

    @Override
    public boolean handleEzEvent(EventObject ev) {
        return false;
    }


    @Override
    public List<ToolView> createViews() {
        List<ToolView> views = new LinkedList<ToolView>();
        views.add(new DummyToolView(this));
        return views;
    }


    @Override
    protected IconsTuple getIcon() {
        return Icons.DEFAULT.toIconsTuple();
    }


    @Override
    protected String getI18nPrefix() {
        return "ezdl.tools.dummy.";
    }

}
/* </class> */
