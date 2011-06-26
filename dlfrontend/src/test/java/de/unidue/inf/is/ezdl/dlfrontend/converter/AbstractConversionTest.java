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

package de.unidue.inf.is.ezdl.dlfrontend.converter;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlcore.AbstractTestBase;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.PersonList;
import de.unidue.inf.is.ezdl.dlcore.data.dldata.TextDocument;



public abstract class AbstractConversionTest extends AbstractTestBase {

    protected DocumentConversionStrategy cs;

    protected String documentExpectedOutput;
    protected String documentOutput;
    protected String journalExpectedOutput;
    protected String journalOutput;
    protected String conferenceExpectedOutput;
    protected String conferenceOutput;
    protected String conferenceTitle;
    protected String journalTitle;
    protected String journalURL;
    protected String title;
    protected String abstractText;
    protected int year;
    protected TextDocument document;


    @Test
    public void testPrintDocument() {
        System.out.println(documentExpectedOutput);
        System.out.println("   ");
        System.out.println(documentOutput);

        Assert.assertEquals(documentExpectedOutput, documentOutput);
    }


    // @Test
    public void testPrintJournal() {
        Assert.assertEquals(journalExpectedOutput, journalOutput);
    }


    // @Test
    public void testPrintConference() {
        Assert.assertEquals(conferenceExpectedOutput, conferenceOutput);
    }


    protected static TextDocument createDocument(String title, int year, String journalTitle, String conferenceTitle,
                    String abstractText, String... authors) {
        TextDocument d = new TextDocument();
        PersonList authorList = new PersonList();
        if (authors != null) {
            for (String authorName : authors) {
                Person author = new Person(authorName);
                authorList.add(author);
            }
        }

        // Conference c1 = new Conference();
        // c1.setTitle(conferenceTitle);
        //
        // Journal jn = new Journal();
        // jn.setTitle(journalTitle);
        //
        // document.setConference(c1);
        // document.setJournal(jn);
        d.setAuthorList(authorList);
        d.setTitle(title);
        d.setYear(year);
        d.setAbstract(abstractText);

        return d;
    }

}
