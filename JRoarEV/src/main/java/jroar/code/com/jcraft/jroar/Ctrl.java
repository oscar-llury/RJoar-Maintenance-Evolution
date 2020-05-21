/* -*-mode:java; c-basic-offset:2; -*- */
/* JRoar -- pure Java streaming server for Ogg
 *
 * Copyright (C) 2001,2002 ymnk, JCraft,Inc.
 *
 * Written by: 2001,2002 ymnk<ymnk@jcraft.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package jroar.code.com.jcraft.jroar;

import jroar.web.model.InfoClient;

import java.io.*;
import java.util.*;

public class Ctrl {
// Es la página web que sirve como panel de control: Muestra los mountpoints

    public static List<InfoClient> getClients() {
        List<InfoClient> lClient = new LinkedList<>();
        for (int i = 0; i < Client.clients.size(); i++) {
            Client c = ((Client) (Client.clients.elementAt(i)));
            if (c instanceof ShoutClient) {
                ShoutClient sc = (ShoutClient) c;
                String srcmpoint = sc.srcmpoint;
                String dsthost = sc.dsthost;
                int dstport = sc.dstport;
                String dstmpoint = sc.dstmpoint;
                String url = "http://" + sc.dsthost + ":" + sc.dstport + sc.dstmpoint;
                lClient.add(new InfoClient(srcmpoint, dsthost, dstport, dstmpoint, url));
            }
        }
        return lClient;
    }

    private String ogg2m3u(String ogg) {
        // Convierte un .ogg a un .m3u
        if (!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
        byte[] foo = ogg.getBytes();
        foo[foo.length - 1] = 'u';
        foo[foo.length - 2] = '3';
        foo[foo.length - 3] = 'm';
        return new String(foo);
    }

    private static final String _http = "http://";

    private String getHost(String url) {
        if (!url.startsWith(_http)) return null;
        int foo = url.substring(_http.length()).indexOf('/');
        if (foo != -1) {
            return url.substring(0, _http.length() + foo + 1);
        }
        return null;
    }
}
