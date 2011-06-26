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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * StoredDocument is used to keep track of internal information about documents.
 * <p>
 * Important parts are the lists of sources (e.g. digital libraries) that
 * contributed to the contained document's data and the list of those sources
 * that were kindly asked but were not able to contribute information.
 * 
 * @author mj
 */
public class StoredDocument implements Mergeable, Serializable {

    private static final long serialVersionUID = 3987239217089450803L;

    /**
     * The document that the other fields of StoredDocument are about.
     */
    private Document document;

    /**
     * Information about the sources of the object's data. (E.g. wrappers that
     * found something.)
     */
    private Set<SourceInfo> sources = new HashSet<SourceInfo>();
    /**
     * Information about those sources that failed to contribute to the object's
     * data. (E.g. wrappers that didn't find anything.)
     */
    private Set<SourceInfo> misses = new HashSet<SourceInfo>();


    /**
     * Constructor.
     * <p>
     * Creates a {@link StoredDocument} object with the given document and empty
     * sources and misses lists.
     * 
     * @param document
     *            the document to include. The reference must not be null.
     */
    public StoredDocument(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }
        this.document = document;
    }


    /**
     * Returns the raw document.
     * 
     * @return the document itself and nothing but the document. This is never
     *         null.
     */
    public Document getDocument() {
        return document;
    }


    /**
     * @return the object ID of the contained document
     */
    public String getOid() {
        return document.getOid();
    }


    /**
     * Adds a source to the object.
     * 
     * @param source
     *            the source to add
     */
    public void addSource(SourceInfo source) {
        if (source != null) {
            sources.add(source);
        }
    }


    /**
     * @return the sources
     */
    public Set<SourceInfo> getSources() {
        return sources;
    }


    /**
     * Clears the sources.
     */
    public void clearSources() {
        sources.clear();
    }


    /**
     * Adds a miss. Misses are (in our context) sources that were asked for
     * details to the document but did not return anything, e.g. because of a
     * timeout.
     * 
     * @param miss
     *            the miss to add
     */
    public void addMiss(SourceInfo miss) {
        if (miss != null) {
            misses.add(miss);
        }
    }


    /**
     * Sets the set of misses.
     * 
     * @param misses
     *            the new set of misses
     */
    public void setMisses(Set<SourceInfo> misses) {
        if (misses != null) {
            this.misses = misses;
        }
    }


    /**
     * @return the misses
     */
    public Set<SourceInfo> getMisses() {
        return misses;
    }


    /**
     * Clear the misses.
     */
    public void clearMisses() {
        misses.clear();
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof StoredDocument) {
            StoredDocument otherStDoc = (StoredDocument) obj;
            return document.isSimilar(otherStDoc.getDocument());
        }
        return false;
    }


    @Override
    public void merge(Mergeable other) {
        if (other instanceof StoredDocument) {
            StoredDocument otherStDoc = (StoredDocument) other;
            document.merge(otherStDoc.getDocument());

            mergeSourceInfos(otherStDoc);
            misses.addAll(otherStDoc.getMisses());
        }
    }


    private void mergeSourceInfos(StoredDocument otherStDoc) {
        final Set<SourceInfo> otherSources = otherStDoc.sources;
        Set<SourceInfo> nonNullTimestampSourecs = findNonNullDetailTimestamps(otherSources);
        sources.removeAll(nonNullTimestampSourecs);
        sources.addAll(nonNullTimestampSourecs);
    }


    private Set<SourceInfo> findNonNullDetailTimestamps(final Set<SourceInfo> otherSources) {
        Set<SourceInfo> nonNullTimestampSourecs = new HashSet<SourceInfo>();
        for (SourceInfo otherSource : otherSources) {
            if (otherSource.getDetailTimestamp() != null) {
                nonNullTimestampSourecs.add(otherSource);
            }
        }
        return nonNullTimestampSourecs;
    }


    @Override
    public String toString() {
        return "{StoredDocument from " + sources + " missed: " + misses + " doc: " + document + "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((misses == null) ? 0 : misses.hashCode());
        result = prime * result + ((sources == null) ? 0 : sources.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StoredDocument other = (StoredDocument) obj;
        if (document == null) {
            if (other.document != null) {
                return false;
            }
        }
        else if (!document.equals(other.document)) {
            return false;
        }
        if (misses == null) {
            if (other.misses != null) {
                return false;
            }
        }
        else if (!misses.equals(other.misses)) {
            return false;
        }
        if (sources == null) {
            if (other.sources != null) {
                return false;
            }
        }
        else if (!sources.equals(other.sources)) {
            return false;
        }
        return true;
    }

}
