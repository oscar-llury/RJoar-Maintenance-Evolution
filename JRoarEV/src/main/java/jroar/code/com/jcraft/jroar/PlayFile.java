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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import jroar.code.com.jcraft.jogg.SyncState;
import jroar.code.com.jcraft.jogg.Page;

class PlayFile extends Source implements Runnable {
    static final int BUFSIZE = 4096 * 2;

    private InputStream bitStream = null;

    private SyncState oy;
    private Page og;

    private byte[] buffer = null;
    private int bytes = 0;

    private Thread me = null;
    private String file = null;
    private String[] files = null;

    PlayFile(String mountpoint, String[] files) {
        super(mountpoint);
        HttpServer.source_connections++;
        this.source = "playlist";
        this.files = files;
    }

    PlayFile(String mountpoint, String file) {
        super(mountpoint);
        HttpServer.source_connections++;
        this.source = "playlist";
        // Si se le pasa una dirección http con una playlist, se llama al método fetch_m3u
        if (file.startsWith("http://") &&
                file.endsWith(".m3u")) {
            Vector foo = JRoar.fetch_m3u(file);
            if (foo.size() > 0) {
                this.files = new String[foo.size()];
                for (int i = 0; i < foo.size(); i++) {
                    this.files[i] = (String) foo.elementAt(i);
                }
                this.source = file;
            } else {
                drop();
                HttpServer.source_connections--;
            }
        } else if (JRoar.running_as_applet) {
            drop();
            HttpServer.source_connections--;
        } else if (file.equals("-")) {
            this.files = new String[1];
            this.files[0] = file;
        } else if (file.endsWith(".ogg") || file.endsWith(".spx")) {
            // Si se le pasa una dirección un solo archivo .ogg
            this.files = new String[1];
            this.files[0] = file;
            if (file.startsWith("http://")) {
                // Si dicho archivo se almacena en un servidor HTTP
                this.source = file;
            }
        } else {
            this.file = file;
            try {
                updateFiles(file);
            } catch (Exception e) {
                System.out.println(e);
                drop();
                HttpServer.source_connections--;
            }
        }
    }

    long file_lastm = 0;

