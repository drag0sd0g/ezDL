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

package de.unidue.inf.is.ezdl.dlcore.utils.merge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * This is a Merge annotation. This Flag is used to merge Objects to remove null
 * values. For more information see MergeTool
 * 
 * @author Jens Kapitza
 * @see MergeUtils
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MergeIfNull {

    /**
     * This option will fail if the modifier is not public
     * 
     * @return true per default cause we will use getter and setter.
     */
    boolean isPOJO() default true;


    /**
     * the default number for null values.
     * 
     * @return 0 as default from JVM
     */
    short nullValueNumber() default 0;


    /**
     * copy the value.
     * 
     * @return true if we will use deepcopy else false.
     */
    boolean deepCopy() default true;


    /**
     * the value is the key to match. we should be able to merge values across
     * different classes
     * 
     * @return the key name if this value is empty the fieldname should be used.
     */
    String value() default ""; // the key
}
