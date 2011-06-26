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

package de.unidue.inf.is.ezdl.gframedl;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.net.URL;

import javax.swing.ImageIcon;



public enum Images {
    LOGO_EZDL("/logo_ezdl.png"), //
    LOGO_EZDL_LARGE_SINGLE("/logo_ezdl_large_single.png"), //
    LOGO_EZDL_APP_ICON("/app_icon_trans.png"), //
    LOGO_UNIDUE("/logo_unidue.png");

    private String file;
    private ImageIcon icon;


    private Images(String file) {
        this.file = file;
    }


    public Image getImage() {
        return get().getImage();
    }


    public ImageIcon get() {
        if (icon == null) {
            if (file == null) {
                throw new IOError(new FileNotFoundException());
            }
            synchronized (file) {
                icon = new ImageIcon(getUrl());
            }
        }
        return icon;
    }


    public URL getUrl() {
        return getClass().getResource(file);
    }
}
