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

package de.unidue.inf.is.ezdl.dlbackend.data;

import java.util.LinkedList;
import java.util.List;

import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;



/**
 * Converts between lists of {@link ResultDocument} and {@link StoredDocument}.
 * 
 * @author mjordan
 */
public class DocumentListConverter {

    /**
     * Repacks a {@link StoredDocumentList} into a list of
     * {@link ResultDocument} objects that all have RSV 0.0.
     * 
     * @param results
     *            the results to repack
     * @return the repacked results
     */
    public static ResultDocumentList toResultDocumentList(StoredDocumentList results) {
        ResultDocumentList out = new ResultDocumentList();
        for (StoredDocument result : results) {
            Document doc = result.getDocument();
            ResultDocument res = new ResultDocument(doc);
            List<String> sources = convertSources(result);
            res.addSources(sources);
            out.add(res);
        }
        return out;
    }


    // TODO
    private static List<String> convertSources(StoredDocument result) {
        List<String> sources = new LinkedList<String>();
        for (SourceInfo sourceInfo : result.getSources()) {
            sources.add(sourceInfo.getSourceID().getDL());
        }
        return sources;
    }

}
