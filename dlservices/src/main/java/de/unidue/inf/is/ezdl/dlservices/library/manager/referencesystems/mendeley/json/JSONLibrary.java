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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.json;

import java.util.ArrayList;
import java.util.List;



/** Represents a MENDELEY Library JSON Object */
public class JSONLibrary {

    private String total_results;
    private String total_pages;
    private String current_page;
    private String items_per_page;
    private List<String> document_ids;


    public JSONLibrary() {
        total_results = "";
        total_pages = "";
        current_page = "";
        items_per_page = "";
        document_ids = new ArrayList<String>();
    }


    public String getCurrent_page() {
        return current_page;
    }


    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }


    public List<String> getDocument_ids() {
        return document_ids;
    }


    public void setDocument_ids(ArrayList<String> document_ids) {
        this.document_ids = document_ids;
    }


    public String getItems_per_page() {
        return items_per_page;
    }


    public void setItems_per_page(String items_per_page) {
        this.items_per_page = items_per_page;
    }


    public String getTotal_pages() {
        return total_pages;
    }


    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }


    public String getTotal_results() {
        return total_results;
    }


    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

}
