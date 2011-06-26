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

package de.unidue.inf.is.ezdl.dlservices.terminfo.services.dummy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.dldata.Term;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfo;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfoService;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermInfos;
import de.unidue.inf.is.ezdl.dlservices.terminfo.services.TermRelationship;



public class DummySynonymousTermsService implements TermInfoService {

    @Override
    public TermInfos getTermInfos(Collection<? extends Term> terms) {
        List<TermInfo> result = new ArrayList<TermInfo>();
        for (int i = 10; i < 20; i++) {
            result.add(new TermInfo(new Term("term" + i), TermRelationship.SYNONYM));
        }
        return new TermInfos(result);
    }

}
