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

/**
 * 
 */
package de.unidue.inf.is.ezdl.gframedl.tools.details.views;

import java.net.URL;

import de.unidue.inf.is.ezdl.dlfrontend.i18n.I18nSupport;
import de.unidue.inf.is.ezdl.gframedl.Images;



/**
 * The DetailView which is displayed at the start.
 * <p>
 * Reads a localized HTML text for display using {@link I18nSupport}. The
 * following macro replacements are performed:
 * <ul>
 * <li><code>%logo%</code> is replaced by the URL of the logo image.
 * </ul>
 * <p>
 * The text is read using {@link I18nSupport#getText(String)} with parameter
 * "welcome.html".
 * 
 * @see I18nSupport#getText(String)
 */
public class WelcomeScreenDetailView extends DefaultDetailView {

    private static final long serialVersionUID = -6626368444917193931L;

    private static final String WELCOME_FILE_NAME = "welcome.html";

    private URL ezdlLogo = Images.LOGO_EZDL_LARGE_SINGLE.getUrl();


    /**
     * Constructor.
     */
    public WelcomeScreenDetailView() {
        super();
        String text = I18nSupport.getInstance().getText(WELCOME_FILE_NAME);
        text = text.replaceAll("%logo%", ezdlLogo.toString());
        editorPane.setText(text);
        editorPane.setCaretPosition(0);
    }

}
