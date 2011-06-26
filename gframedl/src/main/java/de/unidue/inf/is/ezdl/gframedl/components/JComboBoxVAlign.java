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

package de.unidue.inf.is.ezdl.gframedl.components;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;



/**
 * A {@link JComboBox} that aligns its height and Y position with that of a
 * given reference {@link JComponent}.
 * 
 * @author mj
 */
public class JComboBoxVAlign extends JComboBox {

    private static final long serialVersionUID = 6251796315327110269L;
    private JComponent reference;


    public JComboBoxVAlign() {
        super();
    }


    public JComboBoxVAlign(JComponent reference) {
        this.reference = reference;
    }


    public JComboBoxVAlign(JComponent reference, String[] a) {
        super(a);
        this.reference = reference;
    }


    @Override
    public int getHeight() {
        if (reference != null) {
            return reference.getHeight();
        }

        if (getAction() != null) {
            Icon icon = (Icon) getAction().getValue(Action.SMALL_ICON);
            if (icon != null) {
                return icon.getIconHeight();
            }
        }
        return super.getHeight();
    }


    @Override
    public int getY() {
        return reference.getY();
    }
}
