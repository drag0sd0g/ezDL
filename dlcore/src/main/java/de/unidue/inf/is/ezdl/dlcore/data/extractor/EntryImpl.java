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

/**
 * This is a very simple implementation of an {@link Entry}.
 * 
 * @author Jens Kapitza
 * @version 01.03.2010
 */
public class EntryImpl implements Entry {

    private String key;
    private int value;


    /**
     * Constructor.
     * <p>
     * Sets count to 1.
     * 
     * @param k
     *            the string to store
     */
    public EntryImpl(String k) {
        this(k, 1);
    }


    public EntryImpl(String k, int count) {
        key = k;
        value = count;
    }


    @Override
    public String getKey() {
        return key;
    }


    @Override
    public Integer getValue() {
        return value;
    }


    @Override
    public Integer setValue(Integer value) {
        int v = this.value;
        this.value = value;
        return v;
    }


    @Override
    public String getName() {
        return getKey();
    }


    @Override
    public int getCount() {
        return getValue();
    }


    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int compareTo(Entry o) {
        return getKey().compareTo(o.getKey());
    }


    @Override
    public int hashCode() {
        return getKey().hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntryImpl) {
            return getKey().equals(((EntryImpl) obj).getKey());
        }
        return super.equals(obj);
    }
}
