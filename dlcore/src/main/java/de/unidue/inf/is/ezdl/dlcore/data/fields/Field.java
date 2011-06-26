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

package de.unidue.inf.is.ezdl.dlcore.data.fields;

import java.io.Serializable;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.ReviewList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TermWithWeightList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TwoTupleList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.URLList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.GroupList;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;



public enum Field implements Serializable {

    FIELDCODE_MIXED(-2, null, null, null), //
    FIELDCODE_NONE(-1, null, null, null), //

    AUTHOR_EDITOR(1, null, String.class), //
    TITLE(4, new Comparator<Document>() {

        @Override
        public int compare(Document o1, Document o2) {
            Collator collator = Collator.getInstance();
            return collator.compare(o1.getTitle(), o2.getTitle());
        }
    }, String.class), //

    ISBN(7, null, String.class), //
    ISSN(8, null, String.class), //
    YEAR(31, new Comparator<Document>() {

        @Override
        public int compare(Document o1, Document o2) {
            return ((Integer) o1.getYear()).compareTo(o2.getYear());
        }
    }, Integer.class), //
    BOOKTITLE(36, null, String.class), //
    INSTITUTION(56, null, String.class), //
    ABSTRACT(62, null, String.class), //
    NOTE(63, null, String.class), //
    AUTHOR(1003, new Comparator<Document>() {

        @Override
        public int compare(Document o1, Document o2) {
            PersonList al1 = o1.getAuthorList();
            PersonList al2 = o2.getAuthorList();
            if (al1 != null && al1.size() > 0 && al2 != null && al2.size() > 0) {
                Person a1 = al1.get(0);
                Person a2 = al2.get(0);
                if (a1 != null && a2 != null) {
                    String lastName1 = a1.getLastName();
                    String lastName2 = a2.getLastName();
                    if (lastName1 != null && lastName2 != null) {
                        return lastName1.compareTo(lastName2);
                    }
                }
            }
            return 0;
        }

    }, PersonList.class), //
    SUMMARY(1019, null, String.class), //
    EDITOR(1020, null, String.class), //
    TEXT(1046, null, String.class), //
    CONFERENCE(1073, null, String.class), //
    CLASSIFICATION(1040, null, String.class), //
    NUMBER(1066, null, String.class), //
    LANGUAGE(1095, null, String.class), //
    ADDRESS(1137, null, String.class), //
    REFERENCE(5001, null, String.class), //
    CITATION(5002, null, String.class), //
    COMPONENTS(5003, null, String.class), //
    EXTERN(5005, null, String.class), //
    JOURNAL(5006, null, String.class), //
    MONTH(5007, null, String.class), //
    PAGES(5008, null, String.class), //
    URLS(5010, null, URLList.class), //
    ARTIST(5030, null, String.class), //
    VOLUME(5009, null, String.class), //
    KEY(8000, null, String.class), //

    DOI(9997, null, String.class), //

