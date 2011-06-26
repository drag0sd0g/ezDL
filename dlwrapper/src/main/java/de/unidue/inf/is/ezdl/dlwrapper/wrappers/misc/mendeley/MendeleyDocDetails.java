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

public class MendeleyDocDetails {

    private String abstrakt;
    private MendeleyAuthor[] authors;
    private String[] categories;
    private String issue;
    private String mendeleyUrl;
    private String pages;
    private String publicationOutlet;
    private String title;
    private String type;
    private String uuid;
    private String volume;
    private int year;


    public MendeleyDocDetails() {
    }


    public String getAbstrakt() {
        return abstrakt;
    }


    public void setAbstrakt(String abstrakt) {
        this.abstrakt = abstrakt;
    }


    public MendeleyAuthor[] getAuthors() {
        return authors;
    }


    public void setAuthors(MendeleyAuthor[] authors) {
        this.authors = authors;
    }


    public String[] getCategories() {
        return categories;
    }


    public void setCategories(String[] categories) {
        this.categories = categories;
    }


    public String getIssue() {
        return issue;
    }


    public void setIssue(String issue) {
        this.issue = issue;
    }


    public String getMendeleyUrl() {
        return mendeleyUrl;
    }


    public void setMendeleyUrl(String mendeleyUrl) {
        this.mendeleyUrl = mendeleyUrl;
    }


    public String getPages() {
        return pages;
    }


    public void setPages(String pages) {
        this.pages = pages;
    }


    public String getPublicationOutlet() {
        return publicationOutlet;
    }


    public void setPublicationOutlet(String publicationOutlet) {
        this.publicationOutlet = publicationOutlet;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getUuid() {
        return uuid;
    }


    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getVolume() {
        return volume;
    }


    public void setVolume(String volume) {
        this.volume = volume;
    }


    public int getYear() {
        return year;
    }


    public void setYear(int year) {
        this.year = year;
    }

}
