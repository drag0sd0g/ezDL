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

package de.unidue.inf.is.ezdl.dlcore.data.extractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * An Implementation of a result list with some utility methods.
 * 
 * @author Jens Kapitza
 * @version 01.03.2010
 */
public class ExtractionResultImpl implements ExtractionResult {

    private int total = 0;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    private List<Entry> items;


    public ExtractionResultImpl() {
        items = new ArrayList<Entry>();
    }


    /**
     * add an entry to the list. And avoid duplicates. in the case the entry
     * item exists we just do an increment on the object. else we add it to the
     * list
     * 
     * @param item
     *            the entry
     */
    public void add(Entry item) {
        // is item in list. change count
        int index = items.indexOf(item);
        int tCount = item.getCount();
        if (index == -1) {
            items.add(item);
        }
        else {
            Entry item2 = items.get(index);
            item2.setValue(item.getCount() + item2.getCount());
            item = item2;
        }

        min = Math.min(min, item.getCount());
        max = Math.max(max, item.getCount());
        total += tCount;
    }


    /**
     * merge to results. just add the all entries from the other set into this
     * one.
     * 
     * @param data
     *            the set of entries
     */
    public void merge(ExtractionResult data) {
        for (Entry e : data.tableData()) {
            add(e);
        }
    }


    @Override
    public int getTotal() {
        return total;
    }


    @Override
    public int getMinimum() {
        return min;
    }


    @Override
    public int getMaximum() {
        return max;
    }


    @Override
    public List<Entry> tableData() {
        return items;
    }


    @Override
    public List<Entry> cloudData(int startRange, int stopRange) {
        if (stopRange < startRange || startRange < 0) {
            throw new IllegalArgumentException("You should correct your index range.");
        }
        List<Entry> list = new ArrayList<Entry>();
        final int div = (getMaximum() - getMinimum());
        for (Entry e : items) {
            int v = div == 0 ? startRange : ((e.getCount() - getMinimum()) * (stopRange - startRange) / div)
                            + startRange;
            list.add(new EntryImpl(e.getName(), v));
        }
        Collections.sort(list);
        return list;
    }

}
