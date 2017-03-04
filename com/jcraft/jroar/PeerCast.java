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

class PeerCast {
  static String lookupHost="localhost:7144";
  static void setLookupHost(String arg){
    if(arg==null) return;
    if(arg.indexOf(':')==-1){
      arg=arg+":7144";
    }
    lookupHost=arg;
  }
  static String getURL(String peercast){
    InputStream pstream=null;
    if(peercast.startsWith("peercast://")){
      peercast="http://"+lookupHost+peercast.substring("peercast:/".length());
    }
    try{
      URL url=new URL(peercast);
      URLConnection urlc=url.openConnection();
      pstream=urlc.getInputStream();
    }
    catch(Exception ee){
      System.err.println(ee); 	    
      return null;
    }
    if(pstream==null) return null;
    String line=null;
    String tmp=null;
    while(true){
      try{tmp=readline(pstream);}catch(Exception e){}
      if(tmp==null)break;
      if(tmp.endsWith(".ogg")){
        line=tmp;
      }
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
      if(temp!=0 && temp!='\n' && temp!='\r')rtn.append((char)temp);
    }
    while(temp!='\n');                                                        
    return(rtn.toString());
  }
}
