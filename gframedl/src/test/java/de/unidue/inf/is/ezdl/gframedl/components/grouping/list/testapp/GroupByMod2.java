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

package de.unidue.inf.is.ezdl.gframedl.components.grouping.list.testapp;

import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.EquivalenceClass;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.list.GroupByRelation;
import de.unidue.inf.is.ezdl.gframedl.components.grouping.resources.Resources;



/**
 * Example relation.
 * 
 * @author RB1
 */
public class GroupByMod2 extends GroupByRelation {

    /**
     * Constants for performance reasons. In opposite see implementation of
     * GroupByMod5.
     */
    public final EquivalenceClass CLASS0 = new EquivalenceClass("Aquivalenzklasse 0", Resources.I_NULL);
    public final EquivalenceClass CLASS1 = new EquivalenceClass("Aquivalenzklasse 1", Resources.I_ONE);


    public GroupByMod2() {
    }


    @Override
    public String getName() {
        return "mod 2";
    }


    @Override
    public EquivalenceClass assignObject(Object o) {
        Item ti = (Item) o;
        if ((ti.itemValue % 2) == 0) {
            return CLASS0;
        }
        else {
            return CLASS1;
        }
    }

}
