/* -*-mode:java; c-basic-offset:2; -*- */
/* JRoar -- pure Java streaming server for Ogg 
 *
 * Copyright (C) 2001 ymnk, JCraft,Inc.
 *
 * Written by: 2001 ymnk<ymnk@jcraft.com>
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
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

class UDPPage extends Page{
  static void register(){
    register("/udp", UDPPage.class.getName());
  }
  public void kick(MySocket ms, Hashtable vars, Vector h) throws IOException{
    String srcmpoint=(String)vars.get("srcmpoint");
    String _port=(String)vars.get("port");
    String baddress=(String)vars.get("baddress");
    String dstmpoint=(String)vars.get("dstmpoint");
    String passwd=(String)vars.get("passwd");
    if(passwd==null || !passwd.equals(JRoar.passwd)){
      forward(ms, "/");
      return;
    }

    int port=0;
    try{ port=Integer.parseInt(_port);}
    catch(Exception e){ }
    if(srcmpoint!=null && 
       srcmpoint.length()>0 && 
       Source.getSource(srcmpoint)!=null &&
       port!=0 &&
       baddress!=null && baddress.length()>0 &&
       Page.map(dstmpoint)==null &&
       dstmpoint!=null && 
       dstmpoint.startsWith("/")){
      UDPBroadcast u=new UDPBroadcast(srcmpoint,
  				      baddress,
				      port,
				      dstmpoint);
    }
    forward(ms, "/");
  }
}
