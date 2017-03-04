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

/*

  To disable built-in pages, '/' and '/index.html', try as follows,
  java com.jcraft.jroar.JRoar -page / BlankPage \
                              -page /index.html BlankPage    

 */

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.jcraft.jroar.*;

public class BlankPage extends UserPage{
  public void kick(MySocket s, Hashtable vars, Vector httpheader) throws IOException{
    s.println( "HTTP/1.0 200 OK" );
    s.println( "Content-Type: text/plain" );
    s.println( "" ) ;
    s.flush();
    s.close();
  }
}
