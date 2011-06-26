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

import java.io.Serializable;

import com.google.common.base.Objects;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



public class TwoTuple<T extends Serializable, U extends Serializable> extends AbstractDLObject {

    private static final long serialVersionUID = -911406182303096825L;

    private T value1;
    private U value2;


    public TwoTuple(T value1, U value2) {
        super();
        this.value1 = value1;
        this.value2 = value2;
    }


    public T getValue1() {
        return value1;
    }


    public U getValue2() {
        return value2;
    }


    @Override
    public String asString() {
        return value1 + " ## " + value2;
    }


    @Override
    public boolean isSimilar(Mergeable other) {
        if (other instanceof TwoTuple<?, ?>) {
            TwoTuple<?, ?> tt = (TwoTuple<?, ?>) other;
            return Objects.equal(value1, tt.value1) && Objects.equal(value2, tt.value2);
        }
        else {
            return false;
        }
    }

}
