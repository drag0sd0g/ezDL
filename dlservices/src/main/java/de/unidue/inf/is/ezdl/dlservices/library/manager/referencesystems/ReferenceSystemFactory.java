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

package de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.library.ReferenceSystem;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.bibsonomy.Bibsonomy;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.connotea.Connotea;
import de.unidue.inf.is.ezdl.dlservices.library.manager.referencesystems.mendeley.Mendeley;



/** Factory class which provides available online reference systems */
public class ReferenceSystemFactory {

    private static final Map<String, Class<? extends OnlineReferenceSystem>> referencesystems = new HashMap<String, Class<? extends OnlineReferenceSystem>>();

    private static Logger logger = Logger.getLogger(ReferenceSystemFactory.class);

    /** Set available reference systems */
    static {

        referencesystems.put("1. " + Mendeley.getReferenceSystemName(), Mendeley.class);
        referencesystems.put("2. " + Bibsonomy.getReferenceSystemName(), Bibsonomy.class);
        referencesystems.put("3. " + Connotea.getReferenceSystemName(), Connotea.class);
    }


    /**
     * Returns a List of available reference systems
     * 
     * @return List of available reference systems
     */
    public static List<ReferenceSystem> getAvailableReferenceSystems() {
        List<ReferenceSystem> list = new ArrayList<ReferenceSystem>();
        for (String s : referencesystems.keySet()) {
            try {
                OnlineReferenceSystem ors = referencesystems.get(s).newInstance();
                ReferenceSystem rs = new ReferenceSystem(s, ors.getRequiredAuthParameters(),
                                ors.getOtherRequiredParameters());
                list.add(rs);
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return list;
    }


    /**
     * Creates new instance
     * 
     * @param referenceSystem
     *            , the choosen referencesystem
     * @param props
     *            property file from library *
     * @return
     * @throws Exception
     */
    public static OnlineReferenceSystem createInstance(ReferenceSystem referenceSystem, Properties props)
                    throws Exception {
        if (referenceSystem.getName() != null) {
            OnlineReferenceSystem rs = referencesystems.get(referenceSystem.getName()).newInstance();
            rs.initialize(referenceSystem.getRequiredParameters(), referenceSystem.getOtherParameters(), props);
            return rs;
        }
        else {
            throw new ReferenceSystemException("Reference System Error: ", "no reference system selected");
        }
    }
}
