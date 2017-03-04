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

class Stats extends Page{
  static void register(){
    register("/stats.xml", Stats.class.getName());
  }

  static final private char[] _lt="<".toCharArray();
  static final private char[] _gt=">".toCharArray();
  static final private char[] _ltslash="</".toCharArray();

  static final private char[] _client_connections="client_connections".toCharArray();
  static final private char[] _limit="limit".toCharArray();
  static final private char[] _connections="connections".toCharArray();
  static final private char[] _source_connections="source_connections".toCharArray();
  static final private char[] _sources="sources".toCharArray();
  static final private char[] _listeners="listeners".toCharArray();

  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
/*
    StringBuffer sb=new StringBuffer();
    sb.append("<?xml version=\"1.0\"?>\n");
    sb.append("<icestats>\n");

    if(HttpServer.client_connections>0){
      indent(sb, 1);
      wrapln(sb, _client_connections, HttpServer.client_connections);
    }

    indent(sb, 1); 
    wrapln(sb, _connections, HttpServer.connections);

    synchronized(Source.sources){
      if(HttpServer.source_connections>0){
      indent(sb, 1); 
      wrapln(sb, _source_connections, HttpServer.source_connections);

      Enumeration keys=Source.sources.keys();
      indent(sb, 1); 
      wrapln(sb, _sources, Source.sources.size());

      if(keys.hasMoreElements()){ 
        for(; keys.hasMoreElements();){
          String mount=((String)(keys.nextElement()));
          Source source=(Source)(Source.getSource(mount));
          indent(sb, 1); sb.append("<source mount=\""+mount+"\">"); ln(sb);

          if(source.getLimit()!=0){
            indent(sb, 2);
            wrapln(sb, _limit, source.getLimit());
	  }

          indent(sb, 2);
          wrapln(sb, _connections, source.getConnections());

          indent(sb, 2);
          wrapln(sb, _listeners, source.getListeners());

          indent(sb, 1); sb.append("</source>"); ln(sb);
	}
      }
      }
    }
    sb.append("</icestats>\n");
    sb.append("\n");
*/

    StringBuffer sb=new StringBuffer();
    sb.append("<?xml version=\"1.0\"?>");
    sb.append("<icestats>");
    if(HttpServer.client_connections>0){
      wrap(sb, _client_connections, HttpServer.client_connections);
    }
    wrap(sb, _connections, HttpServer.connections);

    synchronized(Source.sources){
      if(HttpServer.source_connections>0){
      wrap(sb, _source_connections, HttpServer.source_connections);
      Enumeration keys=Source.sources.keys();
      wrap(sb, _sources, Source.sources.size());

      if(keys.hasMoreElements()){ 
        for(; keys.hasMoreElements();){
          String mount=((String)(keys.nextElement()));
          Source source=(Source)(Source.getSource(mount));
          sb.append("<source mount=\""+mount+"\">");

          if(source.getLimit()!=0){
            wrap(sb, _limit, source.getLimit());
	  }
          wrap(sb, _connections, source.getConnections());
          wrap(sb, _listeners, source.getListeners());
          sb.append("</source>");
	}
      }
      }
    }
    sb.append("</icestats>");

    String foo=sb.toString();

    s.println("HTTP/1.0 200 OK" );
    s.println("Content-Length: "+foo.length());
    s.println("Content-Type: text/html");
    s.println("") ;
    s.print(foo);
    s.flush();
    s.close();
  }

  static final private char[] blank="  ".toCharArray();
  private void  indent(StringBuffer sb, int foo){
    for(int i=0; i<foo; i++){
      sb.append(blank);
    }
  }

  private void  wrap(StringBuffer sb, char[] tag, int foo){
    //sb.append("<"+tag+">"+foo+"</"+tag+">");
    sb.append(_lt); sb.append(tag); sb.append(_gt);
    sb.append(foo);
    sb.append(_ltslash); sb.append(tag); sb.append(_gt);
    return;
  }
  private void  wrap(StringBuffer sb, char[] tag, String foo){
    //sb.append("<"+tag+">"+foo+"</"+tag+">");
    sb.append(_lt); sb.append(tag); sb.append(_gt);
    sb.append(foo);
    sb.append(_ltslash); sb.append(tag); sb.append(_gt);
    return;
  }
  private void  wrapln(StringBuffer sb, String tag, int foo){
    wrapln(sb, tag.toCharArray(), foo);
  }
  private void  wrapln(StringBuffer sb, String tag, String foo){
    wrapln(sb, tag.toCharArray(), foo);
  }
  private void  wrapln(StringBuffer sb, char[] tag, int foo){
    wrap(sb, tag, foo); ln(sb);
  }
  private void  wrapln(StringBuffer sb, char[] tag, String foo){
    wrap(sb, tag, foo); ln(sb);
    return;
  }
  static final char[] _ln="\n".toCharArray();
  private void  ln(StringBuffer sb){
    sb.append(_ln);
    return;
  }
}
