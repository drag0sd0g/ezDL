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

package de.unidue.inf.is.ezdl.dlfrontend.helper;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlfrontend.query.NoSuchFieldCodeException;



/**
 * Registry for field codes and their clear text.
 * <p>
 * Used by Parser, Form and Results Display.
 */
public final class FieldRegistry {

    private static FieldRegistry instance = null;


    /**
     * Keeps records of field information together.
     */
    public class FieldInfo {

        /**
         * The field code of the field.
         */
        public Field code;
        /**
         * The name of the field.
         */
        public String i18nKey;
        /**
         * The term usable in a text-based parser to refer to this field.
         */
        public String parserWord;


        /**
         * Constructor.
         * 
         * @param code
         *            the internal code of the field
         * @param i18nKey
         *            the key for the i18n'd label if the field (e.g.
         *            field.author)
         * @param parserWord
         *            the term usable in a text-based parser to refer to this
         *            field
         */
        public FieldInfo(Field code, String i18nKey, String parserWord) {
            this.code = code;
            this.i18nKey = i18nKey;
            this.parserWord = parserWord;
        }


        /**
         * Just for debugging
         */
        @Override
        public String toString() {
            return i18nKey + "(" + code.toString() + "): " + parserWord;
        }
    }


    private class FieldInfoCollisionException extends IllegalArgumentException {

        /**
         * 
         */
        private static final long serialVersionUID = -7565855356095603037L;


        public FieldInfoCollisionException(String message) {
            super(message);
        }
    }


    /**
     * Stores information about input field contexts like the internally used
     * WrapperConstant or the key for its i18n'd label. While a normal map maps
     * one value to one other value, this is a map that maps one of multiple
     * values to all values of a tuple.
     * 
     * @author mj
     */
    private class FieldMap {

        /**
         * Keeps FieldInfo objects by their WrapperConstant code.
         */
        private Map<Field, FieldInfo> byCode = new EnumMap<Field, FieldInfo>(Field.class);
        /**
         * Keeps FieldInfo objects by their i18n label key.
         */
        private Map<String, FieldInfo> byi18nKey = new HashMap<String, FieldInfo>();
        /**
         * Keeps FieldInfo objects by their parser word.
         */
        private Map<String, FieldInfo> byParserWord = new HashMap<String, FieldInfo>();


        public void put(Field code, String i18nKey, String parserWord) throws FieldInfoCollisionException {
            if (byCode(code) != null) {
                throw new FieldInfoCollisionException("Field already registered by code: " + code.toString());
            }

            if (byi18nKey(i18nKey) != null) {
                throw new FieldInfoCollisionException("Field already registered by i18n: " + i18nKey);
            }

            if (byParserWord(parserWord) != null) {
                throw new FieldInfoCollisionException("Field already registered by parser word: " + parserWord);
            }

            FieldInfo fi = new FieldInfo(code, i18nKey, parserWord);
            byCode.put(code, fi);
            byi18nKey.put(i18nKey, fi);
            byParserWord.put(parserWord, fi);
        }


        /**
         * Returns field information by the code of the field.
         * 
         * @param code
         *            the code (e.g. WrapperConstants.AUTHOR)
         * @return
         */
        public FieldInfo byCode(Field code) {
            return byCode.get(code);
        }


        /**
         * Returns field information by its i18n label.
         * 
         * @param key
         *            the i18n label of the field.
         * @return
         */
        public FieldInfo byi18nKey(String key) {
            return byi18nKey.get(key);
        }


        /**
         * Returns field information by its parser word.
         * 
         * @param key
         *            the parser word of the field (e.g. "Author" as in
         *            "Author=Fuhr").
         * @return
         */
        public FieldInfo byParserWord(String parserWord) {
            return byParserWord.get(parserWord);
        }

    }


    /**
     * The map of field information objects.
     */
    private FieldMap map;


    /**
     * Constructor.
     */
    private FieldRegistry() {
        map = new FieldMap();
        initFieldNames();
    }


    /**
     * Returns the FieldRegistry object.
     * 
     * @return the field registry
     */
    public static synchronized FieldRegistry getInstance() {
        if (instance == null) {
            instance = new FieldRegistry();
        }
        return instance;
    }


