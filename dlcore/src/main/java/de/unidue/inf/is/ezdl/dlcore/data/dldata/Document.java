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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;
import de.unidue.inf.is.ezdl.dlcore.utils.merge.MergeUtils;



/**
 * "What's metadata to you, is someone else's fundamental data." (Ralph Swick).
 */
public abstract class Document extends AbstractDLObject {

    private static final long serialVersionUID = -4347509956016642262L;

    /**
     * Integer constant that designates an invalid entry for the year record.
     */
    public static final int YEAR_INVALID = Integer.MIN_VALUE;

    /**
     * The object ID that this item received when it was first put into the
     * system.
     */
    private String oid;
    /**
     * Map with objects for the {@link Field}s
     */
    private Map<Field, Object> fieldMap = new EnumMap<Field, Object>(Field.class);


    /**
     * Constructor.
     */
    public Document() {
        super();
    }


    /**
     * Checks if the object has enough data in it.
     * <p>
     * Documents are invalid only if no authors are stored and if the title is
     * empty.
     * 
     * @return true, if the object is valid. Else false.
     * @see #isComplete()
     */
    public boolean isValid() {
        final String title = getTitle();
        if (StringUtils.isEmpty(title)) {
            return false;
        }

        final PersonList al = getAuthorList();
        if (al == null || al.size() == 0) {
            return false;
        }
        return true;
    }


    /**
     * Returns if the document has data in all its fields.
     * 
     * @return true, if the document is complete. Else false.
     * @see #isValid()
     */
    public boolean isComplete() {
        if (!isValid()) {
            return false;
        }

        if (getYear() == YEAR_INVALID) {
            return false;
        }

        return true;
    }


    @Override
    public final void merge(Mergeable other) {
        if (other instanceof Document) {
            Document otherDoc = (Document) other;
            for (Entry<Field, Object> otherFMEntry : otherDoc.fieldMap.entrySet()) {
                final Field key = otherFMEntry.getKey();
                final Object otherValue = otherFMEntry.getValue();
                final Object ourValue = fieldMap.get(key);

                final boolean otherDocContainsKey = otherDoc.fieldMap.containsKey(key);
                final boolean ourFieldIsEmpty = empty(ourValue);

                if (!otherDocContainsKey || ourFieldIsEmpty) {
                    fieldMap.put(key, otherValue);
                }
                else if (otherValue != null) {
                    if ((ourValue instanceof Mergeable) && (otherValue instanceof Mergeable)) {
                        ((Mergeable) ourValue).merge((Mergeable) otherValue);
                    }
                    else if (key == Field.URLS) {
                        ((URLList) ourValue).merge(otherValue);
                    }
                    else {
                        MergeUtils.merge(ourValue, otherValue);
                    }
                }
            }
        }

        MergeUtils.merge(this, other);
    }


    private boolean empty(final Object ourValue) {
        boolean empty = (ourValue == null);
        if (!empty && (ourValue instanceof String)) {
            empty = StringUtils.isEmpty((String) ourValue);
        }
        return empty;
    }


    /**
     * Returns the list of persons who was involved in creating the document.
     * 
     * @return the list of authors
     */
    public PersonList getAuthorList() {
        return (PersonList) getFieldValue(Field.AUTHOR);
    }


    public void setAuthorList(PersonList authorList) {
        setFieldValue(Field.AUTHOR, authorList);
    }


    public String getTitle() {
        return (String) getFieldValue(Field.TITLE);
    }


    public void setTitle(String title) {
        setFieldValue(Field.TITLE, title);
    }


    public int getYear() {
        Integer year = (Integer) getFieldValue(Field.YEAR);
        if (year == null) {
            return YEAR_INVALID;
        }
        return year;
    }


    public void setYear(int year) {
        setFieldValue(Field.YEAR, year);
    }


    // TODO: Should probably use lazy instantiation code that is currently in
    // addDetailURL.
    public URLList getDetailURLs() {
        return (URLList) getFieldValue(Field.URLS);
    }


    public void setDetailURLs(URLList detaillinks) {
        setFieldValue(Field.URLS, detaillinks);
    }


    public void addDetailURL(URL url) {
        URLList list = getDetailURLs();
        if (list == null) {
            list = new URLList();
            setDetailURLs(list);
        }
        list.add(url);
    }


    /**
     * Returns the value of the given field.
     * 
     * @param field
     *            the field whose value is to be returned
     * @return the value of the field
     * @throws IllegalArgumentException
     *             if the given field is not searchable and, thus, not stored in
     *             the document
     * @see Field#isSearchable()
     */
    public Object getFieldValue(Field field) {
        if (field.isSearchable()) {
            return fieldMap.get(field);
        }
        else {
            throw new IllegalArgumentException(field + " is not searchable!");
        }
    }


    public void setFieldValue(Field field, Object value) {
        if (field.isSearchable()) {
            if (value != null && !field.isPermittedType(value.getClass())) {
                throw new IllegalArgumentException(value.getClass() + " is not a permitted type for field " + field
                                + "!");
            }
            fieldMap.put(field, value);
        }
        else {
            throw new IllegalArgumentException(field + " is not searchable!");
        }
    }


    /**
     * Removes a field from the document.
     * 
     * @param field
     *            the field to remove
     */
    public void clearFieldValue(Field field) {
        if (field.isSearchable()) {
            fieldMap.remove(field);
        }
        else {
            throw new IllegalArgumentException(field + " is not searchable!");
        }
    }


    /**
     * Returns the fields that have a value in the map.
     * 
     * @return the field map's keys
     */
    public Set<Field> getFields() {
        return fieldMap.keySet();
    }


    @Override
    public String getOid() {
        return oid;
    }


    /**
     * Sets the object ID of the document.
     * 
     * @param oid
     *            the new object ID. If null, an
     *            {@link IllegalArgumentException} is thrown.
     */
    public void setOid(String oid) {
        if (oid == null) {
            throw new IllegalArgumentException("oid must not be null");
        }
        this.oid = oid;
    }


    @Override
    public String asString() {
        StringBuffer out = new StringBuffer();
        out.append(getAuthorList());
        out.append(": ''");
        out.append(getTitle());
        out.append("'' ");
        out.append('(').append(getYear()).append(')');
        return out.toString();
    }


    @Override
    public boolean isSimilar(Mergeable obj) {
        if (obj instanceof Document) {
            Document doc = (Document) obj;
            return (oid != null) && oid.equals(doc.getOid());
        }
        return false;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldMap == null) ? 0 : fieldMap.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (empty(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Document other = (Document) obj;
        if (fieldMap == null) {
            if (other.fieldMap != null) {
                return false;
            }
        }
        else if (!fieldMap.equals(other.fieldMap)) {
            return false;
        }
        return true;
    }

}
