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

import javax.swing.Icon;



public final class IconsTuple {

    private Icon get16x16;
    private Icon get22x22;


    public IconsTuple(Icon get22x22, Icon get16x16) {
        super();
        this.get22x22 = get22x22;
        this.get16x16 = get16x16;
    }


    public Icon get22x22() {
        return get22x22;
    }


    public Icon get16x16() {
        return get16x16;
    }

}