    private void updateFiles(String file) throws java.io.FileNotFoundException {
        System.out.println("loadPlaylist: " + file);
        File _file = new File(file);
        file_lastm = _file.lastModified();
        BufferedReader d
                = new BufferedReader(new InputStreamReader(new FileInputStream(_file)));
        Vector v = new Vector();
        try {
            while (true) {
                String s = d.readLine();
                if (s == null) break;
                if (s.startsWith("#")) continue;
                if (!s.startsWith("http://") &&
                        !s.endsWith(".ogg") &&
                        !s.endsWith(".spx"))
                    continue;
                System.out.println("playFile (" + s + ")");
                v.addElement(s);
            }
            d.close();
        } catch (Exception ee) {
            System.err.println(ee);
        }
        this.files = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            this.files[i] = (String) v.elementAt(i);
        }
    }

    void init_ogg() {
        oy = new SyncState();
        og = new Page();
        buffer = null;
        bytes = 0;
        oy.init();
    }

    public void kick() {

        if (me != null) {
            return;
        }
        me = new Thread(this);
        me.start();
    }

    static String status = "status0";

    public void run() {
        Vector http_header = new Vector();
        http_header.addElement("HTTP/1.0 200 OK");
        http_header.addElement("Content-Type: application/x-ogg");

        int ii = -1;
        while (me != null) {
            ii++;
            if (this.file != null &&
                    file_lastm < (new File(this.file)).lastModified()) {
                try {
                    updateFiles(file);
                } catch (Exception e) {
                    break;
                }
            }
            if (ii >= files.length) ii = 0;

            status = "status1";

            bitStream = null;
            if (files[ii].startsWith("http://")) {
                try {
                    URL url = new URL(files[ii]);
                    URLConnection urlc = url.openConnection();

                    setURLProperties(urlc);        // for jroar

                    bitStream = urlc.getInputStream();
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (files[ii].equals("-")) {
                bitStream = System.in;
            }

            if (bitStream == null) {
                try {
                    bitStream = new FileInputStream(files[ii]);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if (bitStream == null) {
                files[ii] = null;
                String[] foo = new String[files.length - 1];
                if (ii > 0)
                    System.arraycopy(files, 0, foo, 0, ii);
                if (ii != files.length - 1)
                    System.arraycopy(files, ii + 1, foo, ii, files.length - 1 - ii);
                files = foo;
                ii--;
                if (files.length == 0) {
                    break;
                }
                continue;
            }

            file = files[ii];
            status = "status2";

            init_ogg();

            int serialno = -1;
            long granulepos = -1;

            long start_time = -1;
            long last_sample = 0;
            long time = 0;

            ByteArrayOutputStream _header = new ByteArrayOutputStream();
            byte[] header = null;
            Page[] pages = new Page[10];
            int page_count = 0;

            boolean eos = false;
            while (!eos) {
                status = "status3";
                if (me == null) break;
                status = "status4";
                int index = oy.buffer(BUFSIZE);
                buffer = oy.data;
                try {
                    bytes = bitStream.read(buffer, index, BUFSIZE);
                } catch (Exception e) {
                    System.err.println(e);
                    eos = true;
                    continue;
                }
                if (bytes == -1 || bytes == 0) break;
                status = "status5";
                oy.wrote(bytes);

                while (!eos) {
                    status = "status6";
                    if (me == null) break;
                    status = "status7";
                    int result = oy.pageout(og);

                    if (result == 0) break; // need more data
                    if (result != -1) {

                        serialno = og.serialno();
                        granulepos = og.granulepos();
                        status = "status8";

                        if ((granulepos == 0)
                                || (granulepos == -1)          // hack for Speex
                        ) {

                            if (pages.length <= page_count) {
                                Page[] foo = new Page[pages.length * 2];
                                System.arraycopy(pages, 0, foo, 0, pages.length);
                                pages = foo;
                            }
                            pages[page_count++] = og.copy();
                        } else {
                            if (header == null) {
                                parseHeader(pages, page_count);
                                Page foo;
                                for (int i = 0; i < page_count; i++) {
                                    foo = pages[i];
                                    _header.write(foo.header_base, foo.header, foo.header_len);
                                    _header.write(foo.body_base, foo.body, foo.body_len);
                                }
                                header = _header.toByteArray();
                                _header.reset();

                                page_count = 0;
                                start_time = System.currentTimeMillis();
                                last_sample = 0;
                                time = 0;
                            }
                        }

                        status = "status9";
                        status = "status99";
                        int size = listeners.size();

                        Client c = null;
                        for (int i = 0; i < size; ) {
                            status = "status10";
                            try {
                                c = (Client) (listeners.elementAt(i));
                                c.write(http_header, header,
                                        og.header_base, og.header, og.header_len,
                                        og.body_base, og.body, og.body_len);
                            } catch (Exception e) {
                                c.close();
                                removeListener(c);
                                size--;
                                continue;
                            }
                            i++;
                        }
                        status = "status11";
                        if (granulepos != 0 &&
                                key_serialno == serialno) {
                            status = "status111";
                            if (last_sample == 0) {
                                time = (System.currentTimeMillis() - start_time) * 1000;
                            } else {
                                time += (long) (((granulepos - last_sample) * 1000000) /
                                        (current_info.rate));
                            }
                            last_sample = granulepos;
                            long sleep = (time / 1000) - (System.currentTimeMillis() - start_time);
                            status = "status112";
                            if (sleep > 0) {
                                try {
                                    Thread.sleep(sleep);
                                } catch (Exception e) {
                                }
                            }
                            status = "status12";
                        }

                        // sleep for green thread.
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                        }

                        status = "status13";

                    }
                    status = "status14";
                }
                status = "status15";
            }
            oy.clear();
            try {
                if (bitStream != null) bitStream.close();
            } catch (Exception e) {
            }
            bitStream = null;
            status = "status16";
        }

        oy.clear();
        try {
            if (bitStream != null) bitStream.close();
        } catch (Exception e) {
        }
        bitStream = null;
        status = "status14";

        drop();
    }

    private void setURLProperties(URLConnection urlc) {
        if (HttpServer.myURL != null) {
            urlc.setRequestProperty("jroar-proxy", HttpServer.myURL + mountpoint);
            if (JRoar.comment != null)
                urlc.setRequestProperty("jroar-comment", JRoar.comment);
        }
    }

    public void stop() {
        if (me != null) {
            if (oy != null) oy.clear();
            try {
                if (bitStream != null) bitStream.close();
            } catch (Exception e) {
            }
            bitStream = null;
            me = null;
        }
        drop_clients();
    }

    void drop_clients() {
        Client c = null;
        synchronized (listeners) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                c = (Client) (listeners.elementAt(i));
                try {
                    c.close();
                } catch (Exception e) {
                }
            }
            listeners.removeAllElements();
        }
    }

    @Override
    void drop() {
        stop();
        super.drop();
    }
}
