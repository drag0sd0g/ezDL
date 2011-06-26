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

package de.unidue.inf.is.ezdl.dlbackend.message.content;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import de.unidue.inf.is.ezdl.dlbackend.AbstractBackendTestBase;
import de.unidue.inf.is.ezdl.dlbackend.message.Message;
import de.unidue.inf.is.ezdl.dlcore.data.query.ResultDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.content.DocumentDetailsTell;



public class DocumentXXTellTest extends AbstractBackendTestBase {

    @Test
    public void testEquals() {
        Message msg1 = getMsg();
        Message msg2 = getMsg();
        Assert.assertEquals("both objects equal", msg1, msg2);
    }


    @Test
    public void testContains() {
        Set<DocumentDetailsTell> tells = new HashSet<DocumentDetailsTell>();
        tells.add(getContent());
        Assert.assertTrue("contains", tells.contains(getContent()));

    }


    private Message getMsg() {
        DocumentDetailsTell tell1 = getContent();
        Message msg1 = new Message("client", "repo", tell1, "reqid");
        return msg1;
    }


    private DocumentDetailsTell getContent() {
        ResultDocumentList documentList = new ResultDocumentList();
        DocumentDetailsTell tell1 = new DocumentDetailsTell(documentList, false);
        return tell1;
    }

}
