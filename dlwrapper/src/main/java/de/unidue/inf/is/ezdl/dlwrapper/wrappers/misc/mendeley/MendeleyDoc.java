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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.mendeley;

public class MendeleyDoc {

    private String uuid;
    private String title;
    private String publicationOutlet;
    private int year;
    private String mendeleyUrl;
    private String doi;
    private String authors;


    public MendeleyDoc() {
    }


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getPublicationOutlet() {
        return publicationOutlet;
    }


    public void setPublicationOutlet(String publicationOutlet) {
        this.publicationOutlet = publicationOutlet;
    }


    public int getYear() {
        return year;
    }


    public void setYear(int year) {
        this.year = year;
    }


    public String getMendeleyUrl() {
        return mendeleyUrl;
    }


    public void setMendeleyUrl(String mendeleyUrl) {
        this.mendeleyUrl = mendeleyUrl;
    }


    public String getDoi() {
        return doi;
    }


    public void setDoi(String doi) {
        this.doi = doi;
    }


    public String getAuthors() {
        return authors;
    }


    public void setAuthors(String authors) {
        this.authors = authors;
    }

}
