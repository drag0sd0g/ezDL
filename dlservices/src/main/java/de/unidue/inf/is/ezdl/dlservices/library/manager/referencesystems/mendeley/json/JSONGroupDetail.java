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



/** Represents all details of a group as a JSON object */
public class JSONGroupDetail {

    private String current_page;
    private List<String> document_ids;
    private String group_id;
    private String group_invite_only;
    private String group_name;
    private String items_per_page;
    private String total_pages;
    private String total_results;
    private String group_type;


    public JSONGroupDetail() {
        current_page = "";
        document_ids = new ArrayList<String>();
        group_id = "";
        group_invite_only = "";
        group_name = "";
        items_per_page = "";
        total_pages = "";
        total_results = "";
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


    public void setDocument_ids(List<String> document_ids) {
        this.document_ids = document_ids;
    }


    public String getGroup_id() {
        return group_id;
    }


    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }


    public String getGroup_invite_only() {
        return group_invite_only;
    }


    public void setGroup_invite_only(String group_invite_only) {
        this.group_invite_only = group_invite_only;
    }


    public String getGroup_name() {
        return group_name;
    }


    public void setGroup_name(String group_name) {
        this.group_name = group_name;
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


    public String getGroup_type() {
        return group_type;
    }


    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

}
