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

public class MySocket {
  Socket socket=null;
  private DataInputStream dataInputStream=null;
  private OutputStream os=null;

  MySocket(Socket s) throws IOException{

    try{ s.setTcpNoDelay(true); }
    catch(Exception e){
      System.out.println(e+" tcpnodelay");
    }
    socket=s;
    BufferedInputStream bis=new BufferedInputStream(s.getInputStream());
    dataInputStream=new DataInputStream(bis);
    os=s.getOutputStream();
  }

  InputStream getInputStream(){
    try{ return dataInputStream;}
    catch(Exception e){}
    return null;
  }

  public void close(){
    try{
      socket.shutdownOutput();
      dataInputStream.close();
      os.close();
      socket.close();
    }
    catch(IOException e){ }
  }

  public int readByte(){
    try{ int r=dataInputStream.readByte(); return(r&0xff); }
    catch(IOException e){ return(-1); }
  }

  public String readLine(){
    try{ return(dataInputStream.readLine()); }
    catch(IOException e){ return(null); }
  }

  public void write(byte[] foo, int start, int length) throws IOException{
    os.write(foo, start, length);
  }
  public void p(String s) throws IOException{
    os.write(s.getBytes());
  }
  public void print(String s) throws IOException{
    os.write(s.getBytes());
  }
  public void p(byte[] s) throws IOException{
    os.write(s);
  }
  public void print(byte[] s) throws IOException{
    os.write(s);
  }
  public void p(char c) throws IOException{
    os.write(c);
  }
  public void print(char c) throws IOException{
    os.write(c);
  }
  public void p(int c) throws IOException{
    os.write(Integer.toString(c).getBytes());
  }
  public void print(int c) throws IOException{
    os.write(Integer.toString(c).getBytes());
  }

  static final private byte[] _rn="\r\n".getBytes();
  public void pn(String s) throws IOException{
    println(s);
  }
  public void println(String s) throws IOException{
    print(s); print(_rn);
  }
  public void flush() throws IOException{
    os.flush();
  }
}
