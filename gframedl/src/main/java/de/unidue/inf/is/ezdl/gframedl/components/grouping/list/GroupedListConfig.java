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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.jdesktop.swingx.JXList;



/**
 * The configuration for the GroupedList. This is the default implementation.
 * 
 * @author RB1
 */
public class GroupedListConfig {

    /**
     * Constructor.
     */
    public GroupedListConfig() {
    }


    /**
     * Must return a list of possible group by relations.
     * 
     * @return
     */
    public List<GroupByRelation> getRelations() {
        List<GroupByRelation> result = new ArrayList<GroupByRelation>();
        result.add(new GroupByRelation());
        return result;
    }


    /**
     * Must return an empty list instance. The GroupedList calls this method
     * when new Groups have to be created. This instance can be initialized with
     * cell renderer and mouseListeners etc.
     * 
     * @return
     */
    public JXList getEmptyListInstance() {
        return new JXList();
    }


    /**
     * An empty model which is used by the sublist instance.
     * 
     * @return
     */
    public DefaultListModel getEmptyModel() {
        return new DefaultListModel();
    }


    /**
     * This method is called when a sublist gets disposed.
     * 
     * @param l
     */
    public void removingListInstance(JXList l) {
    }
}
