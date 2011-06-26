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

package de.unidue.inf.is.ezdl.dlcore.data.dldata;

import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlcore.utils.StringUtils;



/**
 * TextDocument is a text document, which is basically the kind of documents
 * that can be found in a digital library.
 * <p>
 * The difference between a text document and a picture of a text document
 * (imagine somebody taking a photo of a magazine page) is that the text
 * document is meant to be read, probably able to be processed automatically by
 * text software and it's impossible to assign a shutter speed to a text
 * document.
 * 
 * @author mj
 */
public class TextDocument extends Document {

    private static final long serialVersionUID = 7015154829398189755L;


    @Override
    public boolean isComplete() {
        if (!super.isComplete()) {
            return false;
        }
        if (StringUtils.isEmpty(getAbstract())) {
            return false;
        }
        return true;
    }


    public String getAbstract() {
        return (String) getFieldValue(Field.ABSTRACT);
    }


    public void setAbstract(String docAbstract) {
        setFieldValue(Field.ABSTRACT, docAbstract);
    }


    @Override
    public String asString() {
        StringBuffer out = new StringBuffer();
        out.append(getAuthorList());
        out.append(": ''");
        out.append(getTitle());
        out.append("'' ");
        out.append('(').append(getYear()).append(')');
        String abs = getAbstract();
        if (abs != null) {
            out.append(" {").append(StringUtils.shortenString(abs, 20)).append("}");
        }
        return out.toString();
    }

}
