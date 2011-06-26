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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.jdesktop.swingx.JXList;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupByRelation;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupedListConfig;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultListModel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.panels.ResultPanel;
import de.unidue.inf.is.ezdl.gframedl.tools.search.resultList.ResultList;



/**
 * The configuration of the EzDL GroupedList.
 * 
 * @author RT
 */
public class ResultListConfig extends GroupedListConfig {

    private static I18nSupport i18n;
    private ResultPanel rp;


    /**
     * Constructor.
     * 
     * @param rp
     */
    public ResultListConfig(ResultPanel rp) {
        this.rp = rp;
    }


    @Override
    public JXList getEmptyListInstance() {
        ResultList result = new ResultList(rp);
        ResultListModel model = new ResultListModel();
        result.setModel(model);
        return result;
    }


    @Override
    public DefaultListModel getEmptyModel() {
        return new ResultListModel();
    }


    /**
     * Method returns the available grouping relations.
     */
    @Override
    public List<GroupByRelation> getRelations() {
        List<GroupByRelation> result = new ArrayList<GroupByRelation>();
        result.add(new GroupByRelation());
        result.add(new GroupByDecade());
        result.add(new GroupByLibrary());
        result.add(new GroupByAuthors());
        return result;
    }


    @Override
    public void removingListInstance(JXList l) {
        super.removingListInstance(l);
    }


    /**
     * Initialization of the strings used in GroupedList component.
     */
    public static void initStrings() {
        i18n = I18nSupport.getInstance();
        Resources.S_COLLAPSE = i18n.getLocString("ezdl.grouping.collapse");
        Resources.S_COLLAPSE_ALL = i18n.getLocString("ezdl.grouping.collapseAll");
        Resources.S_EXPAND = i18n.getLocString("ezdl.grouping.expand");
        Resources.S_EXPAND_ALL = i18n.getLocString("ezdl.grouping.expandAll");
        Resources.S_SELECT_GROUP = i18n.getLocString("ezdl.grouping.selectGroup");
        Resources.S_DESELECT_GROUP = i18n.getLocString("ezdl.grouping.deselectGroup");
        Resources.S_NO_GROUPING = i18n.getLocString("ezdl.grouping.noGrouping");
        Resources.S_GROUP_BY = i18n.getLocString("ezdl.grouping.groupBy");
        Resources.S_OTHER = i18n.getLocString("ezdl.grouping.other");
    }
}
