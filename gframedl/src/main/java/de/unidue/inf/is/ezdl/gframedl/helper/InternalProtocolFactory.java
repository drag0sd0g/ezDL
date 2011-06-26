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

package de.unidue.inf.is.ezdl.gframedl.helper;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class InternalProtocolFactory implements URLStreamHandlerFactory {

    private static final List<String> EZDL_SUPPORTED_PROTOCOLS = Collections.unmodifiableList(Arrays.asList("ezdl"));


    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        URLStreamHandler h = null;
        if (EZDL_SUPPORTED_PROTOCOLS.contains(protocol)) {
            h = new EzDLProtocolHandler();
        }
        return h;
    }
}
