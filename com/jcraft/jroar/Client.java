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

abstract class Client{
  static Vector clients=new Vector();
  String proxy=null;
  long lasttime=0;
  boolean ready=false;

  Client(){
    lasttime=System.currentTimeMillis();
    synchronized(clients){
      clients.addElement(this);
    }
  }

  abstract void write(Vector http_header, byte[] header,
	     byte[] foo, int foostart, int foolength,
	     byte[] bar, int barstart, int barlength) throws IOException;
  void close(){
    synchronized(clients){
      clients.removeElement(this);
    }
  }
  abstract boolean isRunning();
}
