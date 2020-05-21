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

import jroar.web.model.InfoProxy;
import jroar.web.model.InfoSource;

import java.io.*;
import java.util.*;

public class HomePage {
    // Interfaz web de la página principal. Muestra cada Source con su número de listeners y de conexiones relizadas

    public static List<InfoSource> newKick(){
        List<InfoSource> lSource = new LinkedList<>();
        Enumeration keys = Source.sources.keys();
        if (!keys.hasMoreElements()) {
            return lSource;
        }

        while (keys.hasMoreElements()) {
            String mountpoint = ((String) (keys.nextElement()));
            Source source = Source.getSource(mountpoint);
            if (source == null) continue;
            String source_name = source.source;
            String m3u = ogg2m3u(mountpoint);

            int listeners = source.getListeners();
            int connections = source.getConnections();

            Object[] proxies = source.getProxies();
            List<InfoProxy> lp = new LinkedList<>();
            boolean hasProxy = false;
            if (proxies != null) {
                hasProxy = true;
                for (int i = 0; i < proxies.length; i++) {
                    String foo = (String) (proxies[i]);
                    String host = getHost(foo);
                    String m3up = "";
                    if (host == null) {
                        m3up = ogg2m3u(foo);
                        lp.add(new InfoProxy(foo,m3up,"", false));
                    } else {
                        m3up = ogg2m3u(foo);
                        lp.add(new InfoProxy(foo,m3up,host, true));
                    }
                }
            }
            InfoSource iS = new InfoSource(mountpoint, source_name, m3u, listeners, connections,source.isVideo(), lp, hasProxy);
            lSource.add(iS);
        }

        return lSource;
    }

    private static String ogg2m3u(String ogg) {
        if (!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
        byte[] foo = ogg.getBytes();
        foo[foo.length - 1] = 'u';
        foo[foo.length - 2] = '3';
        foo[foo.length - 3] = 'm';
        return new String(foo);
    }

    private static final String _http = "http://";

    private static String getHost(String url) {
        if (!url.startsWith(_http)) return null;
        int foo = url.substring(_http.length()).indexOf('/');
        if (foo != -1) {
            return url.substring(0, _http.length() + foo + 1);
        }
        return null;
    }

}
