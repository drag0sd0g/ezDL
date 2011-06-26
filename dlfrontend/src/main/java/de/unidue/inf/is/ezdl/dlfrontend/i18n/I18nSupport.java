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

package de.unidue.inf.is.ezdl.dlfrontend.i18n;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.utils.IOUtils;



/**
 * A support class for internationalization. This class is a singleton.
 */
public final class I18nSupport {

    /**
     * {@link Control} to load property files in UTF-8.
     */
    public class UTF8Control extends Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                        boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            }
            else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                }
                finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }


    private static final char MNEMONIC_POS_MARKER = '_';
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Map<String, Locale> LOCALES_MAP;
    static {
        Map<String, Locale> map = new HashMap<String, Locale>();
        // add available languages
        map.put("de", Locale.GERMAN);
        map.put("es", new Locale("es", "ES"));
        map.put("fr", Locale.FRENCH);
        map.put("en", DEFAULT_LOCALE);
        LOCALES_MAP = Collections.unmodifiableMap(map);
    }
    /** The base names for all resource bundles */
    private static final String BUNDLE_BASE_NAME = "lang.language";

    private static Logger logger = Logger.getLogger(I18nSupport.class);

    private static volatile I18nSupport instance;

    private Locale locale;
    private ResourceBundle resourceBundle;


    private I18nSupport() {
        if (instance != null) {
            throw new IllegalStateException("I18nSupport already instantiated");
        }
    }


    public Map<String, Locale> getLocalesMap() {
        return LOCALES_MAP;
    }


    public Locale getLocale() {
        return locale;
    }


    /**
     * Returns a localized string for the given key from the locale stuff.
     * <p>
     * The first {@link #MNEMONIC_POS_MARKER} is discarded from the output but
     * can be used to indicate the position of a mnemonic if the string is to be
     * used as a label. E.g. fookey=C_ancel would return "Cancel".
     * 
     * @param key
     *            the key of the string
     * @return the string
     * @see #getMnemonicPos(String)
     */
    public String getLocString(String key) {
        String result = getRawLocString(key);
        result = result.replaceFirst(String.valueOf(MNEMONIC_POS_MARKER), "");
        return result;
    }


    private String getRawLocString(String key) {
        String result = "";
        if (resourceBundle != null) {
            try {
                result = resourceBundle.getString(key);
            }
            catch (MissingResourceException e) {
                result = key;
            }
        }
        return result;
    }


    /**
     * Returns the position of the character in the localized string with the
     * given key that is to be used as mnemonic.
     * <p>
     * The position is marked by a {@link #MNEMONIC_POS_MARKER} in front of the
     * character to be used. E.g. fookey=C_ancel would return 1
     * 
     * @param key
     *            the key of the string
     * @return the position of the mnemonic character or 0 if no marker is
     *         found.
     * @see #getLocString(String)
     * @see JButton#setMnemonic(int)
     * @see JLabel#setDisplayedMnemonic(int)
     */
    public int getMnemonicPos(String key) {
        String result = getRawLocString(key);
        int mnemonicPos = result.indexOf(MNEMONIC_POS_MARKER);
        if (mnemonicPos < 0) {
            mnemonicPos = 0;
        }
        return mnemonicPos;
    }


    public void setLocale(Locale locale) {
        if (locale == null) {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.US, new UTF8Control());
        }
        else {
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, new UTF8Control());
        }
        this.locale = resourceBundle.getLocale();
        Locale.setDefault(locale);
    }


    public static I18nSupport getInstance() {
        if (instance == null) {
            synchronized (I18nSupport.class) {
                if (instance == null) {
                    instance = new I18nSupport();
                }
            }
        }
        return instance;
    }


    public void init(String locale) {
        Locale l = LOCALES_MAP.get(locale);
        setLocale(l != null ? l : DEFAULT_LOCALE);
    }


    /**
     * Returns a localized text.
     * <p>
     * A text with the name "welcome" is loaded from
     * <code>/lang/language_XX.welcome</code>, with "XX" being the locale code.
     * 
     * @param textName
     *            the name of the text to load
     * @return the contents of that text file as a String
     */
    public String getText(String textName) {
        final String fileName = "/lang/language_" + locale.getLanguage() + "." + textName;
        BufferedInputStream s = new BufferedInputStream(getClass().getResourceAsStream(fileName));
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(s, "UTF-8"));
            return IOUtils.readBufferAsString(br);

        }
        catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

}
