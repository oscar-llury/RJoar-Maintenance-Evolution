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
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

import com.jcraft.jogg.*;

public class JRoar extends Applet implements Runnable{
  static final String version="0.0.9";

  static boolean running_as_applet=true;
  static java.net.URL codebase=null;
//  static String passwd="passssap";
  static String passwd=null;
//static String icepasswd="changeme";
  static String icepasswd=null;
  static String comment=null;

  static java.util.Vector mplisteners=new java.util.Vector();

  Button mount;

  public JRoar(){
  }

  public void init(){
    String s;

    codebase=getCodeBase();

    s=getParameter("jroar.port");
    if(s!=null){
      try{ HttpServer.port=Integer.parseInt(s); }
      catch(Exception e){}
    }

//  s=getParameter("jroar.ipaddress");
//  HttpServer.myaddress=s;

    s=getParameter("jroar.myaddress");
    HttpServer.myaddress=s;

    s=getParameter("jroar.passwd");
    if(s!=null) JRoar.passwd=s;

    s=getParameter("jroar.icepasswd");
    if(s!=null) JRoar.icepasswd=s;

    s=getParameter("jroar.comment");
    if(s!=null) JRoar.comment=s;

    add(mount = new Button("Control"));

    mount.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try{
          getAppletContext().showStatus("Opening "+HttpServer.myURL+"/ctrl.html");
          URL url=new URL(HttpServer.myURL+"/ctrl.html");
          getAppletContext().showDocument(url, "_blank");
	}
	catch(Exception ee){System.out.println(ee);}
      }
    });
    setBackground(Color.white);
    (new Thread(this)).start();
  }

  static WatchDog wd=null;

  public void run(){
    HttpServer httpServer=new HttpServer();
    httpServer.start();

    wd=new WatchDog();
    wd.start();
  }

  public static void main(String[] arg){
    String[] usage={
	"  acceptable options",
	"  -port     port-number (default: 8000)",
        "  -myaddress my-address", 
	"  -relay    mount-point url-of-source [limit]",
	"  -playlist mount-point filename [limit]", 
	"  -page     page-name class-name", 
	"  -store    page-name URL", 
	"  -passwd   password-for-web-interface",
	"  -icepasswd password-for-ICE",
	"  -mplistener class-name",
	"  -shout    src-mount-point ip-address port-number password  dst-mount-point",
	"  -udp      src-mount-point broadcast-address port-number dst-mount-point",
	"  -peercast-host peercast-lookup-host (default: localhost:7144)"
    };

    running_as_applet=false;

    HttpServer.myaddress=null;
    for(int i=0; i<arg.length; i++){
      if(arg[i].equals("-port") && arg.length>i+1){
        try{ HttpServer.port=Integer.parseInt(arg[i+1]);}
	catch(Exception e){}
	i++;
      }
//    else if(arg[i].equals("-ipaddress") && arg.length>i+1){
//      HttpServer.myaddress=arg[i+1];
//      i++;
//    }
      else if(arg[i].equals("-myaddress") && arg.length>i+1){
        HttpServer.myaddress=arg[i+1];
        i++;
      }
      else if(arg[i].equals("-passwd") && arg.length>i+1){
        JRoar.passwd=arg[i+1];
        i++;
      }
      else if(arg[i].equals("-icepasswd") && arg.length>i+1){
        JRoar.icepasswd=arg[i+1];
        i++;
      }
      else if(arg[i].equals("-comment") && arg.length>i+1){
        JRoar.comment=arg[i+1];
        i++;
      }
      else if((arg[i].equals("-relay") || arg[i].equals("-proxy")) &&
	      arg.length>i+2){
        Proxy proxy=new Proxy(arg[i+1], arg[i+2]);
        i+=2;
        if(arg.length>i+1 && !(arg[i+1].startsWith("-"))){
          try{ proxy.setLimit(Integer.parseInt(arg[i+1])); }
          catch(Exception e){
	  }
          i++;
	}
      }
      else if(arg[i].equals("-playlist") && arg.length>i+2){
        PlayFile p=new PlayFile(arg[i+1], arg[i+2]);
        i+=2;
        if(arg.length>i+1 && !(arg[i+1].startsWith("-"))){
          try{ p.setLimit(Integer.parseInt(arg[i+1])); }
          catch(Exception e){
	  }
          i++;
	}
        p.kick();
      }
      else if(arg[i].equals("-udp") && arg.length>i+4){
        int port=0;
        try{ port=Integer.parseInt(arg[i+3]);}
  	catch(Exception e){System.err.println(e);}
        UDPBroadcast u=new UDPBroadcast(arg[i+1], // src mount point
                                        arg[i+2], // broadcast address
					port,     // port number
					arg[i+4]);// dst mount point
        i+=4;
      }
      else if(arg[i].equals("-shout") && arg.length>i+5){
        int port=0;
        try{ port=Integer.parseInt(arg[i+3]);}
  	catch(Exception e){System.err.println(e);}
        ShoutClient sc=new ShoutClient(arg[i+1], // src mount point
                                       arg[i+2], // dst ip address
                                       port,     // dst port number
				       arg[i+4], // passwd
				       arg[i+5]);// dst mount point
        i+=5;
      }
      else if(arg[i].equals("-page") && arg.length>i+2){
        try{
          Class classObject=Class.forName(arg[i+2]);
          Page.register(arg[i+1], arg[i+2]);
        }
        catch(Exception e){
          System.err.println("Unknown class: "+arg[i+2]);
        }
        i+=2;
      }
      else if(arg[i].equals("-store") && arg.length>i+2){
        try{
          new Store(arg[i+1], arg[i+2]);
        }
        catch(Exception e){
//        System.err.println("Unknown class: "+arg[i+2]);
        }
        i+=2;
      }
      else if(arg[i].equals("-mplistener") && arg.length>i+1){
        try{
          Class c=Class.forName(arg[i+1]);
	  System.out.println("c: "+c);
          addMountPointListener((MountPointListener)(c.newInstance()));
        }
        catch(Exception e){
          System.err.println("Unknown listener class: "+arg[i+1]);
        }
        i++;
      }
      else if(arg[i].equals("-peercast-host") && arg.length>i+1){
        PeerCast.setLookupHost(arg[i+1]);
        i++;
      }
      else {
        System.err.println("invalid option: "+arg[i]);
        for(int ii=0; ii<usage.length; ii++){
          System.err.println(usage[ii]);
	}
        System.exit(-1);
      }
    }


    // Live Ogg stream at 'http://192.168.1.2:8000/example1.ogg' 
    // is mouted at /test1.ogg
    /*
    Proxy proxy=new Proxy("/foo1.ogg", 
    	                    "http://192.168.1.2:8888/example1.ogg");
    */

    /*
    // Ogg Vorbis files included in foo will be streamed at /test2.ogg
    String[] foo={
      "/tmp/foo.ogg",
      "/tmp/bar.ogg"
    };
    PlayFile p=new PlayFile("/foo2.ogg", foo);
    p.kick();
    */  
 
    /*
    // A stream mounted at /foo1.ogg will be broadcasted UDP port 8000
    UDPBroadcast u=new UDPBroadcast("/foo1.ogg", "192.168.0.255", 8000, "/udp.ogg");
    */

    // A stream mounted at /test2.ogg is transfered to IceCast2/JRoar at
    // 192.168.0.3:8000 with mountpoint /example2.ogg.
    /*
    ShoutClient sc=new ShoutClient("/foo2.ogg", "192.168.0.3", 8000, "/example2.ogg", "hackme");
    */

    HttpServer	httpServer=new HttpServer();
    httpServer.start();

    wd=new WatchDog();
    wd.start();
  }

  static Vector fetch_m3u(String m3u){
    InputStream pstream=null;
    if(m3u.startsWith("http://")){
      try{
        URL url=null;
        if(running_as_applet) url=new URL(codebase, m3u);
        else url=new URL(m3u);
        URLConnection urlc=url.openConnection();
        pstream=urlc.getInputStream();
      }
      catch(Exception ee){
        System.err.println(ee); 	    
        return null;
      }
    }
    if(pstream==null && !running_as_applet){
      try{
        pstream=new FileInputStream(System.getProperty("user.dir")+"/"+m3u);
      }
      catch(Exception ee){
        System.err.println(ee); 	    
        return null;
      }
    }

    String line=null;
    Vector foo=new Vector();
    while(true){
      try{line=readline(pstream);}catch(Exception e){}
      if(line==null)break;
System.out.println("playFile ("+line+")");
      if(line.startsWith("#")) continue;
      foo.addElement(line);
    }
    return foo;
  }

  private static String readline(InputStream is) {
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

  public static Hashtable getSources(){
    return Source.sources;
  }

  public static int getListeners(String mpoint) throws JRoarException{
    Source source=Source.getSource(mpoint);
    if(source!=null){
      return source.getListeners();
    }
    throw new JRoarException("invalid mountpoint: "+mpoint);
  }
  public static int getConnections(String mpoint) throws JRoarException{
    Source source=Source.getSource(mpoint);
    if(source!=null){
      return source.getConnections();
    }
    throw new JRoarException("invalid mountpoint: "+mpoint);
  }

  public static String getMyURL(){
    return HttpServer.myURL;
  }
  public static void store(String foo, String bar){
    new Store(foo, bar);
  }

  private static final int WATCHDOGSLEEP=3000;
  static class WatchDog extends Thread{
    public void run(){
      Source source;
      Enumeration sources;
      while(true){
        try{
          sources=Source.sources.elements();
          for(; sources.hasMoreElements();){
            source=((Source)sources.nextElement());
            int size=source.listeners.size();
            Client c=null;
            for(int i=0; i<size; i++){
              try{
                c=(Client)(source.listeners.elementAt(i));
                if(c.ready && System.currentTimeMillis()-c.lasttime>1000){
//System.out.println("drop: "+c);
                  ((HttpClient)c).ms.close();
	        }
	      }
              catch(Exception ee){}
	    }
  	  }
	}
        catch(Exception e){
System.out.println("WatchDog: "+e);
	}

        try{Thread.sleep(WATCHDOGSLEEP);}
        catch(Exception e){}
      }
    }
  }

  static void addMountPointListener(MountPointListener foo){
    synchronized(mplisteners){
      mplisteners.addElement(foo);
    }
  }
  static void removeMountPointListener(MountPointListener foo){
    synchronized(mplisteners){
      mplisteners.removeElement(foo);
    }
  }
}
