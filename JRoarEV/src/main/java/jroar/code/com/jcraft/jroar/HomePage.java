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

import java.io.*;
import java.util.*;

class HomePage extends Page {
    // Interfaz web de la página principal. Muestra cada Source con su número de listeners y de conexiones relizadas
    static void register() {
        register("/", HomePage.class.getName());
        register("/index.html", HomePage.class.getName());
    }

    private static int count = 0;

    public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException {
        count++;
        s.pn("HTTP/1.0 200 OK");
        s.pn("Content-Type: text/html");
        s.pn("");
        s.pn("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
        s.pn("<HTML><HEAD>");
        s.p("<TITLE>JRoar ");
        s.p(JRoar.version);
        s.p(" at ");
        s.p(HttpServer.myURL);
        s.pn("/</TITLE>");
        s.pn("</HEAD><BODY>");
        s.p("<h1>JRoar ");
        s.p(JRoar.version);
        s.p(" at ");
        s.p(HttpServer.myURL);
        s.pn("/</h1>");

        Enumeration keys = Source.sources.keys();
        if (!keys.hasMoreElements()) {
            s.pn("There is no mount point.<br>");
        }

        s.pn("<table cellpadding=3 cellspacing=0 border=0>");
        while (keys.hasMoreElements()) {
            String mountpoint = ((String) (keys.nextElement()));
            Source source = Source.getSource(mountpoint);
            if (source == null) continue;
            String source_name = source.source;

            s.pn("<tr>");

            s.pn("<td align=left nowrap>");
            s.p("<a href=");
            s.p(ogg2m3u(mountpoint));
            s.p(">");
            s.p(mountpoint);

            s.p("</a>");
            s.p("&nbsp;");
            s.p("(");
            s.p(source.getListeners());
            s.p(",");
            s.p(source.getConnections());
            s.p(")");
            s.pn("</td>");

            s.pn("<td nowrap> &lt;--- </td>");

            if (source instanceof Proxy) {
                s.pn("<td align=left>");
                s.p("<a href=");
                s.p(source_name);
                s.p(">");
                s.p(source_name);
                s.p("</a>");
                s.pn("</td>");

            } else {
                s.p("<td align=left>");
                s.p(source_name);
                s.pn("</td>");
            }
            s.pn("</tr>");

            Object[] proxies = source.getProxies();
            if (proxies != null) {
                for (int i = 0; i < proxies.length; i++) {
                    String foo = (String) (proxies[i]);
                    s.pn("<tr>");
                    s.pn("<td>&nbsp;</td>");
                    s.pn("<td nowrap>---&gt</td>");
                    String host = getHost(foo);
                    if (host == null) {
                        s.p("<td><a href=");
                        s.p(ogg2m3u(foo));
                        s.p(">");
                        s.p(foo);
                        s.pn("</a></td>");
                    } else {
                        s.p("<td><a href=");
                        s.p(ogg2m3u(foo));
                        s.p(">");
                        s.p(foo.substring(host.length() - 1));
                        s.p("</a>&nbsp;at&nbsp;");
                        s.p("<a href=");
                        s.p(host);
                        s.p(">");
                        s.p(host);
                        s.pn("</a></td>");
                    }
                    s.pn("</tr>");
                }
            }

        }
        s.pn("</table>");

        s.pn("<hr width=80%>");

        s.pn("<table width=100%>");
        s.pn("<tr><td align=\"right\"><a href=\"/ctrl.html\">Control</a></td></tr>");
        s.p("<tr><td align=\"right\"><small><i>");
        s.p(count);
        s.pn("</i></small></td></tr>");
        s.pn("</table>");

        s.pn("</BODY></HTML>");
        s.flush();
        s.close();

    }


    private String ogg2m3u(String ogg) {
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
