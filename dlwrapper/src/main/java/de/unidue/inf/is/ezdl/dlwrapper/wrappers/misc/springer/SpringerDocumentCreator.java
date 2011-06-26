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

package de.unidue.inf.is.ezdl.dlwrapper.wrappers.misc.springer;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Document;
import de.unidue.inf.is.ezdl.dlcore.data.fields.Field;
import de.unidue.inf.is.ezdl.dlwrapper.utils.DocumentCreator;



/**
 * @author mjordan
 */
public class SpringerDocumentCreator extends DocumentCreator {

    @Override
    protected void handleVolume(Document doc, String volume) {
        if (volume != null) {
            final String VOLUME_PREFIX = "Volume";
            if (volume.startsWith(VOLUME_PREFIX)) {
                volume = volume.substring(VOLUME_PREFIX.length());
            }
            final String VOLUME_SUFFIX = ",";
            if (volume.endsWith(VOLUME_SUFFIX)) {
                volume = volume.substring(0, volume.length() - VOLUME_SUFFIX.length());
            }
            volume = volume.trim();
            setFieldValueTrimmedIfNotEmpty(doc, Field.VOLUME, volume);
        }
    }
}
