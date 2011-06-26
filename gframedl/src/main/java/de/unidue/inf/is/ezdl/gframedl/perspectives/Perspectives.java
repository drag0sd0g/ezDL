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

package de.unidue.inf.is.ezdl.gframedl.perspectives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.security.Privilege;
import de.unidue.inf.is.ezdl.dlfrontend.security.SecurityInfo;
import de.unidue.inf.is.ezdl.gframedl.ToolController;



/**
 * Helper class with all available perspectives.
 */
public final class Perspectives {

    private static List<Perspective> perspectives;


    private Perspectives() {
    }


    private static void init() {
        SecurityInfo securityInfo = ToolController.getInstance().getSecurityInfo();
        List<Perspective> p = new ArrayList<Perspective>();
        p.add(new DefaultPerspective());
        p.add(new SimpleSearchPerspective());
        if (securityInfo.check(Privilege.ADMINISTER_USERS)) {
            p.add(new AdminPerspective());
        }
        perspectives = Collections.unmodifiableList(p);
    }


    /**
     * Returns the perspectives.
     */
    public static List<Perspective> getPerspectives() {
        if (perspectives == null) {
            init();
        }
        return perspectives;
    }


    /**
     * Returns a perspective that has a certain class name.
     * 
     * @param perspectiveClassName
     *            The class name of a perspective
     * @return the perspective that has the specified class name
     */
    public static Perspective perspectiveForClassName(String perspectiveClassName) {
        for (Perspective perspective : Perspectives.getPerspectives()) {
            if (perspective.getClass().getName().equals(perspectiveClassName)) {
                return perspective;
            }
        }
        return null;
    }
}
