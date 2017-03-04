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

class Ctrl extends Page{

  static void register(){
    register("/ctrl.html", Ctrl.class.getName());
  }
  private static final int REFRESH=60;
  private static int count=0;

  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
    count++;
    s.println( "HTTP/1.0 200 OK" );
    s.println( "Content-Type: text/html" );
    s.println( "" ) ;
    s.println("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
    s.println("<HTML><HEAD>");
//  s.println("<META HTTP-EQUIV=\"refresh\" content=\""+REFRESH+";URL=/ctrl.html\">");
    s.println("<TITLE>JRoar "+JRoar.version+" at "+HttpServer.myURL+"/</TITLE>");
    s.println("</HEAD><BODY>");
    s.println( "<h1>JRoar "+JRoar.version+" at "+HttpServer.myURL+"/</h1>" );

    Enumeration keys=Source.sources.keys();
    if(keys.hasMoreElements()){ 
      //s.println("Mount points.<br>"); 
    }
    else{ s.println("There is no mount point.<br>"); }

    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    for(; keys.hasMoreElements();){
      String mountpoint=((String)(keys.nextElement()));
      Source source=Source.getSource(mountpoint); if(source==null) continue;
      String source_name=source.source;

      s.println("<tr>");

      s.println("<td align=left nowrap>");
      s.print("<a href="+ogg2m3u(mountpoint)+">"+mountpoint);
      if(source instanceof UDPSource){
         UDPSource foo=(UDPSource)source;
         s.print("(UDP:"+foo.b.port+")");
      }
      s.print("</a>");
      s.print("&nbsp;("+source.getListeners()+","+source.getConnections()+")");
      s.println("</td>");

      s.println("<td nowrap> &lt;--- </td>");

      if(source instanceof Proxy){
        s.println("<td align=left>");
        s.print("<a href="+source_name+">"+source_name+"</a>");
        s.println("</td>");
      }
      else if(source instanceof UDPSource){
        UDPSource foo=(UDPSource)source;
        s.println("<td align=left>");
        s.print(foo.b.srcmpoint);
        s.println("</td>");
      }
      else{
        s.println("<td align=left>"+source_name+"</td>");
      }
      s.println("</tr>");
      /*
      String comment=getComment(source.current_comment);
      if(comment!=null){
        s.println("<tr>");
        s.println("<td>&nbsp;</td>");
        s.println("<td>&nbsp;</td>");
        s.println("<td>"+comment+"</td>");
        s.println("</tr>");
      }
      */
      Object[] proxies=source.getProxies();
      if(proxies!=null){
        for(int i=0; i<proxies.length; i++){
          String foo=(String)(proxies[i]);
          s.println("<tr>");
          s.println("<td>&nbsp;</td>");
          s.println("<td nowrap>---&gt</td>");
          String host=getHost(foo);
          if(host==null){
            s.println("<td><a href="+ogg2m3u(foo)+">"+foo+"</a></td>");
	  }
          else{
            s.println("<td><a href="+ogg2m3u(foo)+">"+foo.substring(host.length()-1)+"</a>&nbsp;at&nbsp;<a href="+host+">"+host+"</a></td>");
	  }
          s.println("</tr>");
	}
      }

    }
    s.println("</table>");

    s.println("<hr width=80%>");
    s.println("<font size=+1>Mount</font>");
    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    s.println("<form method=post action=/mount>");
    s.println("<tr><td>");
    s.print("mountpoint:&nbsp;"); 
    s.print("<input type=text name=mountpoint value='/' size=10 maxlength=32>");
    s.print("&nbsp;&nbsp;"); 
    s.print("source:&nbsp;"); 
    s.print("<input type=text name=source value='http://' size=40 maxlength=100>");

    s.print("&nbsp;&nbsp;"); 
    s.print("<select name=livestream>");
    s.print("  <option value='true' selected>LiveStream</option>");
    s.print("  <option value='false'>PlayList</option>");
    s.print("</select>");

    s.print("&nbsp;&nbsp;"); 
    s.print("limit:&nbsp;"); 
    s.print("<input type=text name=limit value='' size=3 maxlength=3>");

    s.println("</td></tr>");

    s.println("<tr><td>");
    s.print("passwd:&nbsp;"); 
    s.print("<input type=password name=passwd value='' length=8>");

    s.print("&nbsp;&nbsp;"); 
    s.print("<input type=submit name=Mount value=Mount>");
    s.println("</td></tr>");
    s.print("</form>");
    s.print("</table>");

    s.print("<p>");

    synchronized(Client.clients){

    keys=Source.sources.keys();
    if(keys.hasMoreElements()){
    s.println("<hr width=80%>");
    s.println("<font size=+1>Drop</font>");
    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    s.println("<form method=post action=/drop>");

    s.print("<select name=mpoint size=1>");
    for(; keys.hasMoreElements();){
      String mpoint=((String)(keys.nextElement()));
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>");

    s.print("&nbsp;&nbsp;"); 
    s.print("passwd:&nbsp;"); 
    s.print("<input type=password name=passwd value='' length=8>");

    s.print("&nbsp;&nbsp;"); 
    s.print("<input type=submit name=Drop value=Drop>");
    s.print("</form>");
    s.print("</table>");
    }

    keys=Source.sources.keys();
    if(keys.hasMoreElements()){
    s.println("<hr width=80%>");
    s.println("<font size=+1>Shout</font>");

    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    for(int i=0; i<Client.clients.size(); i++){
      Client c=((Client)(Client.clients.elementAt(i)));
      if(c instanceof ShoutClient){
      ShoutClient sc=(ShoutClient)c;
      s.println("<tr>");

      s.println("<td align=left>");
      s.print(sc.srcmpoint);
      s.println("</td>");

      s.println("<td nowrap>---&gt</td>");

      s.println("<td align=left>");
      s.print("<a href=http://"+sc.dsthost+":"+sc.dstport+sc.dstmpoint+">http://"+sc.dsthost+":"+sc.dstport+sc.dstmpoint+"</a>");
      s.println("</td>");

      s.println("</tr>");
      }
    }
    s.println("</table>");

    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    s.println("<form method=post action=/shout>");

    s.print("<select name=srcmpoint size=1>");
    for(; keys.hasMoreElements();){
      String mpoint=((String)(keys.nextElement()));
      if(Source.sources.get(mpoint) instanceof UDPSource) continue;
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>");

    s.println(" ---&gt ");

    s.print("<input type=text name=dst value='ice://' size=20 maxlength=50>");
    s.print("&nbsp;&nbsp;"); 
    s.print("ice-passwd:&nbsp;"); 
  s.print("<input type=password name=ice-passwd value='' size=8 maxlength=8>");
    s.print("<br>"); 

    s.print("&nbsp;&nbsp;"); 
    s.print("passwd:&nbsp;"); 
    s.print("<input type=password name=passwd value='' length=8>");

    s.print("&nbsp;&nbsp;"); 
    s.print("<input type=submit name=Shout value=Shout>");
//  s.print("&nbsp;(<i>This functionality has not been implemented yet.</i>)");
    s.print("</form>");
    s.print("</table>");
    }

    keys=Source.sources.keys();
    if(keys.hasMoreElements()){
    s.println("<hr width=80%>");
    s.println("<font size=+1>UDP Broadcast</font>");
    s.println("<table cellpadding=3 cellspacing=0 border=0>");
    s.println("<form method=post action=/udp>");

    s.println("<tr>");

    s.println("<td>");
    s.print("<select name=srcmpoint size=1>");
    for(; keys.hasMoreElements();){
      String mpoint=((String)(keys.nextElement()));
      Source source=Source.getSource(mpoint); 
      if(source==null || source instanceof UDPSource) continue;
      s.println("<OPTION VALUE="+mpoint+">"+mpoint);
    }
    s.print("</select>");
    s.println("</td>");

    s.println("<td nowrap>---&gt</td>");

    s.println("<td>");
    s.print("port:&nbsp; "); 
    s.print("<input type=text name=port value='' size=4 maxlength=4>");
    s.print("&nbsp;&nbsp;broadcast address:&nbsp; "); 
    s.print("<input type=text name=baddress value='' size=15 maxlength=20>");
    s.println("</td>");
    s.println("</tr>");

    s.println("<tr>");
    s.println("<td>&nbsp;</td>");
    s.println("<td>&nbsp;</td>");

    s.println("<td>");
    s.print("mountpoint:&nbsp; "); 
    s.print("<input type=text name=dstmpoint value='/' size=20 maxlength=50>");

    s.print("&nbsp;&nbsp;"); 
    s.print("passwd:&nbsp;"); 
    s.print("<input type=password name=passwd value='' length=8>");

    s.print("&nbsp;&nbsp;"); 
    s.print("<input type=submit name=Broadcast value=Broadcast>");
    s.println("</td>");
    s.println("</tr>");

    s.print("</form>");
    s.print("</table>");
    }
    }

    s.println("<hr width=80%>");

    s.println("<table width=100%><tr>");
    s.println("<td align=\"right\"><small><i>"+count+"</i></small></td>");
    s.println("</tr></table>");

    s.println("</BODY></HTML>");
    s.flush();
    s.close();

//  System.gc();
  }
  /*
  private String ogg2pls(String ogg){
    if(!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
    byte[] foo=ogg.getBytes();
    foo[foo.length-1]='s';foo[foo.length-2]='l';foo[foo.length-3]='p';
    return new String(foo);
  }
  */
  private String ogg2m3u(String ogg){
    if(!ogg.endsWith(".ogg") && !ogg.endsWith(".spx")) return ogg;
    byte[] foo=ogg.getBytes();
    foo[foo.length-1]='u';foo[foo.length-2]='3';foo[foo.length-3]='m';
    return new String(foo);
  }
  private static final String _http="http://";
  private String getHost(String url){
    if(!url.startsWith(_http)) return null;
    int foo=url.substring(_http.length()).indexOf('/');
    if(foo!=-1){
      return url.substring(0, _http.length()+foo+1);
    }
    return null;
  }

  /*
  // hmm...
  private String getComment(Comment c){
    if(c.comments==0)return null;
    StringBuffer sb=new StringBuffer();
    for(int i=0; i<c.comments; i++){
      sb.append(new String(c.user_comments[i], 0, c.user_comments[i].length-1));
      if(i+1<c.comments) sb.append("<br>");
    }
    return sb.toString();
  }
  */
}
