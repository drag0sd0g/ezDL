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

package de.unidue.inf.is.ezdl.dlcore.message.content;

import java.util.List;

import de.unidue.inf.is.ezdl.dlcore.data.wrappers.FrontendWrapperInfo;



public class AvailableWrappersTell implements MessageContent {

    private static final long serialVersionUID = 884017566888067806L;

    private List<FrontendWrapperInfo> wrapperInfos;


    public AvailableWrappersTell(List<FrontendWrapperInfo> wrapperInfos) {
        super();
        this.wrapperInfos = wrapperInfos;
    }


    public List<FrontendWrapperInfo> getWrapperInfos() {
        return wrapperInfos;
    }


    public void setWrapperInfos(List<FrontendWrapperInfo> wrapperInfo) {
        this.wrapperInfos = wrapperInfo;
    }


    @Override
    public String toString() {
        return wrapperInfos.toString();
    }

}
