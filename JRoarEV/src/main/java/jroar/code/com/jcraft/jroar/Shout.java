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

public class Shout {

    static public void newKick(String srcmpoint, String dst, String ice_passwd) {
        if (srcmpoint == null ||
                dst == null ||
                ice_passwd == null ||
                !dst.startsWith("ice://")) {
            return;
        }
        dst = dst.substring(6);
        if (dst.indexOf('/') == -1) {
            return;
        }
        String dstmpoint = dst.substring(dst.indexOf('/')); // coge el mountpoint del destino
        dst = dst.substring(0, dst.indexOf('/'));
        int port = 80;
        if (dst.indexOf(':') != -1) {
            String _port = dst.substring(dst.indexOf(':') + 1);
            dst = dst.substring(0, dst.indexOf(':')); // coge la IP
            if (_port.length() == 0) _port = "80";
            try {
                port = Integer.parseInt(_port); // coge el puerto
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        ShoutClient sc = new ShoutClient(srcmpoint, // src mount point
                dst,       // dst ip address
                port,      // dst port number
                ice_passwd,// passwd
                dstmpoint);// dst mount point
    }

}
