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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.cs.ieee;

import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;
import de.unidue.inf.is.ezdl.dlwrapper.utils.DocumentCreator;



/**
 * Creates ezDL {@link Document} objects from {@link ToolkitAPI} output for the
 * {@link IEEEWrapper}.
 * 
 * @author mjordan
 */
public class IEEEDocumentCreator extends DocumentCreator {

    /**
     * The base URL of the IEEE site.
     */
    private static final String IEEE_BASE_URL = "http://www.ieeexplore.ieee.org";


    @Override
    public Document createDocumentFromMap(Map<String, Object> docInfoMap) {
        Document td = super.createDocumentFromMap(docInfoMap);
        String details = (String) docInfoMap.get("metadata");

        setDetails(td, details);

        // td.setAbstract(((String) ht.get(HT_KEY_ABSTRACT)).trim());

        getLogger().debug("Document: " + td);
        return td;

    }


    @Override
    protected String getDetailUrlPrefix() {
        return IEEE_BASE_URL;
    }


    @Override
    protected void handleDetailLink(Document doc, Object detailLink) {
        super.handleDetailLink(doc, detailLink);
    }


    private void setDetails(Document td, String details) {
        String[] lines = details.split("\n");
        Field nextField = Field.AUTHOR;

        for (String line : lines) {
            final String trimmed = line.trim();
            if (!StringUtils.isEmpty(trimmed)) {
                getLogger().debug(trimmed);
                if (nextField != null) {
                    switch (nextField) {
                        case AUTHOR: {
                            final PersonList authors = getAuthorList(trimmed);
                            td.setAuthorList(authors);
                            nextField = null;
                            break;
                        }
                        case DOI: {
                            td.setFieldValue(Field.DOI, trimmed);
                            nextField = null;
                            break;
                        }
                        case YEAR: {
                            td.setYear(Integer.parseInt(trimmed));
                            nextField = null;
                            break;
                        }
                        case PAGES: {
                            td.setFieldValue(Field.PAGES, trimmed);
                            nextField = null;
                            break;
                        }
                    }
                }

                if ("Digital Object Identifier:".equals(trimmed)) {
                    nextField = Field.DOI;
                }
                else if ("Publication Year:".equals(trimmed)) {
                    nextField = Field.YEAR;
                }
            }
        }
    }


    PersonList getAuthorList(final String trimmed) {
        final PersonList authors = new PersonList();
        String[] authorStrs = trimmed.split(";");
        for (String authorStr : authorStrs) {
            String[] parts = authorStr.split(",");
            if (parts.length == 1) {
                final String name = parts[0].trim();
                Person p = new Person(name);
                authors.add(p);
            }
            else if (parts.length == 2) {
                final String firstname = parts[1].trim();
                final String lastname = parts[0].trim();
                Person p = new Person(firstname, lastname);
                authors.add(p);
            }
        }
        return authors;
    }

}
