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

package de.unidue.inf.is.ezdl.dlwrapper.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.unidue.inf.is.ezdl.dlcore.query.YearRangeConverter.YearRange;



/**
 * Abstract class intended to make year range converting fool-proof.
 * <p>
 * This implementation is thread-safe.
 * 
 * @author mjordan
 */
public abstract class AbstractYearRangeParameterConverter {

    /**
     * The String to return if only the start year is given.
     * 
     * @param startYear
     *            the start year
     * @return the year range parameter for this case
     */
    protected abstract String onlyStartYearGiven(String startYear, String nowYear);


    /**
     * The String to return if only the end year is given.
     * 
     * @param endYear
     *            the end year
     * @return the year range parameter for this case
     */
    protected abstract String onlyEndYearGiven(String endYear, String nowYear);


    /**
     * The String to return if both the start and the end year are given.
     * 
     * @param startYear
     *            the start year
     * @param endYear
     * @return the year range parameter for this case
     */
    protected abstract String startAndEndYearGiven(String startYear, String endYear);


    /**
     * The String to return if there is no year given at all.
     * 
     * @return the year range parameter for this case
     */
    protected abstract String noYearGiven(String nowYear);


    /**
     * Returns the current year.
     * 
     * @return the current year
     */
    protected String getNowYear(Date now) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(now);
        return Integer.toString(cal.get(Calendar.YEAR));
    }


    /**
     * Returns the year range parameter for the current case.
     * 
     * @param yearRange
     *            the year range to convert
     * @param now
     *            the current date
     * @return the year range parameter
     */
    public String getYearRangeParameter(YearRange yearRange, Date now) {
        if ((yearRange.minYear != null) && (yearRange.maxYear != null)) {
            return startAndEndYearGiven(Integer.toString(yearRange.minYear), Integer.toString(yearRange.maxYear));
        }
        else if ((yearRange.minYear != null) && (yearRange.maxYear == null)) {
            return onlyStartYearGiven(Integer.toString(yearRange.minYear), getNowYear(now));
        }
        else if ((yearRange.minYear == null) && (yearRange.maxYear != null)) {
            return onlyEndYearGiven(Integer.toString(yearRange.maxYear), getNowYear(now));
        }
        else {
            return noYearGiven(getNowYear(now));
        }
    }
}
