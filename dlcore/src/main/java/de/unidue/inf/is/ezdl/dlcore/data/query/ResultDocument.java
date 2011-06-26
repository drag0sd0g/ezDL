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

package de.unidue.inf.is.ezdl.dlcore.data.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;



/**
 * ResultDocument is an envelope around a raw {@link Document} object so that
 * additional information in the context of a search can be stored. E.g. the
 * RSV.
 * 
 * @author mj
 */
public final class ResultDocument implements Mergeable, Serializable {

    private static final long serialVersionUID = 6109504532274882532L;

    /**
     * The document that the other fields of ResultDocument are about.
     */
    private Document document;
    /**
     * Retrieval status value (between 0.0 and 1.0).
     */
    private double rsv;
    /**
     * Retrieval status value (unnormalized).
     */
    private double unnormalizedRsv;
    /**
     * The set of sources that found information about the document.
     */
    private Set<String> sources;


    /**
     * Constructor.
     * <p>
     * Creates a new ResultDocument object with the given document, an RSV of
     * 0.0 and an empty list of sources.
     * 
     * @param document
     *            the document
     */
    public ResultDocument(Document document) {
        this.document = document;
        this.rsv = 0.0;
        this.sources = new HashSet<String>();
    }


    /**
     * Returns the Retrieval Status Value.
     * 
     * @return the RSV
     */
    public double getRsv() {
        return rsv;
    }


    /**
     * Returns the unnormalized rsv.
     * 
     * @return the unnormalized rsv
     */
    public double getUnnormalizedRsv() {
        return unnormalizedRsv;
    }


    /**
     * Sets the Retrieval Status Value.
     * <p>
     * If the RSV submitted is not in the range between 0 and 1, an
     * {@link IllegalArgumentException} is thrown.
     * 
     * @param rsv
     *            the RSV to set
     */
    public void setRsv(double rsv) {
        if ((rsv < 0.0) || (rsv > 1)) {
            throw new IllegalArgumentException("The RSV must be between 0 and 1");
        }
        this.rsv = rsv;
    }


    /**
     * Sets the unnormalized rsv.
     * 
     * @param unnormalizedRsv
     *            the unnormalized rsv
     */
    public void setUnnormalizedRsv(double unnormalizedRsv) {
        this.unnormalizedRsv = unnormalizedRsv;
    }


    /**
     * @param field
     *            the field to retrieve the value for
     * @return the value of the field in the document
     */
    public Object getFieldValue(Field field) {
        return document.getFieldValue(field);
    }


    /**
     * Sets the value of a field in the document.
     * 
     * @param field
     *            the field whose value to set
     * @param value
     *            the value to set
     */
    public void setFieldValue(Field field, Object value) {
        document.setFieldValue(field, value);
    }


    public void clearFieldValue(Field field) {
        document.clearFieldValue(field);
    }


    /**
     * Adds a source to the document.
     * 
     * @param source
     *            the source to add.
     */
    public void addSource(String source) {
        sources.add(source);
    }


    /**
     * Adds sources to the document.
     * 
     * @param sources
     *            the sources to add.
     */
    public void addSources(Collection<String> source) {
        sources.addAll(source);
    }


    /**
     * @return the sources of the document which might be an empty list but is
     *         never null.
     */
    public Set<String> getSources() {
        return sources;
    }


    /**
     * Clears the sources.
     */
    public void clearSources() {
        sources.clear();
    }


    public Document getDocument() {
        return document;
    }


    public String getOid() {
        return document.getOid();
    }


    public void setOid(String oid) {
        document.setOid(oid);
    }


    @Override
    public boolean isSimilar(Mergeable other) {
        document.isSimilar(other);
        return false;
    }


    @Override
    public void merge(Mergeable other) {
        document.merge(other);

        if (other instanceof ResultDocument) {
            ResultDocument otherRes = (ResultDocument) other;
            Set<String> sources = getSources();
            for (String otherSource : otherRes.getSources()) {
                if (!sources.contains(otherSource)) {
                    sources.add(otherSource);
                }
            }
        }
    }


    @Override
    public String toString() {
        String longDouble = Double.toString(rsv);
        int maxIndex = Math.min(4, longDouble.length() - 1);
        String rsvStr = longDouble.substring(0, maxIndex);
        return "{ResultDocument " + rsvStr + " " + sources + " " + document + "}";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rsv);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        ResultDocument other = (ResultDocument) obj;
        if (document == null) {
            if (other.document != null) {
                return false;
            }
        }
        else if (!document.equals(other.document)) {
            return false;
        }
        if (Double.doubleToLongBits(rsv) != Double.doubleToLongBits(other.rsv)) {
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
