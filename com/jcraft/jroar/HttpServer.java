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

class HttpServer extends Thread{

  static{
    HomePage.register();
    Ctrl.register();
    Stats.register();
    Mount.register();
    Drop.register();
    Shout.register();
    UDPPage.register();
    Store.register();

    Debug.register();
  }

  static int connections=0;
  static int client_connections=0;
  static int source_connections=0;

  private ServerSocket serverSocket=null;
  static int port=8000;
  static String myaddress=null;
  static String myURL=null;

  HttpServer(){
    connections=0;
    try{ serverSocket=new ServerSocket(port); }
    catch(IOException e){
      //System.out.println("ServerSocket error"+e );
      System.exit(1);
    }
    try{
      if(myaddress==null)
        myURL="http://"+InetAddress.getLocalHost().getHostAddress()+":"+port;
      else
        myURL="http://"+myaddress+":"+port;
      //System.out.println("myURL: "+myURL);
    }
    catch(Exception e){
      System.out.println(e );
    }
  }

  public void run(){
    Socket socket=null;
    while(true){
      try{socket=serverSocket.accept();}
      catch(IOException e) {
	System.out.println("accept error");
	System.exit(1);
      }
      connections++;
      new Spawn(socket);
    }
  }

  class Spawn extends Thread{
    private Socket socket=null;
    Spawn(Socket socket){
      super();
      this.socket=socket;
      start();
    }
    public void run(){
      try{(new Dispatch(socket)).doit();}
      catch(Exception e){}
    }
  }
}
 
class Dispatch{
  private MySocket mySocket=null;
  private String rootDirectory=".";
  private String defaultFile="index.html";

  Dispatch(Socket s) throws IOException{
    super();
    mySocket=new MySocket(s);
  }

  private void Response(String file) throws IOException{
    FileInputStream	fileInputStream=null;
    BufferedInputStream	bufferedInputStream=null;
    DataInputStream	dataInputStream=null;

    try {
      fileInputStream=new FileInputStream(file);
      bufferedInputStream=new BufferedInputStream(fileInputStream);
      dataInputStream=new DataInputStream(bufferedInputStream);
    }
    catch(IOException e){return;}

    try {
      int c;
      while(true){
        c=dataInputStream.readByte();
	mySocket.print((char)c);
      }
    }
    catch(IOException e){ }

    try{ dataInputStream.close(); } catch(IOException e){ }
    mySocket.flush();
    mySocket.close( ) ;
  }

  private Vector getHttpHeader(MySocket ms) throws IOException{
    Vector v=new Vector();
    String foo=null;
    while(true){
      foo=ms.readLine();
      if(foo.length()==0){
	break;
      }
System.out.println(" "+foo);
      v.addElement(foo);
    }
    return v;
  }

  private void procPOST(String string, Vector httpheader) throws IOException{
    String foo;
    int	len=0;
    int	c;
    String file=string.substring(string.indexOf(' ') + 1);
    if(file.indexOf(' ')!=-1)
      file=file.substring(0 , file.indexOf(' '));

    for(int i=0; i<httpheader.size(); i++){
      foo=(String)httpheader.elementAt(i);
//System.out.println("foo: "+foo);
      if(foo.startsWith("Content-Length:") ||
	 foo.startsWith("Content-length:")  // hmm... for Opera, lynx
	 ){
	foo=foo.substring(foo.indexOf(' ')+1);
	foo=foo.trim();
	len=Integer.parseInt(foo);
      }
    }

    try{
      Object o=Page.map(file);
      if(o!=null){
        Page cgi=null;
        if(o instanceof String){
          String className=(String)o;
          Class classObject=Class.forName(className);
          cgi=(Page)classObject.newInstance();
        }
        else if(o instanceof Page){
          cgi=(Page)o;
        }
        if(cgi!=null){
          cgi.kick(mySocket, cgi.getVars(mySocket , len), httpheader);
          mySocket.flush();
          mySocket.close();
          return;
	}
      }
    }
    catch(Exception e){
    }
    Page.unknown(mySocket, file);
  }

