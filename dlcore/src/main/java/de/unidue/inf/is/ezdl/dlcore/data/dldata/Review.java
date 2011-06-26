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

import com.google.common.base.Objects;

import de.unidue.inf.is.ezdl.dlcore.data.Mergeable;



public class Review extends AbstractDLObject {

    private static final long serialVersionUID = -6868434047952944923L;

    private String authorid;
    private int rating;
    private int totalVotes;
    private int helpfulVotes;
    private String date;
    private String summary;
    private String content;


    public Review(String authorid, int rating, int totalVotes, int helpfulVotes, String date, String summary,
                    String content) {
        super();
        this.authorid = authorid;
        this.rating = rating;
        this.totalVotes = totalVotes;
        this.helpfulVotes = helpfulVotes;
        this.date = date;
        this.summary = summary;
        this.content = content;
    }


    public String getAutorid() {
        return authorid;
    }


    public int getRating() {
        return rating;
    }


    public int getTotalVotes() {
        return totalVotes;
    }


    public int getHelpfulVotes() {
        return helpfulVotes;
    }


    public String getDate() {
        return date;
    }


    public String getSummary() {
        return summary;
    }


    public String getContent() {
        return content;
    }


    @Override
    public String asString() {
        return getSummary();
    }


    @Override
    public boolean isSimilar(Mergeable other) {
        if (other instanceof Review) {
            Review review = (Review) other;
            return Objects.equal(authorid, review.authorid) && Objects.equal(date, review.date)
                            && Objects.equal(summary, review.summary);
        }
        else {
            return false;
        }
    }

}
