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

import de.unidue.inf.is.ezdl.dlbackend.data.StoredDocumentList;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * @author mj
 */

public class DocumentDetailsFillAsk implements MessageContent {

    private static final long serialVersionUID = 7274564223130604997L;

    private StoredDocumentList documents;


    public DocumentDetailsFillAsk(StoredDocumentList documentList) {
        this.documents = documentList;
    }


    /**
     * @return the documents
     */
    public StoredDocumentList getDocuments() {
        return documents;
    }


    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        out.append("{DocumentDetailsFillAsk ");
        out.append(documents);
        out.append('}');
        return out.toString();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((documents == null) ? 0 : documents.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DocumentDetailsFillAsk other = (DocumentDetailsFillAsk) obj;
        if (documents == null) {
            if (other.documents != null) {
                return false;
            }
        }
        else if (!documents.equals(other.documents)) {
            return false;
        }
        return true;
    }

}
