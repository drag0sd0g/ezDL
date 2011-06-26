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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.query.Filter;
import de.unidue.inf.is.ezdl.dlwrapper.toolkit.ToolkitAPI;



/**
 * Toolkit-based Wrapper for libraries that have to be queried in a two-step
 * process.
 * <p>
 * The wrapper works basically like {@link AbstractBasicToolkitWrapper} but has
 * an additional method to implement:
 * {@link #performStepTwo(StoredDocumentList)}.
 * <p>
 * The idea is to first query the remote DL using the usual Tookit approach but
 * only scraping document IDs off the result page. The result document IDs are
 * expected in items labeled "id". The first step executes the toolkit
 * configuration and returns a {@link StoredDocumentList} with the data scraped
 * off that page.
 * <p>
 * The second step is then to traverse the list, taking the ID information for
 * each item and doing whatever necessary to get the document details for these
 * items. One wrapper might post these IDs to another form on the DL to retrieve
 * an export file -- e.g. in BibTeX format -- and parse it. This second step is
 * implemented in {@link #performStepTwo(StoredDocumentList)}.
 */
public abstract class AbstractTwoStepToolkitWrapper extends AbstractBasicToolkitWrapper {

    /**
     * The default constructor initializes the two ToolkitAPI references with
     * real ToolkitAPI objects.
     */
    public AbstractTwoStepToolkitWrapper() {
        this(new ToolkitAPI());
    }


    /**
     * The constructor that takes references to ToolkitAPI objects. Used for
     * testing.
     * 
     * @param api
     *            the toolkit object used for Document queries
     */
    public AbstractTwoStepToolkitWrapper(ToolkitAPI api) {
        super(api);
    }


    @Override
    @SuppressWarnings("rawtypes")
    protected URL processFollowData(StoredDocumentList result, List followData, Filter filter) {
        URL nextPage = null;
        Iterator iter = followData.iterator();
        while (!isHalted() && iter.hasNext()) {
            Map ht = (Map) iter.next();
            /*
             * Not logging an error here because sometimes only few results are
             * returned and then the wrapper should not shut down. If the web
             * page layout is wildly different and that is the reason for the
             * page number parsing problem, we should have other parsing errors
             * as well and those should be more significant than this one.
             */
            nextPage = (URL) ht.get(HT_KEY_NEXTPAGE);

            String detailsLink = (String) ht.get(HT_KEY_DETAILS);
            Document md = createDocumentFromMap(ht);

            StoredDocument stored = new StoredDocument(md);
            stored.addSource(new SourceInfo(getSourceID(), detailsLink));

            result.add(stored);
        }

        performStepTwo(result);

        return nextPage;
    }


    protected abstract void performStepTwo(StoredDocumentList result);


    @SuppressWarnings("rawtypes")
    @Override
    protected Document createDocumentFromMap(Map ht) {
        Document doc = new TextDocument();
        doc.setFieldValue(Field.DOI, ht.get("id"));
        return doc;
    }
}
