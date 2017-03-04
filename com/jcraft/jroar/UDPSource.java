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

class UDPSource extends Source {
  byte[] foo=new byte[0];
  UDPBroadcast b=null;
  UDPSource(UDPBroadcast b, String mpoint) {
    super(mpoint);
    this.b=b;
  }

  synchronized void addListener(Client c){
    super.addListener(c);
    Vector http_header=new Vector();
    http_header.addElement("HTTP/1.0 200 OK" );
    http_header.addElement("udp-port: "+b.port);
    http_header.addElement("udp-broadcast-address: "+b.baddress);
    http_header.addElement("Content-Type: application/x-ogg");

    try{
      c.write(http_header, b.header, foo, 0, 0, foo, 0, 0);
      c.close();
    }
    catch(Exception e){
    }
    removeListener(c);
  }
}
