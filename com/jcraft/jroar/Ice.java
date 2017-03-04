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

class Ice extends Source{
  private static final int BUFSIZE=4096*2;

  private static final int TIMEOUT=3000; // 3 seconds

  private InputStream bitStream=null;
  private MySocket mysocket=null;

  private SyncState oy;
  private com.jcraft.jogg.Page og;
  
  private byte[] buffer=null;
  private int bytes=0;

  private Thread me=null;

  private long lasttime=0;

  Vector http_header=new Vector();

  private static final String _icepasswd="ice-password: ";
  private static final String _ice="ice-";
  Ice(String mountpoint, MySocket mysocket, Vector headerfromice){
    this(mountpoint, mysocket, headerfromice, "ICE/1.0");
  }
  Ice(String mountpoint, MySocket mysocket, Vector headerfromice, String protocol){
    super(mountpoint);

    this.mysocket=mysocket;
    this.bitStream=mysocket.getInputStream();
    this.source="ice at "+mysocket.socket.getInetAddress();

    boolean accept=false;
    String foo=null;
    int size=headerfromice.size();

    if(protocol==null || protocol.startsWith("ICE")){
      for(int i=0; i<size;){
        foo=(String)headerfromice.elementAt(i);
//System.out.println("fromIce: "+foo);
        if(foo.startsWith(_ice)){
 	  if(foo.startsWith(_icepasswd)){
            String icepasswd=foo.substring(_icepasswd.length());
            if(JRoar.icepasswd!=null && JRoar.icepasswd.equals(icepasswd)){
              accept=true;
	    }
          }
          headerfromice.removeElement(foo);
          size--;
          continue;
        }
        i++;
      }	
    }
    else if(protocol.startsWith("HTTP")){
      String _auth="Authorization: Basic ";
      for(int i=0; i<size;){
        foo=(String)headerfromice.elementAt(i);
System.out.println("fromIce: "+foo);
        if(foo.startsWith(_auth)){
          String icepasswd=foo.substring(_auth.length());
          byte[] code=null;
//        try{ code=(new sun.misc.BASE64Decoder()).decodeBuffer(icepasswd); }
//        catch(Exception ee){ }
	  try{
            byte[] bar=icepasswd.getBytes();	      
	    if(bar.length>0){
              code=fromBase64(bar, 0, bar.length);
	    }
	  }
          catch(Exception ee){ }
System.out.println("code: "+new String(code));
          if(JRoar.icepasswd!=null && 
	     code!=null &&
             (java.util.Arrays.equals(code, ("source:"+JRoar.icepasswd).getBytes())||
              java.util.Arrays.equals(code, ("relay:"+JRoar.icepasswd).getBytes()))
	     ){
            accept=true;
	  }
          headerfromice.removeElement(foo);
          size--;
          continue;
        }
        i++;
      }	
System.out.println("accept: "+accept);
      if(JRoar.icepasswd!=null && !accept){
        try { 
          mysocket.println("HTTP/1.0 401 Authentication Required");
          mysocket.println("www-authenticate: Basic realm=\"jroar\"");
          mysocket.println("");
   	  mysocket.flush();
        }
        catch(Exception e){}
        drop();
        mountpoint=null;
        try { if(mysocket!=null)mysocket.close(); } 
        catch(Exception e){}
        //mysocket=null;
        return;
      }
      try { 
        mysocket.println("HTTP/1.0 200 OK");
        mysocket.println("");
        mysocket.flush();
      }
      catch(Exception e){}
    }
    else {
      System.out.println("unkown protocol: "+protocol);
    }

    if(JRoar.icepasswd!=null && !accept){
      drop();
      mountpoint=null;
      try { if(mysocket!=null)mysocket.close(); } 
      catch(Exception e){}
      //mysocket=null;
      return;
    }

    http_header.addElement("HTTP/1.0 200 OK");
    http_header.addElement("Content-Type: application/x-ogg");
  }

  void init_ogg(){
    oy=new SyncState();
    og=new com.jcraft.jogg.Page();
    buffer=null;
    bytes=0;
    oy.init();
  }

  public void kick(){
    if(mountpoint==null) return;
    if(me!=null) return;
    me=Thread.currentThread();
    run();
  }

