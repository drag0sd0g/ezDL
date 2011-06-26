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

package de.unidue.inf.is.ezdl.dlservices.repository.store;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocument;
import de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;



/**
 * This decision strategy decides whether to retrieve details for a document
 * based on two pieces of information:
 * <ul>
 * <li>The last time of an attempt to access details</li>
 * <li>The publishing date</li>
 * </ul>
 * If the publishing date is too long ago (
 * {@link #PUBLISHING_DATE_THRESHOLD_YEARS}), no further attempt at retrieving
 * details will be made unless the source infos indicate that no such attempt
 * has been made, yet.
 * <p>
 * If the last attempt at accessing details is too recent (
 * {@link #LAST_ACCESS_THRESHOLD_MS}), no attempt will be made.
 * 
 * @author mjordan
 */
public class SmartDetailRetrievalDecision implements DetailRetrievalDecision {

    /**
     * A constant to calculate the conversion from a timestamp to days.
     */
    private static final int DAY_EXCHANGE_CONST = 24 * 60 * 60 * 1000;
    /**
     * The threshold for an old access.
     */
    static final int LAST_ACCESS_THRESHOLD_MS = 1 * DAY_EXCHANGE_CONST;
    /**
     * The threshold for old publications.
     */
    static final int PUBLISHING_DATE_THRESHOLD_YEARS = 10;


    @Override
    public boolean detailRetrievalSensible(StoredDocument stored) {
        Date currentDate = new Date();
        return toBeCompleted(stored) && isDocGoodDetailsCandidate(currentDate, stored);
    }


    private boolean isDocGoodDetailsCandidate(Date currentDate, StoredDocument document) {
        final boolean isDocYoungEnough = documentYoungEnough(currentDate, document);
        if (isDocYoungEnough) {
            return someSourceMightHaveNewInfo(currentDate, document);
        }
        else {
            return docHasNoDetailsYet(document);
        }
    }


    private boolean docHasNoDetailsYet(StoredDocument document) {
        for (SourceInfo source : document.getSources()) {
            final Date timestamp = source.getDetailTimestamp();
            if (timestamp != null) {
                return false;
            }
        }
        return true;
    }


    private boolean someSourceMightHaveNewInfo(Date currentDate, StoredDocument document) {
        Set<SourceInfo> sourceInfos = null;
        try {
            sourceInfos = new HashSet<SourceInfo>(document.getSources());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (SourceInfo sourceInfo : sourceInfos) {
            if (sourceMightHaveNewInfo(currentDate, sourceInfo)) {
                return true;
            }
        }
        return false;
    }


    private boolean sourceMightHaveNewInfo(Date currentDate, SourceInfo time) {
        final Date detailTimestamp = time.getDetailTimestamp();
        if (detailTimestamp == null) {
            return true;
        }
        final boolean lastAccessTooLongAgo = (currentDate.getTime() - detailTimestamp.getTime()) > LAST_ACCESS_THRESHOLD_MS;
        if (lastAccessTooLongAgo) {
            return true;
        }
        return false;
    }


    private boolean documentYoungEnough(Date currentDate, StoredDocument document) {
        final Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        final int currentYear = c.get(Calendar.YEAR);
        final int docPublishedBeforeYears = currentYear - document.getDocument().getYear();
        if (docPublishedBeforeYears < PUBLISHING_DATE_THRESHOLD_YEARS) {
            return true;
        }
        return false;
    }


    /**
     * Determines if a document is to be completed by passing it to some
     * wrappers, asking for details.
     * <p>
     * Currently, this is already the case if the document is not complete, as
     * determined by {@link Document#isComplete()}. No further levels of
     * sophistication are involved here, yet.
     * 
     * @param stored
     * @return
     */
    private static boolean toBeCompleted(StoredDocument stored) {
        final Document document = stored.getDocument();
        return !document.isComplete();
    }

}
