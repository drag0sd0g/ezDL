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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;



final public class MergeUtils {

    private static Logger logger = Logger.getLogger(MergeUtils.class);


    private MergeUtils() {
    }


    private static Map<Field, MergeIfNull> loadFields(Class<?> clzx) {
        // finde alle Merge daten
        Map<Field, MergeIfNull> fields = new HashMap<Field, MergeIfNull>();

        Class<?> clz = clzx;
        do {
            for (Field f : clz.getDeclaredFields()) {
                MergeIfNull anno = f.getAnnotation(MergeIfNull.class);
                if (anno != null) {
                    fields.put(f, anno);
                }
            }
            clz = clz.getSuperclass();
        }
        while (clz != null);
        return fields;
    }


    private static Map<String, Object> collectValues(Map<String, Object> map, Object obj)
                    throws IllegalAccessException, InvocationTargetException {
        Map<String, Object> data = new HashMap<String, Object>();
        if (map != null) {
            data.putAll(map);
        }
        // finde alle Merge daten
        Map<Field, MergeIfNull> fields = loadFields(obj.getClass());

        // habe nun alle Felder
        // nun die Annotation checken und getter sowie setter oder den Wert
        // laden
        for (Entry<Field, MergeIfNull> e : fields.entrySet()) {
            MergeIfNull m = e.getValue();
            // nun getter und setter
            String key = m.value();
            if (key.isEmpty()) {
                key = e.getKey().getName();
            }
            Object cache = data.get(key);
            boolean replace = (cache == null);
            if (cache instanceof Number) {
                if (((Number) cache).shortValue() == m.nullValueNumber()) {
                    replace = true;
                }
            }

            if (replace) {
                if (m.isPOJO()) {
                    cache = invokeGetter(obj, e.getKey());
                }
                else {
                    Field f = e.getKey();
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    cache = f.get(obj); // einfaches auslesen
                }
                // insert or replace
                data.put(key, cache);
            }

        }

        return data;
    }


    private static Object invokeGetter(Object obj, Field f) throws IllegalAccessException, InvocationTargetException {
        String getterName = "get" + Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
        Method m = null;
        try {
            m = obj.getClass().getDeclaredMethod(getterName);
        }
        catch (NoSuchMethodException e) {
            for (Method mx : obj.getClass().getMethods()) {
                if (mx.getName().equals(getterName)) {
                    // use first one
                    m = mx;
                }
            }
        }
        Object out = null;
        if (m != null) {
            if (!m.isAccessible()) {
                m.setAccessible(true); // error if not public
            }
            out = m.invoke(obj);
        }
        return out;
    }


    private static void invokeSetter(Object obj, Field f, Object value) throws IllegalArgumentException,
                    IllegalAccessException, InvocationTargetException {
        String setterName = "set" + Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
        Class<?> param = value != null ? value.getClass() : Object.class;
        // problem mit auto-boxing
        Method m = null;
        try {
            m = obj.getClass().getDeclaredMethod(setterName, param);
        }
        catch (NoSuchMethodException e) {
            for (Method mx : obj.getClass().getMethods()) {
                if (mx.getName().equals(setterName)) {
                    // use first one
                    m = mx;
                }
            }
        }
        if (!m.isAccessible()) {
            m.setAccessible(true); // error if not public
        }
        m.invoke(obj, value);
    }


    private static void setValues(Map<String, Object> map, Object target) throws Exception {

        // find MergeStuff
        Map<Field, MergeIfNull> fields = loadFields(target.getClass());

        for (Entry<Field, MergeIfNull> e : fields.entrySet()) {
            MergeIfNull m = e.getValue();
            // nun getter und setter
            String key = m.value();
            if (key.isEmpty()) {
                key = e.getKey().getName();
            }
            Object cache = map.get(key);
            if (cache != null) {
                // deepcopy?
                if (m.deepCopy()) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(cache);
                    oos.close();
                    bos.flush();
                    ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
                    ObjectInputStream oin = new ObjectInputStream(bin);
                    cache = oin.readObject();
                    oin.close();
                    bin.close();
                    bos.close();
                }

                //
                if (m.isPOJO()) {
                    invokeSetter(target, e.getKey(), cache);
                }
                else {
                    try {
                        e.getKey().set(target, cache);
                    }
                    catch (IllegalAccessException ex) {
                        logger.error("can't set " + key);
                        throw ex;
                    }
                }
            }
        }

    }


    public static Object merge(Object target, Object... src) {
        // init if target
        Object t = null;
        try {
            Map<String, Object> map = collectValues(null, target);
            for (Object node : src) {
                map = collectValues(map, node);
            }
            setValues(map, target);
            t = target;
        }
        catch (Exception e) {
            logger.error("Merge failed.", e);
        }
        return t;
    }
}
