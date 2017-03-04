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

import com.jcraft.jogg.*;

class Store extends Page{
  static void register(){
    register("/store", Mount.class.getName());
  }

  String source=null;
  Vector header=new Vector();
  byte[] content=null;

  Store(String mountpoint, String source){
    this.source=source;
    store(mountpoint, source);
  }

  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
    if(content==null){
      String mountpoint=(String)vars.get("mountpoint");
      String source=(String)vars.get("source");
      String passwd=(String)vars.get("passwd");
      if(passwd==null || !passwd.equals(JRoar.passwd)){
        forward(s, "/");
         return;
      }
      store(mountpoint, source);
      forward(s, "/");
      return;
    }

//    System.out.println("! header:"+ header);
    s.pn( "HTTP/1.0 200 OK" );
    for(int i=0; i<header.size(); i++){
//    System.out.println("i="+i);
      String foo=(String)header.elementAt(i);
      s.pn(foo);
//    System.out.println(foo);
    }
    s.pn( "" ) ;
//    System.out.println("!! content:"+ content+" ,"+content.length);
    s.p(content);
//    System.out.println("!|");
    s.flush();
    s.close();
  }

  void store(String mountpoint, String source){
    try{
      URL url=new URL(source);
      URLConnection urlc=url.openConnection();
      String foo=urlc.getHeaderField(0); // HTTP/1.0 200 OK
      InputStream bitStream=urlc.getInputStream();

      if(foo.indexOf(" 200 ")==-1){
        bitStream.close();
        return;
      }
  
      int i=0;
      String s=null;
      String t=null;
      while(true){
        s=urlc.getHeaderField(i);
        t=urlc.getHeaderFieldKey(i);
        if(s==null)break;
        // System.out.println("header: "+t+": "+s);
        header.addElement((t==null?s:(t+": "+s)));
        i++;
      }

      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      byte[] buffer=new byte[1024];
      int bytes=0;
      try{
        while(true){
          bytes=bitStream.read(buffer, 0, 1024);
          if(bytes==-1)break;
          bos.write(buffer, 0, bytes);
        }
      }
      catch(Exception ee){ }

      bitStream.close();
      bos.close();

      content=bos.toByteArray();

      register(mountpoint, this);
    }
    catch(Exception e){
    }
  }

    /*
  public String toString(){
    return super.toString()+" me="+me+", bitStream="+bitStream;
  }
    */
}