  public void run(){
    lasttime=System.currentTimeMillis();

    init_ogg();

    int serialno=-1;

    ByteArrayOutputStream _header=new ByteArrayOutputStream();
    byte[] header=null;

    while(me!=null){
      boolean eos=false;
      while(!eos){
        if(me==null)break;
        int index=oy.buffer(BUFSIZE);
        buffer=oy.data;
        try{ bytes=bitStream.read(buffer, index, BUFSIZE); }
        catch(Exception e){
//        System.err.println(e);
          if(me==null)break;
          bytes=-1;
        }

        if(bytes==-1)break;
        if(bytes==0)break;

        try{Thread.sleep(1);}  // sleep for green thread.
        catch(Exception e){}

        lasttime=System.currentTimeMillis();

        oy.wrote(bytes);

        while(!eos){
          if(me==null)break;

	  int result=oy.pageout(og);

	  if(result==0)break; // need more data
	  if(result==-1){ // missing or corrupt data at this page position
//	    System.err.println("Corrupt or missing data in bitstream; continuing...");
	  }
	  else{
  	    if(serialno!=og.serialno()){
              header=null;
              serialno=og.serialno();
	    }
          
            if((og.granulepos()==0)
               || (og.granulepos()==-1)          // hack for Speex
              ){ 

              _header.write(og.header_base, og.header, og.header_len);
              _header.write(og.body_base, og.body, og.body_len);
	    }
            else{
              if(header==null){
                header=_header.toByteArray();
                _header.reset();
                parseHeader(header);
	      }
	    }

//          synchronized(listeners){   // In some case, c.write will block.
  	      int size=listeners.size();

              Client c=null;
              for(int i=0; i<size;){
                try{
	          c=(Client)(listeners.elementAt(i));
                  c.write(http_header, header,
  			  og.header_base, og.header, og.header_len,
			  og.body_base, og.body, og.body_len);
		}
		catch(Exception e){
                  c.close();
                  removeListener(c);
                  size--;
                  continue;
                }
                i++;
	      }
//          }
	    if(og.eos()!=0)eos=true;
	  }
        }
      }
      if(bytes==-1) break;
    }

    oy.clear();
//    try { if(bitStream!=null)bitStream.close(); } 
//    catch(Exception e){}
//    bitStream=null;
    try { if(mysocket!=null)mysocket.close(); } 
    catch(Exception e){}
    mysocket=null;
    me=null;
    drop();
  }

  void stop(){
    if(me!=null){
      if(oy!=null) oy.clear();
      try { if(mysocket!=null)mysocket.close(); } 
      catch(Exception e){}
      mysocket=null;
      me=null;
    }
    drop();
  }

  void drop_clients(){
    Client c=null;
    synchronized(listeners){
      int size=listeners.size();
      for(int i=0; i<size;i++){
        c=(Client)(listeners.elementAt(i));
        try{ c.close();}
        catch(Exception e){}
      }
      listeners.removeAllElements();
    }
  }

  void drop(){
    drop_clients();
    super.drop();
  }

  void addListener(Client c){
    super.addListener(c);
    if((System.currentTimeMillis()-lasttime)>TIMEOUT){
System.out.println("drop[0]:  current="+System.currentTimeMillis()+", last="+lasttime);
	/*
      stop();
	*/
    }
  }
 
  boolean isAlive(){
    if((System.currentTimeMillis()-lasttime)>TIMEOUT){
System.out.println("drop[1]:  current="+System.currentTimeMillis()+", last="+lasttime);
	/*
      return false;
	*/
    }
    return true;
  }

  private static final byte[] b64 ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes();
  private static byte val(byte foo){
    if(foo == '=') return 0;
    for(int j=0; j<b64.length; j++){
      if(foo==b64[j]) return (byte)j;
    }
    return 0;
  }
  private static byte[] fromBase64(byte[] buf, int start, int length){
    byte[] foo=new byte[length];
    int j=0;
    int l=length;
    for(int i=start;i<start+length;i+=4){
      foo[j]=(byte)((val(buf[i])<<2)|((val(buf[i+1])&0x30)>>>4));
      if(buf[i+2]==(byte)'='){ j++; break;}
      foo[j+1]=(byte)(((val(buf[i+1])&0x0f)<<4)|((val(buf[i+2])&0x3c)>>>2));
      if(buf[i+3]==(byte)'='){ j+=2; break;}
      foo[j+2]=(byte)(((val(buf[i+2])&0x03)<<6)|(val(buf[i+3])&0x3f));
      j+=3;
    }
    byte[] bar=new byte[j];
    System.arraycopy(foo, 0, bar, 0, j);
    return bar;
  }
}
