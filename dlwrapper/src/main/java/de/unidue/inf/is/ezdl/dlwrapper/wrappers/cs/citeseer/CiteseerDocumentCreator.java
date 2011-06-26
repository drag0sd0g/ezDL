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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.citeseer;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.utils.DocumentCreator;



/**
 * Creates ezDL {@link Document} objects from {@link ToolkitAPI} output for the
 * {@link CiteseerWrapper}.
 * 
 * @author mjordan
 */
public class CiteseerDocumentCreator extends DocumentCreator {

    private static final String AUTHOR_PREFIX = "by ";


    @Override
    protected void handleAuthors(Document doc, Object authorInput) {
        if (authorInput instanceof String) {
            String authors = (String) authorInput;

            int right = 0;
            right = authors.indexOf("—");

            if (right == -1) {
                right = authors.length() - 1;
            }

            final int left = authors.indexOf(AUTHOR_PREFIX) + AUTHOR_PREFIX.length();

            authors = authors.substring(left, right);
            String[] authorArray = authors.split(",");

            handlePersonList(doc, Field.AUTHOR, authorArray);
        }
    }
}
