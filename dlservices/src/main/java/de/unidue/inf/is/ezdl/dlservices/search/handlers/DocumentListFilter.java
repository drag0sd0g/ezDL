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

package de.unidue.inf.is.ezdl.dlservices.search.handlers;

import java.util.Collections;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Sorting;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultConfiguration;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;



/**
 * Filters and sorts a {@link DocumentList} according to a given
 * {@link ResultConfiguration}.
 * 
 * @author mj
 */
public class DocumentListFilter {

    /**
     * The configuration to apply.
     */
    private ResultConfiguration resultConfig;


    /**
     * Creates a new filter with the given configuration.
     * 
     * @param resultConfig
     *            the configuration to apply to the list
     */
    public DocumentListFilter(ResultConfiguration resultConfig) {
        super();
        this.resultConfig = resultConfig;
    }


    /**
     * Applies the filter configuration to the list.
     * <p>
     * The output is first sorted, then the interesting interval is cut out. The
     * remaining data is then filtered to contain only the fields given in the
     * configuration.
     * 
     * @param list
     *            the list to process. The list stays unchanged.
     * @return the filtered list
     */
    public ResultDocumentList process(ResultDocumentList list) {
        ResultDocumentList listCopy = new ResultDocumentList(list);

        ResultDocumentList sortedList = sortList(listCopy);
        ResultDocumentList slicedList = sliceList(sortedList);
        ResultDocumentList filteredList = filterFields(slicedList);
        return filteredList;
    }


    /**
     * Filters the information inside of documents in the given list. Only
     * information consistent with the given field list is returned.
     * 
     * @param list
     *            the list to filter
     * @return the filtered list, which is a reference to the list passed as a
     *         parameter
     */
    ResultDocumentList filterFields(ResultDocumentList list) {
        ResultDocumentList out = new ResultDocumentList();

        for (ResultDocument data : list) {
            ResultDocument copy = fieldlessCopy(data);
            out.add(copy);
            for (Field field : Field.values()) {
                boolean fieldWanted = resultConfig.getFields().contains(field);
                boolean searchable = field.isSearchable();
                if (fieldWanted && searchable) {
                    copy.setFieldValue(field, data.getDocument().getFieldValue(field));
                }
            }
        }

        return out;
    }


    private ResultDocument fieldlessCopy(ResultDocument data) {
        Document doc;

        try {
            doc = data.getDocument().getClass().newInstance();
        }
        catch (InstantiationException e) {
            return null;
        }
        catch (IllegalAccessException e) {
            return null;
        }

        doc.setOid(data.getDocument().getOid());

        ResultDocument copy = new ResultDocument(doc);
        copy.setRsv(data.getRsv());
        copy.addSources(data.getSources());

        return copy;
    }


    /**
     * Sorts the list using the given configuration.
     * 
     * @param list
     *            the list
     * @return the sorted list
     */
    ResultDocumentList sortList(ResultDocumentList list) {

        for (Sorting sorting : resultConfig.getSortings()) {
            if (sorting != null) {
                Collections.sort(list, sorting.getResultComparator());
            }
        }

        ResultDocumentList sortedList = new ResultDocumentList();
        for (ResultDocument data : list) {
            sortedList.add(data);
        }
        return sortedList;
    }


    /**
     * Returns a slice (interval) of the list.
     * 
     * @param list
     *            the list to slice
     * @return the sliced list
     */
    ResultDocumentList sliceList(ResultDocumentList list) {
        ResultDocumentList slicedList = new ResultDocumentList();
        int startNo = resultConfig.getStartDocNumber();
        int endNo = resultConfig.getEndDocNumber();
        if (endNo == ResultConfiguration.INF_DOCS) {
            endNo = Integer.MAX_VALUE;
        }

        int pos = 0;
        for (ResultDocument data : list) {
            if (startNo <= pos) {
                slicedList.add(data);
            }
            if (pos == endNo) {
                break;
            }
            pos++;
        }
        return slicedList;
    }
}
