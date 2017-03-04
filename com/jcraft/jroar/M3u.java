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

class M3u extends Page{
  String pls=null;
  static void register(){
  }

  M3u(String pls){
    super();
    this.pls=pls;
  }
  public void kick(MySocket ms, Hashtable vars, Vector h) throws IOException{
    byte[] foo=pls.getBytes();
    foo[foo.length-1]='g'; foo[foo.length-2]='g'; foo[foo.length-3]='o';
    String ogg=new String(foo);
    Source source=Source.getSource(ogg);
    if(source==null){
      foo[foo.length-1]='x'; foo[foo.length-2]='p'; foo[foo.length-3]='s';
      ogg=new String(foo);
      source=Source.getSource(ogg);
    }
    if(source!=null){
      ms.println("HTTP/1.0 200 OK") ;
      ms.println("Connection: close") ;
      ms.println("Content-Type: audio/x-mpegurl") ;
      ms.println("") ;
      ms.println(HttpServer.myURL+ogg);
      ms.flush( ) ;
      ms.close( ) ;
    }
    else{
      notFound(ms);
    }
  }

  static String getURL(String m3u){
    InputStream pstream=null;
    if(m3u.startsWith("http://")){
      try{
        URL url=new URL(m3u);
        URLConnection urlc=url.openConnection();
        pstream=urlc.getInputStream();
      }
      catch(Exception ee){
        System.err.println(ee); 	    
        return null;
      }
    }
    if(pstream==null) return null;
    String line=null;
    while(true){
      try{line=readline(pstream);}catch(Exception e){}
      if(line==null)break;
      break;
    }
    try{pstream.close();}catch(Exception e){}
    return line;
  }     
  static private String readline(InputStream is) {
    StringBuffer rtn=new StringBuffer();
    int temp;
    do {
      try {temp=is.read();}
      catch(Exception e){return(null);}
      if(temp==-1){ return(null);}
      if(temp!=0 && temp!='\n')rtn.append((char)temp);
    }while(temp!='\n');                                                        
    return(rtn.toString());
  }
}
