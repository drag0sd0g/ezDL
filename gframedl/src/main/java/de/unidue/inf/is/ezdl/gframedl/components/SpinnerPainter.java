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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jdesktop.swingx.painter.BusyPainter;



/**
 * A painter which draw a spinner animation.
 * 
 * @author markus
 */

public class SpinnerPainter extends BusyPainter {

    @Override
    protected void doPaint(Graphics2D g, Object object, int width, int height) {
        // centralize the effect
        Rectangle r = getTrajectory().getBounds();
        int tw = width - r.width - 2 * r.x;
        int th = height - r.height - 2 * r.y;
        g.translate(tw / 2, th / 2);
        super.doPaint(g, object, width, height);
    }
}