    /**
     * Returns the field name of a field by its code.
     * 
     * @param fieldcode
     *            the internally used code.
     * @return the
     * @throws NoSuchFieldCodeException
     */
    public String getFieldName(Field fieldcode) throws NoSuchFieldCodeException {
        FieldInfo fi = map.byCode(fieldcode);
        if (fi == null) {
            throw new NoSuchFieldCodeException("Code: " + fieldcode);
        }
        return fi.i18nKey;
    }


    /**
     * Returns the field code of a field by its i18n key.
     * 
     * @param key
     *            the i18n key of the field (e.g. "field.author")
     * @return the internally used field code
     * @throws NoSuchFieldCodeException
     */
    public Field getFieldCode(String key) throws NoSuchFieldCodeException {
        FieldInfo fi = map.byi18nKey(key);
        if (fi == null) {
            throw new NoSuchFieldCodeException(key);
        }
        return fi.code;
    }


    /**
     * Returns the parser word of a field by its code.
     * 
     * @param fieldcode
     *            the internally used code.
     * @return the word used to refer to that field in a text-based query
     * @throws NoSuchFieldCodeException
     */
    public String getParserWord(Field fieldcode) throws NoSuchFieldCodeException {
        FieldInfo fi = map.byCode(fieldcode);
        if (fi == null) {
            throw new NoSuchFieldCodeException("Code: " + fieldcode);
        }
        return fi.parserWord;
    }


    /**
     * Returns the field code of a field by its parser word.
     * 
     * @param fieldcode
     *            the internally used code.
     * @return the internally used field code
     * @throws NoSuchFieldCodeException
     */
    public Field getFieldNumberByParserWord(String parserWord) throws NoSuchFieldCodeException {
        FieldInfo fi = map.byParserWord(parserWord);
        if (fi == null) {
            throw new NoSuchFieldCodeException(parserWord);
        }
        return fi.code;
    }


    /**
     * Initializes the field information.
     */
    private void initFieldNames() {
        try {
            map.put(Field.TITLE, "field.title", "Title");
            map.put(Field.AUTHOR_EDITOR, "field.author-editor", "AuEd");
            map.put(Field.AUTHOR, "field.author", "Author");
            map.put(Field.YEAR, "field.year", "Year");
            map.put(Field.PUBLISHER, "field.publisher", "Publisher");
            map.put(Field.ISBN, "field.isbn", "ISBN");
            map.put(Field.TEXT, "field.text", "Text");
            map.put(Field.CLASSIFICATION, "field.classification", "Classification");
            map.put(Field.URLS, "field.url", "URL");
            map.put(Field.REFERENCE, "field.reference", "Reference");
            map.put(Field.CITATION, "field.citation", "Citation");
            map.put(Field.ABSTRACT, "field.abstract", "Abstract");
            map.put(Field.CONFERENCE, "field.conference", "Conference");
            map.put(Field.JOURNAL, "field.journal", "Journal");

            map.put(Field.RSV, "field.rsv", "RSV");

            // for media search
            map.put(Field.ARTIST, "field.artist", "Artist");

            map.put(Field.ADDRESS, "address", "address");
            map.put(Field.BOOKTITLE, "booktitle", "booktitle");
            map.put(Field.COMPONENTS, "components", "components");
            map.put(Field.EDITION, "edition", "edition");
            map.put(Field.EDITOR, "editor", "editor");
            map.put(Field.EXTERN, "extern", "extern");
            map.put(Field.INSTITUTION, "institution", "institution");
            map.put(Field.ISSN, "issn", "issn");
            map.put(Field.LANGUAGE, "language", "language");
            map.put(Field.MONTH, "month", "month");
            map.put(Field.NOTE, "note", "note");
            map.put(Field.NUMBER, "number", "number");
            map.put(Field.PAGES, "pages", "pages");
            map.put(Field.SERIES, "series", "series");
            map.put(Field.SUMMARY, "summary", "summary");
            map.put(Field.VOLUME, "volume", "volume");
        }
        catch (FieldInfoCollisionException e) {
            e.printStackTrace();
        }
    }

}
