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

package de.unidue.inf.is.ezdl.gframedl.tools.search.resultList;

import de.unidue.inf.is.ezdl.gframedl.components.dynamiclist.ListCellRendererFactory;
import de.unidue.inf.is.ezdl.gframedl.tools.search.ResultItem;
import de.unidue.inf.is.ezdl.gframedl.tools.search.resultList.renderers.TextDocumentCellRenderer;



/**
 * This class contains a list of renderer's for different result types in the
 * ResultList.
 * 
 * @author RT
 */
public class ResultListRendererFactory extends ListCellRendererFactory {

    /**
     * In case of grouping there are multiple instances of the ResultList. They
     * share this instance.
     */
    public static ResultListRendererFactory sharedInstance = new ResultListRendererFactory();


    public ResultListRendererFactory() {
        super();
        addRenderer(ResultItem.class, new TextDocumentCellRenderer());
    }

}
