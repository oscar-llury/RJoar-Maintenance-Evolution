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
import com.jcraft.jorbis.*;
import com.jcraft.jogg.*;

class Source{
  static Hashtable sources=new Hashtable();
  Vector listeners=new Vector();
  String mountpoint=null;
  String source=null;

  boolean for_relay_only=false;

  int connections=0;

  int limit=0;

  Info current_info=new Info();
  Comment current_comment=new Comment();
  int key_serialno=-1;

  private Vector proxies=null;
  void addListener(Client c){
    connections++;
    synchronized(listeners){
      listeners.addElement(c);
      if(c.proxy!=null){
        if(proxies==null)proxies=new Vector();
         proxies.addElement(c.proxy);
      }
    }
  }
  void removeListener(Client c){
    synchronized(listeners){
      listeners.removeElement(c);
      if(c.proxy!=null){
        if(proxies!=null){
          proxies.removeElement(c.proxy);
	}  
        //else{ } ???
      }
    }
  }

  void drop(){
    String tmp=mountpoint;
    synchronized(sources){
      sources.remove(mountpoint);
      mountpoint=null;
    }
    kickmplisters(tmp, false);
  }

  static Source getSource(String mountpoint){
    synchronized(sources){
      Source foo=(Source)(sources.get(mountpoint));
      if(foo!=null && foo.limit>0){
        if(foo.limit < foo.getListeners()){
          foo=null;
	}
      }
      return foo;
    }
  }

  int getListeners(){
    synchronized(listeners){
      return listeners.size();
    }
  }
  int getConnections(){
    return connections;
  }

  Object[] getProxies(){
    if(proxies!=null){
      synchronized(listeners){
        return proxies.toArray();
      }
    }
    return null;
  }

  static final int BUFSIZE=4096*2;
    /*
  static com.jcraft.jogg.Page og=new com.jcraft.jogg.Page();
  static Packet op=new Packet();
  static SyncState oy=new SyncState();
  static StreamState os=new StreamState();
    */

  boolean parseHeader(com.jcraft.jogg.Page[] pages, int count){
    current_info.rate=0;
    java.util.Hashtable oss=new java.util.Hashtable();
    java.util.Hashtable vis=new java.util.Hashtable();
    java.util.Hashtable vcs=new java.util.Hashtable();
    Packet op=new Packet();
    for(int i=0; i<count; i++){
      com.jcraft.jogg.Page page=pages[i];
      int serialno=page.serialno();
      StreamState os=(StreamState)(oss.get(new Integer(serialno)));
      Info vi=(Info)(vis.get(new Integer(serialno)));
      Comment vc=(Comment)(vcs.get(new Integer(serialno)));
      if(os==null){
        os=new StreamState();
	os.init(serialno);
	os.reset();
	oss.put(new Integer(serialno), os);
	vi=new Info();
	vi.init();
	vis.put(new Integer(serialno), vi);
	vc=new Comment();
	vc.init();
	vcs.put(new Integer(serialno), vc);
      }
      os.pagein(page);
      os.packetout(op);
      int type=op.packet_base[op.packet];
      //System.out.println("type: "+type);
      byte[] foo=op.packet_base;
      int base=op.packet+1;

      if(foo[base+0]=='v' &&
	 foo[base+1]=='o' &&
	 foo[base+2]=='r' &&
	 foo[base+3]=='b' &&
	 foo[base+4]=='i' &&
	 foo[base+5]=='s'){
	key_serialno=serialno;
	current_info=vi=(Info)(vis.get(new Integer(serialno)));
	vc=(Comment)(vcs.get(new Integer(serialno)));
	vi.synthesis_headerin(vc, op);
      }
      else if(foo[base-1+0]=='S' &&
	      foo[base-1+1]=='p' &&
	      foo[base-1+2]=='e' &&
	      foo[base-1+3]=='e' &&
	      foo[base-1+4]=='x' &&
	      foo[base-1+5]==' ' &&
	      foo[base-1+6]==' ' &&
	      foo[base-1+7]==' '){
	key_serialno=serialno;
	current_info=vi=(Info)(vis.get(new Integer(serialno)));
	if(vi.rate==0){
	vi.rate=(((foo[base-1+39]<<24)&0xff000000)|
		 ((foo[base-1+38]<<16)&0xff0000)|
		 ((foo[base-1+37]<<8)&0xff00)|
		 ((foo[base-1+36])&0xff));
	}
      }
    }
    return current_info.rate!=0;
  }

