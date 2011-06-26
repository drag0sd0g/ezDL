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

package de.unidue.inf.is.ezdl.gframedl.tools.extraction.views;

import javax.swing.JScrollPane;

import de.unidue.inf.is.ezdl.dlcore.data.extractor.ExtractionResult;



/**
 * Defines that every extraction view has to implement a method that handles the
 * display of an {@link ExtractionResult}.
 * 
 * @author tacke
 */
public abstract class AbstractExtractionView extends JScrollPane {

    private static final long serialVersionUID = -5384182049063680832L;


    /**
     * Display an {@link ExtractionResult}
     * 
     * @param results
     */
    public abstract void displayData(ExtractionResult results);


    /**
     * Extraction views have to overload the toString method and return their
     * Description. For Example: "Display as Table".
     */
    @Override
    public abstract String toString();

}
