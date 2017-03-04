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
import java.util.*;
import java.io.*;

public abstract class Page{
  static Hashtable map=new Hashtable();

  static void register(){}

  static void register(String src, Object dst){
    synchronized(map){
      map.put(src, dst);
    }
  }

  static Object map(String foo){
    synchronized(map){
      return map.get(foo);
    }
  }

  String decode(String arg){

    byte[] foo=arg.getBytes();
    StringBuffer sb=new StringBuffer();
    for(int i=0; i<foo.length; i++){
      if(foo[i]=='+'){ sb.append((char)' '); continue; }
      if(foo[i]=='%' && i+2<foo.length){
        int bar=foo[i+1];
	bar=('0'<=bar && bar<='9')? bar-'0' : 
	    ('a'<=bar && bar<='z')? bar-'a'+10 : 
	    ('A'<=bar && bar<='Z')? bar-'A'+10 : bar;
	bar*=16;
	int goo=foo[i+2];
	goo=('0'<=goo && goo<='9')? goo-'0' : 
	    ('a'<=goo && goo<='f')? goo-'a'+10 : 
	    ('A'<=goo && goo<='F')? goo-'A'+10 : goo;
	bar+=goo; bar&=0xff;
        sb.append((char)bar);
        i+=2;
        continue;
      }
      sb.append((char)foo[i]);
    }
    return sb.toString();
  }

  static void forward(MySocket mysocket, String location) throws IOException{
    mysocket.println("HTTP/1.0 302 Found");
    //mysocket.println("Location: "+HttpServer.myURL+location);
    mysocket.println("Location: "+location);
    mysocket.println("");
    mysocket.flush();
    mysocket.close();
  }

  static void unknown(MySocket mysocket, String location) throws IOException{
    mysocket.println("HTTP/1.0 404 Not Found");
    mysocket.println("Connection: close");
    mysocket.println("Content-Type: text/html; charset=iso-8859-1");
    mysocket.println("");
    mysocket.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    mysocket.println("<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>");
    mysocket.println("<H1>Not Found</H1>");
    mysocket.println("The requested URL "+location+" was not found on this server.<P>");
    mysocket.println("<HR>");
    mysocket.println("<ADDRESS>JRoar at "+HttpServer.myURL+"/</ADDRESS>");
    mysocket.println("</BODY></HTML>");
    mysocket.flush();
    mysocket.close();
  }

  static void ok(MySocket mysocket, String location) throws IOException{
    mysocket.println("HTTP/1.0 200 OK");
    mysocket.println("Last-Modified: Thu, 04 Oct 2001 14:09:23 GMT");
    mysocket.println("Connection: close");
//    mysocket.println("Content-Type: text/html; charset=iso-8859-1");
    mysocket.println("");
    mysocket.flush();
    mysocket.close();
  }

  abstract void kick(MySocket mysocket, Hashtable ht, Vector v) throws IOException;

  Hashtable getVars(String arg){

    Hashtable vars=new Hashtable();
    vars.put("jroar-method", "GET");
    if(arg==null) return vars;

    arg=decode(arg);

    int foo=0;
    int i=0;
    int c=0;

    String key, value;

    while(true){
      key=value=null; 

      foo=arg.indexOf('=');
      if(foo==-1)break;
      key=arg.substring(0, foo);
      arg=arg.substring(foo+1);

      foo=arg.indexOf('&');
      if(foo!=-1){
	value=arg.substring(0, foo);
        arg=arg.substring(foo+1);
      }
      else value=arg;

      vars.put(key, value);

      if(foo==-1)break;
    }
    return vars;
  }

  Hashtable getVars(MySocket mysocket, int len){
    Hashtable vars=new Hashtable();
    vars.put("jroar-method", "POST");
    if(len==0) return vars;

    int i=0;
    int c=0;
    StringBuffer sb=new StringBuffer();
    String key, value;

    while(i<len){
      key=value=null; 
      sb.setLength(0);
      while(i<len){
        c=mysocket.readByte( ); i++;
        if(c=='='){
          key=sb.toString();
          break;
	}
	sb.append((char)c);
      }
      sb.setLength(0);
      while(i<len){
        c=mysocket.readByte( ); i++;
        if(c=='&'){
          value=sb.toString();
          break;
	}
	sb.append((char)c);
      }
      if(key!=null && value!=null){
        key=decode(key); value=decode(value);
        vars.put(key, value);
      }
    }
    return vars;
  }

  static void notFound(MySocket ms) throws IOException{
    ms.println("HTTP/1.0 404 Not Found") ;
    ms.println("Content-Type: text/html") ;
    ms.println("") ;
    ms.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    ms.println("<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>");
    ms.println("<H1>Not Found</H1>The requested URL was not found on this server.<HR>");
    ms.println("</BODY></HTML>");
    ms.flush();
    ms.close();
  }
}
