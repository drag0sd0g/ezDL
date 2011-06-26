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

import net.infonode.docking.util.ViewMap;
import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;



/**
 * An admin perspective for user administration.
 */
final class AdminPerspective extends AbstractPerspective {

    @Override
    protected ViewMap createViewMap() {
        ViewMap viewMap = new ViewMap();
        return viewMap;
    }


    @Override
    protected void layoutRootWindow() {
    }


    @Override
    public String getName() {
        return I18nSupport.getInstance().getLocString("desktop.perspective.name.admin");
    }

}
