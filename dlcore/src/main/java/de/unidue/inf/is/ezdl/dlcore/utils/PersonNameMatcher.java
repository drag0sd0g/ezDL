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

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Person;



/**
 * Matches person names in various forms.
 * 
 * @author mj
 */
final public class PersonNameMatcher {

    private PersonNameMatcherRuleBase ruleBase;


    /**
     * Initialization on demand holder. Apparently the only thread-safe way to
     * get a singleton.
     * 
     * @author mj
     */
    private static class Holder {

        private static PersonNameMatcher instance = new PersonNameMatcher();
    }


    private PersonNameMatcher() {
        super();
        ruleBase = new PersonNameMatcherRuleBase();
    }


    public static synchronized PersonNameMatcher instance() {
        return Holder.instance;
    }


    /**
     * Checks if the names is in one of the forms
     * <ul>
     * <li>M Jackson</li>
     * <li>M. Jackson</li>
     * <li>AJ Simon</li>
     * </ul>
     * 
     * @return true, if the names is in one of the above forms. Else false.
     */
    public boolean isInFormMJackson(Person person) {
        final String firstName = person.getFirstName();
        // M Jackson
        final boolean initialOnly = initialOnly(firstName);
        // M. Jackson
        final boolean initialPeriod = initialPeriod(firstName);
        // AJ Simon
        final boolean initials = initialsOnly(firstName);
        if (!initialOnly && !initialPeriod && !initials) {
            return false;
        }
        return true;
    }


    boolean initialsOnly(final String str) {
        return StringUtils.allCaps(str);
    }


    /**
     * Checks if two persons have an equal name. This is true if the last name
     * is the same and the first name is similar, according to certain rules.
     * <ul>
     * <li>"A" and "A." equal anything that starts with A (e.g. "A" == "Andrew")
     * </li>
     * <li>"AJ", "A J", "A.J." and "A. J." equal names with two first name parts
     * that start with "A" and "J", respectively (e.g. "AJ" == "Andrew Jackson")
     * </li>
     * <li>"Donald E" and "Donald E." equal names like "Donald Ervin"</li>
     * <li>"Donald E" and "Donald E." also equal "Donald"</li>
     * </ul>
     * 
     * @param other
     *            the other person to compare this with. This is treated as the
     *            name to match so if other is "Silva" and one is "de Silva",
     *            the names match. If other is "de Silva" and one is just
     *            "Silva" then the names don't match because it is assumed that
     *            other is as specific as intended for the query.
     * @return true if the names look like they are meant to be equal according
     *         to the above rules. Else false.
     */
    public boolean specialEquals(Person one, Person other) {
        final boolean lastNamesEqual = lastNamesSpecialEqual(one.getLastName(), other.getLastName());
        if (!lastNamesEqual) {
            return false;
        }

        boolean firstNamesEqual = firstNamesSpecialEqual(one.getFirstName(), other.getFirstName());

        if (!firstNamesEqual) {
            return ruleBase.equivalent(one.asString(), other.asString());
        }

        return firstNamesEqual;
    }


    private boolean lastNamesSpecialEqual(String one, String other) {
        String oneSC = one.toLowerCase();
        String otherSC = other.toLowerCase();
        final int oneLen = one.length();
        final int otherLen = other.length();
        if (oneLen > otherLen) {
            oneSC = oneSC.substring(oneLen - otherLen, oneLen);
        }

        return oneSC.equalsIgnoreCase(otherSC);
    }


    boolean firstNamesSpecialEqual(String one, String other) {
        if ((one == null) || (other == null)) {
            return true;
        }

        String[] oneFirstNameParts = splitFirstName(one);
        String[] otherFirstNameParts = splitFirstName(other);
        for (int i = 0; (i < oneFirstNameParts.length) && (i < otherFirstNameParts.length); i++) {
            String onePart = oneFirstNameParts[i];
            String otherPart = otherFirstNameParts[i];
            if (!firstNamePartSpecialEquals(onePart, otherPart)) {
                return false;
            }
        }

        return true;
    }


    String[] splitFirstName(String firstName) {
        String[] spaceSplit = firstName.split(" ");
        if (spaceSplit.length != 1) {
            return spaceSplit;
        }

        String[] dotSplit = firstName.split("\\.");
        if (dotSplit.length != 1) {
            return dotSplit;
        }

        if (StringUtils.allCaps(firstName)) {
            String[] charSplit = new String[firstName.length()];
            char[] chars = firstName.toCharArray();
            int len = chars.length;
            for (int i = 0; (i < len); i++) {
                charSplit[i] = String.valueOf(chars[i]);
            }
            return charSplit;
        }

        String[] out = new String[1];
        out[0] = firstName;
        return out;
    }


    boolean firstNamePartSpecialEquals(String firstStr, String secondStr) {
        final int firstLen = firstStr.length();
        final int secondLen = secondStr.length();
        final String longerStr;
        final String shorterStr;
        if (firstLen > secondLen) {
            longerStr = firstStr;
            shorterStr = secondStr;
        }
        else {
            longerStr = secondStr;
            shorterStr = firstStr;
        }

        if (shorterStr.isEmpty()) {
            /*
             * No first name means we assume it's the same as in the other name.
             * eg. "Simon" would equal "Andrew Simon".
             */
            return true;
        }

        int pos = getFirstDifferentPos(longerStr, shorterStr);

        if (pos == -1) {
            return true;
        }

        if (shorterStr.charAt(pos) == '.') {
            return true;
        }

        return false;

        // if (initialOnly(shorterStr)) {
        // return initialMatches(longerStr, shorterStr);
        // }
        //
        // if (initialPeriod(shorterStr)) {
        // return initialMatches(longerStr, shorterStr);
        // }
        //
        // return firstStr.equals(secondStr);
    }


    private int getFirstDifferentPos(final String longerStr, final String shorterStr) {
        int shorterLen = shorterStr.length();
        int differentAtPosition = -1;
        for (int i = 0; (i < shorterLen); i++) {
            char a = shorterStr.charAt(i);
            char b = longerStr.charAt(i);
            if (a != b) {
                differentAtPosition = i;
                break;
            }
        }
        return differentAtPosition;
    }


    boolean initialMatches(final String longerStr, final String shorterStr) {
        return (shorterStr.length() != 0) && (longerStr.length() != 0) && (longerStr.charAt(0) == shorterStr.charAt(0));
    }


    boolean initialOnly(final String str) {
        return (str.length() == 1) && (Character.isUpperCase(str.charAt(0)));
    }


    boolean initialPeriod(final String str) {
        return (str.length() == 2) && (Character.isUpperCase(str.charAt(0)) && (str.endsWith(".")));
    }

}
