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

class ShoutClient extends Client{
  boolean headerIsSent=false;
  MySocket mysocket=null;
  String srcmpoint=null;
  String dstmpoint=null;
  String dsthost=null;
  int dstport=0;
  String passwd=null;

  ShoutClient(String srcmpoint, String dsthost, int dstport, String passwd, String dstmpoint){
    super();
    try{
      Socket s=new Socket(dsthost, dstport); 
      mysocket=new MySocket(s);
    }
    catch(Exception e){
      close();
    }
    this.srcmpoint=srcmpoint;
    this.dstmpoint=dstmpoint;
    this.dsthost=dsthost;
    this.dstport=dstport;
    this.passwd=passwd;

    Source source=Source.getSource(srcmpoint);
    source.addListener(this);
    if(source instanceof Proxy){
      ((Proxy)source).kick();
    }
    if(source instanceof PlayFile){
      ((PlayFile)source).kick();
    }
    if(source.mountpoint!=null){
      HttpServer.client_connections++;
    }	
  }

  public void write(Vector http_header, byte[] header,
		    byte[] foo, int foostart, int foolength,
		    byte[] bar, int barstart, int barlength) throws IOException{
    lasttime=System.currentTimeMillis();
    ready=true;
    if(!headerIsSent){
      if(header==null){
        ready=false;
        return;
      }
      mysocket.println("SOURCE "+dstmpoint+" ICE/1.0");
      mysocket.println("ice-password: "+passwd);
      mysocket.println("ice-name: stream name");
      mysocket.println("ice-genre: genre");
      mysocket.println("ice-bitrate: 0");
      mysocket.println("ice-public: 0");
      mysocket.println("ice-description: A short description");
      for(int i=1; i<http_header.size(); i++){
        mysocket.println((String)(http_header.elementAt(i)));
      }
      mysocket.println("");
      mysocket.flush();

      mysocket.write(header, 0, header.length);
      headerIsSent=true;
    }
    mysocket.write(foo, foostart, foolength);
    mysocket.write(bar, barstart, barlength);
    mysocket.flush();
    ready=false;
  }

  public void close(){
    try{mysocket.close();}
    catch(Exception e){}
    mysocket=null;
    super.close();
  }

  public boolean isRunning(){ return (mysocket!=null);}
}
