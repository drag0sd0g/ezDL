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

package de.unidue.inf.is.ezdl.dlcore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;



/**
 * Some total brain-dead version of a rule-based person name matcher.
 * <p>
 * The idea is that some names equal even though there is no algorithm for
 * matching them.
 * <p>
 * Examples are - for full names - C. J. van Rijsbergen and Keith van
 * Rijsbergen.
 * <p>
 * The rules are given in an internal file. The format is: each line holds one
 * full name. Names that are equivalent are grouped and the groups are separated
 * by an empty line.
 * 
 * @author mjordan
 */
public class PersonNameMatcherRuleBase {

    private class EquivClass extends ArrayList<String> {

        private static final long serialVersionUID = -6605417385371570578L;

    }


    private Map<String, EquivClass> equivs = new HashMap<String, EquivClass>();

    private EquivClass tmpEquivs;


    /**
     * Constructor.
     * <p>
     * The constructor is not thread-safe.
     */
    public PersonNameMatcherRuleBase() {
        try {
            initFromFile();
        }
        catch (IOException e) {
            initStatic();
        }
    }


    private void initFromFile() throws IOException {
        InputStream is = this.getClass().getResourceAsStream("/names.equiv");
        if (is == null) {
            throw new IOException();
        }

        List<String> lines = IOUtils.readLines(is);
        initFromLines(lines);
    }


    private void initFromLines(List<String> lines) {
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#")) {
                // comment line
            }
            else if (line.isEmpty()) {
                commit();
            }
            else {
                collect(line);
            }
        }
        commit();
    }


    private void initStatic() {
        collect("Keith van Rijsbergen");
        collect("C.J. van Rijsbergen");
        collect("C. J. van Rijsbergen");
        commit();

        collect("Nick Belkin");
        collect("Nicholas Belkin");
        commit();
    }


    private void collect(String equiv) {
        getEquivs().add(equiv);
    }


    private void commit() {
        for (String equiv : tmpEquivs) {
            equivs.put(equiv, tmpEquivs);
        }
        tmpEquivs = null;
    }


    private List<String> getEquivs() {
        if (tmpEquivs == null) {
            tmpEquivs = new EquivClass();
        }
        return tmpEquivs;
    }


    /**
     * Matches names given by the string parameters using the rule base.
     * 
     * @param a
     *            the first name to match
     * @param b
     *            the second name to match
     * @return true, if the names match according to the rule base, or false, if
     *         they don't.
     */
    public boolean equivalent(String a, String b) {
        EquivClass aEq = equivs.get(a);
        EquivClass bEq = equivs.get(b);

        return aEq == bEq && (aEq != null);
    }
}
