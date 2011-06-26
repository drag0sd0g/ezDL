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

package de.unidue.inf.is.ezdl.gframedl.tools.search.grouping;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.EquivalenceClass;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupByRelation;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultItem;



/**
 * Group by library.
 * 
 * @author RT
 */
public class GroupByLibrary extends GroupByRelation {

    private static I18nSupport i18n;


    /**
     * Constructor.
     */
    public GroupByLibrary() {
        i18n = I18nSupport.getInstance();
    }


    @Override
    public String getName() {
        return i18n.getLocString("ezdl.grouping.relations.library");
    }


    @Override
    public EquivalenceClass assignObject(Object o) {
        EquivalenceClass result = GroupByRelation.ALL;

        ResultItem ri = (ResultItem) o;
        String sourceDLs = ri.getSourceDLs();
        result = new EquivalenceClass(sourceDLs, null);

        return result;
    }
}
