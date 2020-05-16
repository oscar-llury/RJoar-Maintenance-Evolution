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

package com.jcraft.jroar;

import java.io.*;
import java.util.*;

class Stats extends Page {
    static void register() {
        register("/stats.xml", Stats.class.getName());
    }

    private static final char[] _lt = "<".toCharArray();
    private static final char[] _gt = ">".toCharArray();
    private static final char[] _ltslash = "</".toCharArray();

    private static final char[] _client_connections = "client_connections".toCharArray();
    private static final char[] _limit = "limit".toCharArray();
    private static final char[] _connections = "connections".toCharArray();
    private static final char[] _source_connections = "source_connections".toCharArray();
    private static final char[] _sources = "sources".toCharArray();
    private static final char[] _listeners = "listeners".toCharArray();

    public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<icestats>");
        if (HttpServer.client_connections > 0) {
            wrap(sb, _client_connections, HttpServer.client_connections);
        }
        wrap(sb, _connections, HttpServer.connections);

        synchronized (Source.sources) {
            if (HttpServer.source_connections > 0) {
                wrap(sb, _source_connections, HttpServer.source_connections);
                Enumeration keys = Source.sources.keys();
                wrap(sb, _sources, Source.sources.size());

                if (keys.hasMoreElements()) {
                    while (keys.hasMoreElements()) {
                        String mount = ((String) (keys.nextElement()));
                        Source source = (Source) (Source.getSource(mount));
                        sb.append("<source mount=\"" + mount + "\">");

                        if (source.getLimit() != 0) {
                            wrap(sb, _limit, source.getLimit());
                        }
                        wrap(sb, _connections, source.getConnections());
                        wrap(sb, _listeners, source.getListeners());
                        sb.append("</source>");
                    }
                }
            }
        }
        sb.append("</icestats>");

        String foo = sb.toString();

        s.println("HTTP/1.0 200 OK");
        s.println("Content-Length: " + foo.length());
        s.println("Content-Type: text/html");
        s.println("");
        s.print(foo);
        s.flush();
        s.close();
    }

    private void wrap(StringBuffer sb, char[] tag, int foo) {
        sb.append(_lt);
        sb.append(tag);
        sb.append(_gt);
        sb.append(foo);
        sb.append(_ltslash);
        sb.append(tag);
        sb.append(_gt);
    }
}
