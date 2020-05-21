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

package jroar.code.com.jcraft.jroar;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class JRoar  {
    public static final String version = "0.0.9";

    static boolean running_as_applet = true;
    static java.net.URL codebase = null; // Contiene TODA la información sobre una URL
    //  static String passwd="passssap";
    static String passwd = null;
    //static String icepasswd="changeme";
    static String icepasswd = null;
    static String comment = null;

    Button mount;

    public JRoar() {
    }

    static WatchDog wd = null;

    public void run() {
        HttpServer httpServer = new HttpServer();
        httpServer.start();

        wd = new WatchDog();
        wd.start();
    }

    static Vector fetch_m3u(String m3u) {
        InputStream pstream = null;
        if (m3u.startsWith("http://")) {
            try {
                URL url = null;
                if (running_as_applet)
                    url = new URL(codebase, m3u); // Creates a URL by parsing the given spec within a specified context.
                else url = new URL(m3u);
                URLConnection urlc = url.openConnection();
                pstream = urlc.getInputStream(); // Flujo de audio que llega desde una conexión HTTP
            } catch (Exception ee) {
                System.err.println(ee);
                return null;
            }
        }
        if (pstream == null && !running_as_applet) {
            try {
                // El flujo de audio proviene del propio sistema
                pstream = new FileInputStream(System.getProperty("user.dir") + "/" + m3u);
            } catch (Exception ee) {
                System.err.println(ee);
                return null;
            }
        }

        String line = null;
        Vector foo = new Vector();
        while (true) {
            try {
                // Lee líneas de un archivo(playlist) que contiene rutas de archivos con audio.
                line = readline(pstream);
            } catch (Exception e) {
                System.out.println(e);
            }
            if (line == null) break;
            System.out.println("playFile (" + line + ")");
            if (line.startsWith("#")) continue;
            foo.addElement(line);
        }
        return foo;
    }

    private static String readline(InputStream is) {
        StringBuffer rtn = new StringBuffer();
        int temp;
        do {
            try {
                temp = is.read();
            } catch (Exception e) {
                return (null);
            }
            if (temp == -1) {
                return (null);
            }
            if (temp != 0 && temp != '\n') rtn.append((char) temp);
        } while (temp != '\n');
        return (rtn.toString());
    }

    public static Hashtable getSources() {
        return Source.sources;
    }

    public static int getListeners(String mpoint) throws JRoarException {
        Source source = Source.getSource(mpoint);
        if (source != null) {
            return source.getListeners();
        }
        throw new JRoarException("invalid mountpoint: " + mpoint);
    }

    public static int getConnections(String mpoint) throws JRoarException {
        Source source = Source.getSource(mpoint);
        if (source != null) {
            return source.getConnections();
        }
        throw new JRoarException("invalid mountpoint: " + mpoint);
    }

    public static String getMyURL() {
        return HttpServer.myURL;
    }

    private static final int WATCHDOGSLEEP = 3000;

    static class WatchDog extends Thread {
        @Override
        public void run() {
            Source source;
            Enumeration sources;
            while (true) {
                try {
                    sources = Source.sources.elements();
                    while (sources.hasMoreElements()) {
                        source = ((Source) sources.nextElement());
                        int size = source.listeners.size();
                        Client c = null;
                        for (int i = 0; i < size; i++) {
                            try {
                                c = (Client) (source.listeners.elementAt(i));
                                if (c.ready && System.currentTimeMillis() - c.lasttime > 1000) {
                                    ((HttpClient) c).ms.close();
                                }
                            } catch (Exception ee) {
                                System.out.println(ee);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("WatchDog: " + e);
                }

                try {
                    Thread.sleep(WATCHDOGSLEEP);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
    //EVOLUCIÓN
    
    public static void install(Map<String,List<String>> infoParams) {

        running_as_applet = false;

        HttpServer.myaddress = null;
        
        if(infoParams.get("Port")!=null) {
        	HttpServer.port = Integer.parseInt(infoParams.get("Port").get(0));
        }
        
        if(infoParams.get("Pass")!=null) {
        	JRoar.passwd = infoParams.get("Pass").get(0);
        }else {
        	JRoar.passwd = "pass";
        }
        
        if(infoParams.get("PlayList")!=null) {
        	List<String> laux = infoParams.get("PlayList");
        	PlayFile p = new PlayFile(laux.get(0), laux.get(1),laux.get(2).equals("video"));
        	try {
                p.setLimit(Integer.parseInt(laux.get(3)));
            } catch (Exception e) {
            }
        }
        
        if(infoParams.get("Relay")!=null) {
        	List<String> laux = infoParams.get("Relay");
        	Proxy proxy = new Proxy(laux.get(0), laux.get(1),laux.get(2).equals("video"));
        	try {
                proxy.setLimit(Integer.parseInt(laux.get(3)));
            } catch (Exception e) {
            }
        }
        
        if(infoParams.get("Address")!=null) {
        	HttpServer.myaddress = infoParams.get("Address").get(0);
        }
        
        if(infoParams.get("Icepass")!=null) {
        	JRoar.icepasswd = infoParams.get("Icepass").get(0);
        }else {
        	JRoar.icepasswd = "pass";
        }
        
        if(infoParams.get("Shout")!=null) {
        	List<String> laux = infoParams.get("Shout");
        	int port = 0;
            try {
                port = Integer.parseInt(laux.get(2));
            } catch (Exception e) {
                System.err.println(e);
            }
            ShoutClient sc = new ShoutClient(laux.get(0), // src mount point
                    laux.get(1), // dst ip address
                    port,     // dst port number
                    laux.get(3), // passwd
                    laux.get(4));// dst mount point
        }
        
        HttpServer httpServer = new HttpServer();
        httpServer.start();

        wd = new WatchDog();
        wd.start();
    }
}
