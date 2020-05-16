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
import java.net.*;
import java.util.*;

class M3u extends Page {
    String pls = null;

    static void register() {
    }

    M3u(String pls) {
        super();
        this.pls = pls;
    }

    public void kick(MySocket ms, Hashtable vars, Vector h) throws IOException {
        //Busca un fichero  en ogg en Sources, si no lo busca terminado en spx.
        //Si lo encuentra devuevle HTTP OK por el MySocket que recibe de parametro de entrada y si no lo encuentra llama notFound,
        // método heredado de la clase abstracta Page que devuelve un HTTP NOT FOUND por el MySockect que se le pasa de parámetro

        byte[] foo = pls.getBytes();
        foo[foo.length - 1] = 'g';
        foo[foo.length - 2] = 'g';
        foo[foo.length - 3] = 'o';
        String ogg = new String(foo);
        Source source = Source.getSource(ogg);
        if (source == null) {
            foo[foo.length - 1] = 'x';
            foo[foo.length - 2] = 'p';
            foo[foo.length - 3] = 's';
            ogg = new String(foo);
            source = Source.getSource(ogg);
        }
        if (source != null) {
            ms.println("HTTP/1.0 200 OK");
            ms.println("Connection: close");
            ms.println("Content-Type: audio/x-mpegurl");
            ms.println("");
            ms.println(HttpServer.myURL + ogg);
            ms.flush();
            ms.close();
        } else {
            notFound(ms);
        }
    }

    static String getURL(String m3u) {
        //Método que recibe un String como parámetro de entrada y si empieza por http:// considera que es uns url e intenta conectarse a ella
        // si no logra conectarse devuelve null, si logra conectarse accede al flujo de entrada de la conexión (getInputStream())
        // si este flujo esta a null devuelve null y si no lee una linea de este flujo (con readline) y la devuelve después de cerrar el flujo
        InputStream pstream = null;
        if (m3u.startsWith("http://")) {
            try {
                URL url = new URL(m3u);
                URLConnection urlc = url.openConnection();
                pstream = urlc.getInputStream();
            } catch (Exception ee) {
                System.err.println(ee);
                return null;
            }
        }
        if (pstream == null) return null;
        String line = null;
        while (true) {
            try {
                line = readline(pstream);
            } catch (Exception e) {
                System.out.println(e);
            }
            if (line == null) break;
            break;
        }
        try {
            pstream.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return line;
    }
}
