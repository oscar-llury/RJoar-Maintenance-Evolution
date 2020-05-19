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

public class JRoar extends Applet implements Runnable {
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

    @Override
    public void init() {
        String s;

        codebase = getCodeBase(); // contiene la URL del directorio que contiene este applet

        s = getParameter("jroar.port");
        if (s != null) {
            try {
                HttpServer.port = Integer.parseInt(s);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        s = getParameter("jroar.myaddress");
        HttpServer.myaddress = s;

        s = getParameter("jroar.passwd");
        if (s != null) JRoar.passwd = s;

        s = getParameter("jroar.icepasswd");
        if (s != null) JRoar.icepasswd = s;

        s = getParameter("jroar.comment");
        if (s != null) JRoar.comment = s;

        add(mount = new Button("Control"));

        mount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getAppletContext().showStatus("Opening " + HttpServer.myURL + "/ctrl.html");
                    URL url = new URL(HttpServer.myURL + "/ctrl.html");
                    getAppletContext().showDocument(url, "_blank");
                } catch (Exception ee) {
                    System.out.println(ee);
                }
            }
        });
        setBackground(Color.white);
        (new Thread(this)).start();
    }

    static WatchDog wd = null;

    public void run() {
        HttpServer httpServer = new HttpServer();
        httpServer.start();

        wd = new WatchDog();
        wd.start();
    }

    public static void main(String[] arg) {
        String[] usage = {
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
        };

        running_as_applet = false;

        HttpServer.myaddress = null;
        for (int i = 0; i < arg.length; i++) {
            if (arg[i].equals("-port") && arg.length > i + 1) {
                try {
                    HttpServer.port = Integer.parseInt(arg[i + 1]);
                } catch (Exception e) {
                    System.out.println(e);
                }
                i++;
            } else if (arg[i].equals("-myaddress") && arg.length > i + 1) {
                HttpServer.myaddress = arg[i + 1];
                i++;
            } else if (arg[i].equals("-passwd") && arg.length > i + 1) {
                JRoar.passwd = arg[i + 1];
                i++;
            } else if (arg[i].equals("-icepasswd") && arg.length > i + 1) {
                JRoar.icepasswd = arg[i + 1];
                i++;
            } else if (arg[i].equals("-comment") && arg.length > i + 1) {
                JRoar.comment = arg[i + 1];
                i++;
            } else if ((arg[i].equals("-relay") || arg[i].equals("-proxy")) &&
                    arg.length > i + 2) {
                Proxy proxy = new Proxy(arg[i + 1], arg[i + 2]);
                i += 2;
                if (arg.length > i + 1 && !(arg[i + 1].startsWith("-"))) {
                    try {
                        proxy.setLimit(Integer.parseInt(arg[i + 1]));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    i++;
                }
            } else if (arg[i].equals("-playlist") && arg.length > i + 2) {
                PlayFile p = new PlayFile(arg[i + 1], arg[i + 2]);
                i += 2;
                if (arg.length > i + 1 && !(arg[i + 1].startsWith("-"))) {
                    try {
                        p.setLimit(Integer.parseInt(arg[i + 1]));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    i++;
                }
                p.kick();
            } else if (arg[i].equals("-udp") && arg.length > i + 4) {
                int port = 0;
                try {
                    port = Integer.parseInt(arg[i + 3]);
                } catch (Exception e) {
                    System.err.println(e);
                }

            } else if (arg[i].equals("-shout") && arg.length > i + 5) {
                int port = 0;
                try {
                    port = Integer.parseInt(arg[i + 3]);
                } catch (Exception e) {
                    System.err.println(e);
                }
                // Creemos que esto es IceS
                ShoutClient sc = new ShoutClient(arg[i + 1], // src mount point
                        arg[i + 2], // dst ip address
                        port,     // dst port number
                        arg[i + 4], // passwd
                        arg[i + 5]);// dst mount point
                i += 5;
            } else if (arg[i].equals("-page") && arg.length > i + 2) {
                try {
                    Class classObject = Class.forName(arg[i + 2]);
                    Page.register(arg[i + 1], arg[i + 2]);
                } catch (Exception e) {
                    System.err.println("Unknown class: " + arg[i + 2]);
                }
                i += 2;
            } else if (arg[i].equals("-store") && arg.length > i + 2) {
                try {
                    //new Store(arg[i + 1], arg[i + 2]);
                } catch (Exception e) {
                    System.out.println(e);
                }
                i += 2;
            } else {
                System.err.println("invalid option: " + arg[i]);
                for (int ii = 0; ii < usage.length; ii++) {
                    System.err.println(usage[ii]);
                }
                System.exit(-1);
            }
        }

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
        	PlayFile p = new PlayFile(laux.get(0), laux.get(1));
        	try {
                p.setLimit(Integer.parseInt(laux.get(2)));
            } catch (Exception e) {
            }
        }
        
        if(infoParams.get("Relay")!=null) {
        	List<String> laux = infoParams.get("Relay");
        	Proxy proxy = new Proxy(laux.get(0), laux.get(1));
        	try {
                proxy.setLimit(Integer.parseInt(laux.get(2)));
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
