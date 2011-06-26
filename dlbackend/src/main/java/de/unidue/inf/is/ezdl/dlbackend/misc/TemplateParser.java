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

package de.unidue.inf.is.ezdl.dlbackend.misc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;



public final class TemplateParser {

    private Logger logger = Logger.getLogger(TemplateParser.class);

    private int looppos;
    private TemplateParser parent;
    private Map<String, String> tags;
    private String template;
    private StringBuffer work;


    public TemplateParser(TemplateParser templateparser, InputStream s) {
        this(templateparser);
        setTemplate(readTemplate(s));
    }


    public TemplateParser(TemplateParser templateparser, String template) {
        this(templateparser);
        setTemplate(template);
    }


    private TemplateParser(TemplateParser templateparser) {
        tags = new HashMap<String, String>();
        parent = templateparser;
    }


    public void addTag(String s, String s1) {
        tags.put(s, s1);
    }


    public void doParse() {
        work = work.append(internalParse(template).toString());
        tags.clear();
    }


    public void finish() {
        if (parent != null) {
            parent.insertLoop(work);
        }
    }


    public void insertLoop(StringBuffer stringbuffer) {
        work = work.insert(looppos, stringbuffer.toString());
        looppos += stringbuffer.length();
    }


    private StringBuffer internalParse(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (Entry<String, String> entry : tags.entrySet()) {
            sb = replace(sb, entry.getKey(), entry.getValue());
        }
        return sb;
    }


    public TemplateParser loop(String s, String s1) {
        int i = work.toString().indexOf(s);
        int j = work.toString().indexOf(s1);
        if (i == -1 || j == -1) {
            return null;
        }
        TemplateParser templateparser = new TemplateParser(this, work.substring(i + s.length(), j));
        work = work.delete(i, j + s1.length());
        looppos = i;
        return templateparser;

    }


    public String readTemplate(InputStream s) {
        try {
            return IOUtils.toString(s);
        }
        catch (Exception e) {
            logger.error("Cannot read template", e);
            return ("Cannot read template " + s + " !");
        }
    }


    private StringBuffer replace(StringBuffer stringbuffer, String s, String s1) {
        StringBuffer sb = new StringBuffer(stringbuffer.toString());
        if (stringbuffer.length() < 1 || s.length() < 1) {
            return sb;
        }

        int i;
        int j = 0;
        while ((i = sb.toString().indexOf(s, j)) != -1) {
            sb = sb.delete(i, i + s.length());
            sb = sb.insert(i, s1);
            j = i + s1.length();
        }
        return sb;
    }


    public String result() {
        return work.toString();
    }


    public void setTemplate(String s) {
        template = s;
        work = new StringBuffer();
    }
}
