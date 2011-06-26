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

package de.unidue.inf.is.ezdl.dlcore.analysis.stopwords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;



public class DefaultStopwordFilter implements StopwordFilter {

    private Logger logger = Logger.getLogger(DefaultStopwordFilter.class);

    private Multimap<Locale, String> stopwords;


    public DefaultStopwordFilter() {
        stopwords = HashMultimap.create();
        try {
            readStopwordList(Locale.ENGLISH, "/stopwords/english");
            readStopwordList(Locale.GERMAN, "/stopwords/german");
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private void readStopwordList(Locale locale, String url) throws IOException {
        List<String> list = org.apache.commons.io.IOUtils.readLines(DefaultStopwordFilter.class
                        .getResourceAsStream(url));
        stopwords.putAll(locale, list);
    }


    @Override
    public List<String> filter(List<String> terms, Locale locale) {
        Collection<String> stopwordSet = stopwords.get(locale);
        if (stopwordSet == null) {
            throw new IllegalArgumentException("no stopword list for " + locale + " found.");
        }
        List<String> result = new ArrayList<String>();
        for (String term : terms) {
            if (!stopwordSet.contains(term.toLowerCase())) {
                result.add(term);
            }
        }
        return result;
    }

}
