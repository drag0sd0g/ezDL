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

package de.unidue.inf.is.ezdl.dlcore.data.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import de.unidue.inf.is.ezdl.dlcore.analysis.language.DefaultLanguageAnalyzer;
import de.unidue.inf.is.ezdl.dlcore.analysis.stopwords.DefaultStopwordFilter;
import de.unidue.inf.is.ezdl.dlcore.analysis.stopwords.StopwordFilter;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocument;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * This class is indent to help extraction terms e.g. "all but authors"
 * 
 * @author Jens Kapitza
 */
public class TermExtractor extends AbstractExtractor {

    private StopwordFilter filter;
    private Locale locale;


    /**
     * Constructor.
     * <p>
     * A {@link DefaultStopwordFilter} is used and the locale is determined
     * automatically using {@link DefaultLanguageAnalyzer}.
     */
    public TermExtractor() {
        filter = new DefaultStopwordFilter();
    }


    /**
     * Constructor.
     * <p>
     * Behaves the same as {@link TermExtractor()} but uses the given Locale.
     * 
     * @param locale
     *            the Locale to use.
     */
    public TermExtractor(Locale locale) {
        this();
        this.locale = locale;
    }


    /**
     * the real implementation of "all but authors"
     * 
     * @param from
     *            the object we are reading information from
     * @return the extracted information
     */
    @Override
    protected ExtractionResult extract(Document from) {
        ExtractionResultImpl result = new ExtractionResultImpl();
        if (from instanceof TextDocument) {
            add(result, ((TextDocument) from).getAbstract());
        }
        add(result, from.getTitle());

        return result;
    }


    /**
     * Split the information cause in sense of term it is a standalone word.
     * TODO this method removes stopwords but don't detect any phrases.
     * 
     * @param result
     *            the list we will append the items
     * @param item
     *            the item itself.
     */
    private void add(ExtractionResultImpl result, String item) {
        if (item != null) {
            inferLanguage(item);
            List<String> terms = new ArrayList<String>();

            TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_30, new StringReader(item));
            // OffsetAttribute offsetAttribute =
            // tokenStream.getAttribute(OffsetAttribute.class);
            TermAttribute termAttribute = tokenStream.getAttribute(TermAttribute.class);

            try {
                while (tokenStream.incrementToken()) {
                    // int startOffset = offsetAttribute.startOffset();
                    // int endOffset = offsetAttribute.endOffset();
                    String term = termAttribute.term();
                    terms.add(term);
                }
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            terms = filter.filter(terms, locale);

            for (String t : terms) {
                if (!StringUtils.isEmpty((t))) {
                    Entry e = new EntryImpl(t.toLowerCase(locale));
                    result.add(e);
                }
            }
        }
    }


    @Override
    protected ExtractionResult extract(ResultDocument from) {
        return extract(from.getDocument());
    }


    private Locale inferLanguage(String text) {
        if (locale == null) {
            locale = DefaultLanguageAnalyzer.analyze(text);
        }
        return locale;
    }

}
