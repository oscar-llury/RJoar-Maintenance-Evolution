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

class UDPBroadcast extends Client{
  boolean headerIsSent=false;
  MySocket mysocket=null;

  String srcmpoint=null;
  String dstmpoint=null;
  String baddress=null;
  int port=0;
  String passwd=null;
  byte[] header=null;

  UDPIO io=null;
  UDPSource udp_mpoint=null;

  UDPBroadcast(String srcmpoint, String baddress, int port, String dstmpoint){
    super();

    try{
      io=new UDPIO(baddress, port);
    }
    catch(Exception e){
      close();
    }

    this.srcmpoint=srcmpoint;
    this.dstmpoint=dstmpoint;
    this.baddress=baddress;
    this.port=port;

    Source source=Source.getSource(srcmpoint);
    source.addListener(this);
    if(source instanceof Proxy)
      ((Proxy)source).kick();

    udp_mpoint=new UDPSource(this, dstmpoint);
  }

  public void write(Vector http_header, byte[] header,
		    byte[] foo, int foostart, int foolength,
		    byte[] bar, int barstart, int barlength) throws IOException{
    lasttime=System.currentTimeMillis();
    ready=true;
    this.header=header;
    io.write(foo, foostart, foolength);
    io.write(bar, barstart, barlength);
    io.flush();
    ready=false;
  }

  public void close(){
    try{io.close();}
    catch(Exception e){}
    io=null;
    super.close();
  }
  public boolean isRunning(){ return (io!=null);}


  class UDPIO{
    InetAddress address;
    DatagramSocket socket = null;
    DatagramPacket sndpacket;
    DatagramPacket recpacket;
    byte[] buf = new byte[1024];
    String host;
    int port;
    byte[] inbuffer=new byte[1024];
    byte[] outbuffer=new byte[1024];
    int instart=0, inend=0, outindex=0;

    UDPIO(String host, int port){
      this.host=host;
      this.port=port;
      try{
	address = InetAddress.getByName(host);
	socket = new DatagramSocket();
      }
      catch(Exception e){System.err.println(e);}
      recpacket = new DatagramPacket(buf, 1024);
      sndpacket=new DatagramPacket(outbuffer, 0, address, port);
    }

    void setTimeout(int i){
      try{socket.setSoTimeout(i);}
      catch(Exception e){System.out.println(e);}
    }
    /*
    int getByte() throws java.io.IOException{
      if((inend-instart)<1){ read(1); }
      return inbuffer[instart++]&0xff;
    }
    void getByte(byte[] array) throws java.io.IOException{
      getByte(array, 0, array.length);
    }
    void getByte(byte[] array, int begin, int length) throws java.io.IOException{
      int i=0;
      while(true){
	if((i=(inend-instart))<length){
	  if(i!=0){
	    System.arraycopy(inbuffer, instart, array, begin, i);
	    begin+=i;
	    length-=i;
	    instart+=i;
	  }
	  read(length);
	  continue;
	}
	System.arraycopy(inbuffer, instart, array, begin, length);
	instart+=length;
	break;
      }
    }
    int getShort() throws java.io.IOException{
      if((inend-instart)<2){ read(2); }
      int s=0;
      s=inbuffer[instart++]&0xff;
      s = ((s<<8)&0xffff) | (inbuffer[instart++]&0xff);
      return s;
    }
    int getInt() throws java.io.IOException{
      if((inend-instart)<4){ read(4); }
      int i=0;
      i=inbuffer[instart++]&0xff;
      i = ((i<<8)&0xffff) | (inbuffer[instart++]&0xff);
      i = ((i<<8)&0xffffff) | (inbuffer[instart++]&0xff);
      i = (i<<8) | (inbuffer[instart++]&0xff);
      return i;
    }
    void getPad(int n) throws java.io.IOException{
      int i;
      while (n > 0){
	if((i=inend-instart)<n){
	  n-=i;
	  instart+=i;
	  read(n);
	  continue;
	}
	instart+=n;
	break;
      }
    }
    void read(int n) throws java.io.IOException{
      if (n>inbuffer.length){
	n=inbuffer.length;
      }
      instart=inend=0;
      int i;
      while(true){
	recpacket = new DatagramPacket(buf, 1024);
	socket.receive(recpacket);
	i=recpacket.getLength();
	System.arraycopy(recpacket.getData(), 0, inbuffer, inend, i);
	// i=in.read(inbuffer, inend, inbuffer.length-inend);
	if(i==-1){
	  throw new java.io.IOException();
	}
	inend+=i;
	if(n<=inend)break;
      }
    }
    void putByte(byte val) throws java.io.IOException{
      if((outbuffer.length-outindex)<1){ flush(); }
      outbuffer[outindex++]=val;
    }
    void putByte(int val) throws java.io.IOException{
      putByte((byte)val);
    }
    void putByte(byte[] array) throws java.io.IOException{
      putByte(array, 0, array.length);
    }
    */
    void write(byte[] array, int begin, int length) throws java.io.IOException{
      if(length<=0) return;
      int i=0;
      while(true){
	if((i=(outbuffer.length-outindex))<length){
	  if(i!=0){
	    System.arraycopy(array, begin, outbuffer, outindex, i);
	    begin+=i;
	    length-=i;
	    outindex+=i;
	  }
	  flush();
	  continue;
	}
	System.arraycopy(array, begin, outbuffer, outindex, length);
	outindex+=length;
	break;
      }
    }
      /*
    void putShort(int val) throws java.io.IOException{
      if((outbuffer.length-outindex)<2){
	flush();
      }
      outbuffer[outindex++]=(byte)((val >> 8)&0xff);
      outbuffer[outindex++]=(byte)(val&0xff);
    }
    void putInt(int val) throws java.io.IOException{
      if((outbuffer.length-outindex)<4){
	flush();
      }
      outbuffer[outindex++]=(byte)((val >> 24) & 0xff);
      outbuffer[outindex++]=(byte)((val >> 16) & 0xff);
      outbuffer[outindex++]=(byte)((val >> 8) & 0xff);
      outbuffer[outindex++]=(byte)((val) & 0xff);
    }
    void putPad(int n) throws java.io.IOException{
      int i;
      while(true){
	if((i=(outbuffer.length-outindex))<n){
	  if(i!=0){
	    outindex+=i;
	    n-=i;
	  }
	  flush();
	  continue;
	}
	outindex+=n;
	break;
      }
    }
      */
    synchronized void flush() throws java.io.IOException{
      if(outindex==0)return;
      // out.write(outbuffer, 0, outindex);
      //packet=new DatagramPacket(outbuffer, outindex, address, port);
      //socket.send(packet);
      sndpacket.setLength(outindex);
      socket.send(sndpacket);
      outindex=0;
    }
    void close() throws java.io.IOException{
      socket.close();
    }
  }

}
