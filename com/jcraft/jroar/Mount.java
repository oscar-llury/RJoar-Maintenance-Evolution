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
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

class Mount extends Page{
  static void register(){
    register("/mount", Mount.class.getName());
  }

  public void kick(MySocket ms, Hashtable vars, Vector h) throws IOException{

    String mountpoint=(String)vars.get("mountpoint");
    String source=(String)vars.get("source");
    String passwd=(String)vars.get("passwd");

    if(passwd==null || !passwd.equals(JRoar.passwd)){
      forward(ms, "/");
      return;
    }
    String livestream=(String)vars.get("livestream");
    int limit=0;
    {
      String _limit=(String)vars.get("limit");
      if(_limit!=null){
        try{ limit=Integer.parseInt(_limit); }
        catch(Exception e){}
      }
    }

//System.out.println("livestream="+livestream);

    if(mountpoint!=null && 
       source!=null && 
       (source.startsWith("http://") ||
	source.startsWith("peercast://")) && 
       Page.map(mountpoint)==null &&
       mountpoint.startsWith("/") && 
       Source.getSource(mountpoint)==null){
      if(livestream!=null && livestream.equals("true")){
        Proxy proxy=new Proxy(mountpoint, source);
        if(limit!=0){
          proxy.setLimit(limit);
	}
      }
      else{
        PlayFile p=new PlayFile(mountpoint, source);
        if(limit!=0){
          p.setLimit(limit);
	}
        p.kick();
      }

      if(((String)vars.get("jroar-method")).equals("GET")){
        Source s=Source.getSource(mountpoint);
        s.addListener(new HttpClient(ms, h, mountpoint));
        if(s instanceof Proxy){
          ((Proxy)s).kick();
          return;
        }
      }

    }
    forward(ms, "/");
    return;
  }

}
