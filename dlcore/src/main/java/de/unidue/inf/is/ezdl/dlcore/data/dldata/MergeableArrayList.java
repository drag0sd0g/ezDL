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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.merge.MergeUtils;



/**
 * A list of {@link Mergeable}s.
 * 
 * @param <T>
 *            the type of the list
 */
public class MergeableArrayList<T extends Mergeable> extends ArrayList<T> implements MergeableList<T> {

    private static final long serialVersionUID = -5825022186593360571L;

    /**
     * The maximum number of results to include in the {@link #toString()}
     * output.
     */
    protected static final int MAX_RESULTS_IN_TO_STRING = 15;

    /**
     * The logger.
     */
    protected static Logger logger = Logger.getLogger(MergeableArrayList.class);


    /**
     * Create a list with no values.
     */
    public MergeableArrayList() {
        super();
    }


    /**
     * Create a list with initial data.
     * 
     * @param inputData
     *            the initial data used to fill the list
     */
    public MergeableArrayList(Collection<? extends T> inputData) {
        this();
        if (inputData != null) {
            addAll(inputData);
        }
    }


    /**
     * Create a list with initial data.
     * 
     * @param inputData
     *            the initial data used to fill the list
     */
    public MergeableArrayList(MergeableList<? extends T> inputData) {
        super(inputData);
    }


    @Override
    public String toString() {
        return StringUtils.toString(this, toStringHeader(), MAX_RESULTS_IN_TO_STRING);
    }


    protected String toStringHeader() {
        return "DataList";
    }


    /**
     * change the merge stuff to work with a list.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void merge(Mergeable other) {
        if (other instanceof MergeableArrayList<?>) {
            MergeableArrayList<Mergeable> obj = (MergeableArrayList<Mergeable>) other;
            for (Mergeable o : obj) {
                mergeOrAdd(o);
            }
        }
        else if (other != null) {
            mergeOrAdd(other);
        }

        MergeUtils.merge(this, other);
    }


    @SuppressWarnings("unchecked")
    private void mergeOrAdd(Mergeable o) {
        T left = findSimilar(o);
        if (left == null) {
            add((T) o);
        }
        else {
            left.merge(o);
        }
    }


    private T findSimilar(Mergeable obj) {
        for (T n : this) {
            if (n.isSimilar(obj)) {
                return n;
            }
        }
        return null;
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        return equals(obj);
    }

}
