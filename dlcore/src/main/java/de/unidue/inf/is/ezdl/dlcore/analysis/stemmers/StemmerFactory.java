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

package de.unidue.inf.is.ezdl.dlcore.analysis.stemmers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;



/**
 * This factory is responsible for creating {@link Stemmer} objects.
 */
public final class StemmerFactory {

    private static Logger logger = Logger.getLogger(StemmerFactory.class);

    private static final Map<Locale, Class<? extends Stemmer>> STEMMERS;
    static {
        Map<Locale, Class<? extends Stemmer>> stemmers = new HashMap<Locale, Class<? extends Stemmer>>();
        stemmers.put(Locale.GERMAN, GermanStemmer.class);
        stemmers.put(Locale.ENGLISH, EnglishStemmer.class);
        STEMMERS = Collections.unmodifiableMap(stemmers);
    }


    private StemmerFactory() {
    }


    /**
     * Returns the stemmer for the supplied locale or <code>null</code> if no
     * appropriate stemmer is available.
     * 
     * @param locale
     *            the locale of the stemmer
     * @return the stemmer for the supplied locale or <code>null</code> if no
     *         appropriate stemmer is available
     */
    public static Stemmer newStemmer(Locale locale) {
        try {
            return STEMMERS.get(locale).newInstance();
        }
        catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        }
        catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * Returns a stemmer that matches the language used in the supplied text.
     * <p>
     * Currently just returns the {@link PorterStemmer}.
     * 
     * @param text
     *            the text to get a stemmer for
     * @return the stemmer
     */
    public static Stemmer newAutoStemmer(String text) {
        return new PorterStemmer();
    }
}
