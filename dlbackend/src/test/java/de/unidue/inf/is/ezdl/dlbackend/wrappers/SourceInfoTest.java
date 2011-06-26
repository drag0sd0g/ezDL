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

package de.unidue.inf.is.ezdl.dlbackend.wrappers;

import java.sql.Date;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;



/**
 * A test to check a valid SourceInfo and some invalid SourceInfo objects.
 * 
 * @author markus
 */
public class SourceInfoTest extends AbstractBackendTestBase {

    /**
     * {@link de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo#isValid(de.unidue.inf.is.ezdl.dlbackend.wrappers.SourceInfo)}
     * .
     */
    @Test
    public void testIsValid() {

        booleanCheck("1", new SourceID("dummysource", "standard"), "detail", Date.valueOf("1990-01-01"), true);
        booleanCheck("2", new SourceID("dummysource", null), "detail", Date.valueOf("1990-01-01"), false);
        booleanCheck("3", new SourceID(null, "standard"), "detail", Date.valueOf("1990-01-01"), false);
        booleanCheck("3", new SourceID("dummysource", null), "detail", Date.valueOf("1990-01-01"), false);
        booleanCheck("4", new SourceID("dummysource", "standard"), null, Date.valueOf("1990-01-01"), false);
        booleanCheck("5", new SourceID("dummysource", "standard"), "detail", null, true);

    }


    /**
     * Helper method to build and check SourceInfo objects.
     * 
     * @param id
     *            the ID of the document's source
     * @param detailsInfo
     *            information useable to retrieve the document's details
     * @param timestamp
     *            the timestamp of the access
     * @param result
     *            the expectet valed value
     */
    private void booleanCheck(String label, SourceID id, String detailsInfo, Date timestamp, boolean result) {
        Assert.assertEquals(label, result, SourceInfo.isValid(new SourceInfo(id, detailsInfo, timestamp)));
    }

}
