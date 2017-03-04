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

class Debug extends Page{
  static void register(){
    register("/debug.html", Debug.class.getName());
  }

  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
    s.println( "HTTP/1.0 200 OK" );
    s.println( "Content-Type: text/html" );
    s.println( "" ) ;
    s.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    s.println("<HTML><HEAD>");
    s.println("<TITLE>JRoar</TITLE>");
    s.println("</HEAD><BODY>");
    s.println( "<h1>Debug</h1>" );
    s.println( "<pre>" );
//    s.println("PlayFile.status: "+PlayFile.status+"<br>");
//    s.println("PlayFile.file: "+PlayFile.file+"<br>");

    Source source;
    Enumeration sources=Source.sources.elements();
    for(; sources.hasMoreElements();){
      source=((Source)sources.nextElement());
      s.println("Source: "+source);
      s.println("         "+source.listeners);
    }
//s.println("<p>");
//
//s.println("java.version:" +System.getProperty("java.version"));

s.println("<p>");

    if(JRoar.wd!=null){  
      s.println("JRoar.wd.isAlive() " +JRoar.wd.isAlive());
      if(!JRoar.wd.isAlive()){
        JRoar.wd.start();
      }
    }

    s.println( "</pre>" );
    s.println("</BODY></HTML>");
    s.flush();
    s.close();
  }

  static final private String blank="  ";
  private void  indent(StringBuffer sb, int foo){
    for(int i=0; i<foo; i++){
      sb.append(blank);
    }
  }
  private void  wrap(StringBuffer sb, String tag, String  foo){
    sb.append("<"+tag+">"+foo+"</"+tag+">");
    return;
  }
  private void  wrapln(StringBuffer sb, String tag, String  foo){
    wrap(sb, tag, foo);
    ln(sb);
    return;
  }
  static final String _ln="\n";
  private void  ln(StringBuffer sb){
    sb.append(_ln);
    return;
  }
}