  private void procGET(String string, Vector httpheader) throws IOException{

    String file;

    file=string.substring(string.indexOf(' ')+1);
    if(file.indexOf(' ')!=-1){
      file=file.substring(0,file.indexOf(' '));
    }

    String _file=file;

    if(_file.startsWith("//")){
      _file=_file.substring(1);
    }

    Source source=Source.getSource(_file);
    if(source!=null){
      boolean reject=false;
      if(source.getLimit()!=0 &&
         source.getLimit()<source.getListeners()){
        reject=true;
      }
      if(!reject && source.for_relay_only){
        reject=true;
        for(int i=0; i<httpheader.size(); i++){
          String foo=(String)httpheader.elementAt(i);
          if(foo.startsWith("jroar-proxy: ")){
            reject=false;
            break;
          }
        }
      }

      if(reject){
        Page.unknown(mySocket, _file);
        return;
      }

      source.addListener(new HttpClient(mySocket, httpheader, _file));
      if(source instanceof Proxy){
        ((Proxy)source).kick();
      }
      if(source instanceof PlayFile){
        ((PlayFile)source).kick();
      }
      if(source.mountpoint!=null){
        HttpServer.client_connections++;
      }
      return;
    }

    if(_file.indexOf('?')!=-1) _file=_file.substring(0, _file.indexOf('?'));

    try{
      Object o=Page.map(_file);
      if(o!=null){
        Page cgi=null;
        if(o instanceof String){
          String className=(String)o;
          Class classObject=Class.forName(className);
          cgi=(Page)classObject.newInstance();
	}
        else if(o instanceof Page){
          cgi=(Page)o;
        }
        if(cgi!=null){
          cgi.kick(mySocket, cgi.getVars((file.indexOf('?')!=-1)? file.substring(file.indexOf('?')+1):null), httpheader);
          HttpServer.client_connections++;
          return;
        }
      }
    }
    catch(Exception e) {
    }

    if(_file.endsWith(".pls")){
      Page pls=new Pls(_file);
      pls.kick(mySocket, null, httpheader);
      return;
    }

    if(_file.endsWith(".m3u")){
      Page m3u=new M3u(_file);
      m3u.kick(mySocket, null, httpheader);
      return;
    }

    Page.unknown(mySocket, _file);
  }

  private void procHEAD(String string, Vector httpheader) throws IOException{

    String file;

    boolean exist=false;

    file=string.substring(string.indexOf(' ')+1);
    if(file.indexOf(' ')!=-1){
      file=file.substring(0,file.indexOf(' '));
    }

    Source source=Source.getSource(file);
    if(source!=null){
      exist=true;
    }
    else{
      String _file=file;

      if(_file.indexOf('?')!=-1) _file=_file.substring(0, _file.indexOf('?'));

      Object o=Page.map(_file);
      if(o!=null){
        exist=true;
      }
      else if(_file.endsWith(".pls")){
        exist=true;
      }
      else if(_file.endsWith(".m3u")){
        exist=true;
      }
      file=_file;
    }

    if(exist){
      Page.ok(mySocket,file);
    }
    else{
      Page.unknown(mySocket, file);
    }
  }

  private void procSOURCE(String string, Vector httpheader) throws IOException{

HttpServer.source_connections++;

    String file=string.substring(string.indexOf(' ')+1);
    if(file.indexOf(' ')!=-1){
      file=file.substring(0,file.indexOf(' '));
    }
    if(!file.startsWith("/")){
      file="/"+file;
    }

    String protocol=null;
    if(string.lastIndexOf(' ')!=-1){
      protocol=string.substring(string.lastIndexOf(' ')+1);
    }

    Source source=Source.getSource(file);
    if(source !=null && (source instanceof Ice)){
      Ice ice=(Ice)source;
      if(!ice.isAlive()){
        ice.stop();
        source=null;
      }
    }

    if(source==null){
      new Ice(file, mySocket, httpheader, protocol).kick();
    }
    else{
      try{
        mySocket.flush();
        mySocket.close( ) ;
      }
      catch(Exception e){
      }
    }
  }

  public void doit(){
    try{
      String foo=mySocket.readLine();

System.out.println(mySocket.socket.getInetAddress()+": "+foo+" "+(new java.util.Date()));

      if(foo.indexOf(' ')==-1){
        mySocket.close();
        return;
      }

      String bar=foo.substring(0, foo.indexOf(' '));
      //System.out.println(foo);

      Vector v=getHttpHeader(mySocket);

//System.out.println(v);

      if(bar.equalsIgnoreCase("POST")){
        procPOST(foo, v);
        return;
      }

      if(bar.equalsIgnoreCase("GET")){
        procGET(foo, v);
        return;
      }

      if(bar.equalsIgnoreCase("HEAD")){
        procHEAD(foo, v);
        return;
      }

      if(bar.equalsIgnoreCase("SOURCE")){
        procSOURCE(foo, v);
        return;
      }

    }
    catch(Exception e){
    }
  }
}