    CREATORS(8003, null, TwoTupleList.class), //
    PUBLISHER(1018, null, String.class), //
    PRICE(8006, null, String.class), //
    NUMBER_OF_PAGES(8004, null, Integer.class), //
    HEIGHT(8007, null, String.class), //
    LENGTH(8008, null, String.class), //
    WIDTH(8009, null, String.class), //
    WEIGHT(8010, null, String.class), //
    READINGLEVEL(8011, null, String.class), //
    PUBLICATION_DATE(8012, null, String.class), //
    EDITION(8013, null, String.class), //
    DEWEY(8014, null, String.class), //
    BLURBERS(8015, null, TermList.class), //
    DEDICATIONS(8016, null, TermList.class), //
    EPIGRAPHS(8017, null, TermList.class), //
    FIRSTWORDS(8018, null, TermList.class), //
    LASTWORDS(8019, null, TermList.class), //
    QUOTATIONS(8020, null, TermList.class), //
    SERIES(5, null, TermList.class), // 5
    AWARDS(8022, null, TermList.class), //
    PEOPLE(8023, null, TermList.class), //
    PLACES(8024, null, TermList.class), //
    SUBJECTS(8025, null, TermList.class), //
    SIMILAR(8026, null, TermList.class), //
    REVIEWS(8027, null, ReviewList.class), //
    TAGS(8028, null, TermWithWeightList.class), //
    AVERAGE_RATING(8029, new Comparator<Document>() {

        @Override
        public int compare(Document o1, Document o2) {
            Float d1 = (Float) o1.getFieldValue(Field.AVERAGE_RATING);
            Float d2 = (Float) o2.getFieldValue(Field.AVERAGE_RATING);
            if (d1 != null && d2 != null) {
                return d1.compareTo(d2);
            }
            else {
                return 0;
            }
        };
    }, Float.class), //
    REVIEW_COUNT(8030, new Comparator<Document>() {

        @Override
        public int compare(Document o1, Document o2) {
            Integer r1 = (Integer) o1.getFieldValue(Field.REVIEW_COUNT);
            Integer r2 = (Integer) o2.getFieldValue(Field.REVIEW_COUNT);
            if (r1 != null && r2 != null) {
                return r1.compareTo(r2);
            }
            else {
                return 0;
            }
        }
    }, Integer.class), //
    BINDING(8031, null, String.class), //
    LABEL(8032, null, String.class), //
    GROUPS(8033, null, GroupList.class), //
    REFERENCESYSTEMID(8034, null, String.class), //
    REFERENCESYSTEM(8035, null, String.class), //
    RSV(9998, new Comparator<ResultDocument>() {

        @Override
        public int compare(ResultDocument o1, ResultDocument o2) {
            return Double.compare(o1.getRsv(), o2.getRsv());
        }
    }, Number.class, null);

    public static final Set<Field> SORTABLE_FIELDS;

    private static Map<Integer, Field> map = new HashMap<Integer, Field>();

    static {
        Set<Field> sortableFieldsHelp = new HashSet<Field>();
        for (Field field : values()) {
            map.put(field.asInt(), field);
            if (field.isSortable()) {
                sortableFieldsHelp.add(field);
            }
        }
        SORTABLE_FIELDS = Collections.unmodifiableSet(sortableFieldsHelp);
    }

    private int id;
    private Class<?> type;
    private Comparator<Document> comparator;
    private Comparator<ResultDocument> resultComparator;
    private boolean sortable;
    private boolean searchable;


    /**
     * Creates a searchable field.
     */
    private Field(int id, Comparator<Document> comparator, Class<?> type) {
        this.id = id;
        this.type = type;
        this.comparator = comparator;
        this.resultComparator = new Comparator<ResultDocument>() {

            @Override
            public int compare(ResultDocument o1, ResultDocument o2) {
                return Field.this.comparator.compare(o1.getDocument(), o2.getDocument());
            }
        };
        this.sortable = comparator != null;
        this.searchable = true;
    }


    /**
     * Creates a non-searchable field.
     */
    private Field(int id, Comparator<ResultDocument> resultComparator, Class<?> type, Void thisParameterHasNoMeaning) {
        this.id = id;
        this.type = type;
        this.resultComparator = resultComparator;
        this.sortable = resultComparator != null;
    }


    @Override
    public String toString() {
        return Integer.toString(id);
    }


    /**
     * @return Returns the field's int value
     */
    public int asInt() {
        return id;
    }


    /**
     * @return the comparator for {@link Document}s
     */
    public Comparator<Document> getComparator() {
        return comparator;
    }


    /**
     * @return the comparator for {@link ResultDocument}s
     */
    public Comparator<ResultDocument> getResultComparator() {
        return resultComparator;
    }


    /**
     * @return if this field is sortable
     */
    public boolean isSortable() {
        return sortable;
    }


    /**
     * @return if this field is searchable (e.g. RSV is <b>not</b> searchable)
     */
    public boolean isSearchable() {
        return searchable;
    }


    public boolean isPermittedType(Class<?> type) {
        return this.type == null || this.type.isAssignableFrom(type);
    }


    public static Field fromString(String str) {
        int i = Integer.parseInt(str);
        Field w = map.get(i);
        return w;
    }

}