  boolean parseHeader(byte[] header){
    Info vi=current_info;
    Comment vc=current_comment;

    com.jcraft.jogg.Page og=new com.jcraft.jogg.Page();
    Packet op=new Packet();
    SyncState oy=new SyncState();
    StreamState os=new StreamState();

    ByteArrayInputStream is=new ByteArrayInputStream(header);
    int bytes=0;
    oy.reset();
    int index=oy.buffer(BUFSIZE);
    byte[] buffer=oy.data;
    try{ bytes=is.read(buffer, index, BUFSIZE); }
    catch(Exception e){
      System.err.println(e);
      return false;
    }
    oy.wrote(bytes);
    if(oy.pageout(og)!=1){
      if(bytes<BUFSIZE) return false;
      System.err.println("Input does not appear to be an Ogg bitstream.");
      return false;
    }
    key_serialno=og.serialno();
    os.init(key_serialno);
    os.reset();
    vi.init();
    vc.init();
    if(os.pagein(og)<0){ 
      // error; stream version mismatch perhaps
      System.err.println("Error reading first page of Ogg bitstream data.");
      return false;
    }
    if(os.packetout(op)!=1){ 
      // no page? must not be vorbis
      System.err.println("Error reading initial header packet.");
      return false;
    }

    if(vi.synthesis_headerin(vc, op)>=0){ 
      int i=0;
      while(i<2){
        while(i<2){
          int result=oy.pageout(og);
  	  if(result==0) break; // Need more data
	  if(result==1){
            os.pagein(og);
  	    while(i<2){
	      result=os.packetout(op);
	      if(result==0)break;
	      if(result==-1){
	        System.err.println("Corrupt secondary header.  Exiting.");
                return false;
	      }
	      vi.synthesis_headerin(vc, op);
	      i++;
	    }
	  }
        }

        index=oy.buffer(BUFSIZE);
        buffer=oy.data; 
        try{ bytes=is.read(buffer, index, BUFSIZE); }
        catch(Exception e){
          System.err.println(e);
          return false;
        }
        if(bytes==0 && i<2){
  	  System.err.println("End of file before finding all Vorbis headers!");
          return false;
        }
        oy.wrote(bytes);
      }

    /*
      {
        byte[][] ptr=vc.user_comments;
        StringBuffer sb=null;
        if(acontext!=null) sb=new StringBuffer();

        for(int j=0; j<ptr.length;j++){
          if(ptr[j]==null) break;
          System.err.println("Comment: "+new String(ptr[j],0,ptr[j].length-1));
          if(sb!=null)sb.append(" "+new String(ptr[j], 0, ptr[j].length-1));
        } 
        System.err.println("Bitstream is "+vi.channels+" channel, "+vi.rate+"Hz");
        System.err.println("Encoded by: "+new String(vc.vendor, 0, vc.vendor.length-1)+"\n");
        if(sb!=null)acontext.showStatus(sb.toString());
      }
    */

      return true;
    }

//  System.err.println("This Ogg bitstream does not contain Vorbis audio data.");

  //System.out.println(new String(buffer, 28, 8)); // "Speex   "
    if(buffer.length<26){
//    System.err.println("This Ogg bitstream does not contain Speex audio data.");
      return false;
    }
    int base=26+(buffer[26]&0xff)+1;  // 28
    if((buffer.length<base+8)|
       (buffer[base+0]!='S' ||
	buffer[base+1]!='p' ||
	buffer[base+2]!='e' ||
	buffer[base+3]!='e' ||
	buffer[base+4]!='x' ||
	buffer[base+5]!=' ' ||
	buffer[base+6]!=' ' ||
	buffer[base+7]!=' ')){
//    System.err.println("This Ogg bitstream does not contain Speex audio data.");
      return false;
    }

  //System.out.println(new String(buffer, 36, 20)); // speex_version
  //System.out.print("header version: ");
  //System.out.print(Integer.toHexString(buffer[56])+", ");
  //System.out.print(Integer.toHexString(buffer[57])+", ");
  //System.out.print(Integer.toHexString(buffer[58])+", ");
  //System.out.println(Integer.toHexString(buffer[59])+", ");

  //System.out.print("header size: "+((buffer[63]<<24)|
  //                                  (buffer[62]<<16)|
  //                                  (buffer[61]<<8)|
  //                                  (buffer[60]))+"  ");
  //System.out.print(Integer.toHexString(buffer[60])+", ");
  //System.out.print(Integer.toHexString(buffer[61])+", ");
  //System.out.print(Integer.toHexString(buffer[62])+", ");
  //System.out.println(Integer.toHexString(buffer[63])+", ");

  //System.out.print("rate: "+((buffer[67]<<24)|
  //                                  (buffer[66]<<16)|
  //                                  (buffer[65]<<8)|
  //                                  (buffer[64]))+"  ");
  //System.out.print(Integer.toHexString(buffer[64])+", ");
  //System.out.print(Integer.toHexString(buffer[65])+", ");
  //System.out.print(Integer.toHexString(buffer[66])+", ");
  //System.out.println(Integer.toHexString(buffer[67])+", ");

    vi.rate=(((buffer[base+39]<<24)&0xff000000)|
	     ((buffer[base+38]<<16)&0xff0000)|
	     ((buffer[base+37]<<8)&0xff00)|
	     ((buffer[base+36])&0xff));

  //System.out.println("vi.rate: "+vi.rate);

  //System.out.print("buffer: ");
  //for(int i=0; i<header.length; i++){
  ////System.out.print(Integer.toHexString(header[i])+", ");
  //System.out.print(new Character((char)(header[i])));
  //}
  //System.out.println("");


    if(vi.rate!=0){
      return true;
    }

    return false;
  }
    
  Source(String mountpoint){
    this.mountpoint=mountpoint;
    synchronized(sources){
      sources.put(mountpoint, this);
    }
    if(mountpoint.startsWith("/for_relay_only_")){
      for_relay_only=true;
    }

    kickmplisters(mountpoint, true);
  }

  int getLimit(){ return limit; } 
  void setLimit(int foo){ limit=foo; } 

  private static void kickmplisters(String mountpoint, boolean mount){
    synchronized(JRoar.mplisteners){
      for(java.util.Enumeration e=JRoar.mplisteners.elements();
	   e.hasMoreElements();){
        if(mount)
          ((MountPointListener)(e.nextElement())).mount(mountpoint);
        else
          ((MountPointListener)(e.nextElement())).unmount(mountpoint);
      }
    }
  }

}
