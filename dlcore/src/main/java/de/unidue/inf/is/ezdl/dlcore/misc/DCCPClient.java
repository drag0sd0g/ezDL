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

package de.unidue.inf.is.ezdl.dlcore.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;



/**
 * DCCPClient retrieves connection info for specific protocols from the DCCP
 * server.
 * 
 * @author mjordan
 */
public class DCCPClient {

    /**
     * Retrieves connection information for the GUI client.
     * <p>
     * *
     * 
     * @param dccpUrl
     *            the URL at which to find the DCCP
     * @return
     */
    public static URL getConnectionInfoForGui(String dccpUrl) {
        URL mtaUrl = null;
        try {
            URL url = new URL(dccpUrl + "/gui");
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String answer = rd.readLine();
            mtaUrl = new URL(answer);
        }
        catch (MalformedURLException e) {
        }
        catch (IOException e) {
        }
        return mtaUrl;
    }


    public static void main(String[] args) {
        System.out.println(DCCPClient.getConnectionInfoForGui("http://localhost:1337"));
    }
}
