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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import jroar.code.com.jcraft.jogg.Page;
import jroar.code.com.jcraft.jogg.SyncState;

class Proxy extends Source implements Runnable {
    static final int BUFSIZE = 4096 * 2;

    private InputStream bitStream = null;

    private SyncState oy;
    private Page og;

    private byte[] buffer = null;
    private int bytes = 0;

    private Thread me = null;

    private int RETRY = 3;
    int retry = RETRY;

    private long lasttime = 0;

    //Evolución: añadido el parámetro isVideo que indica si el usuario pretende retransmitir
    //video o no(audio)
    Proxy(String mountpoint, String source,boolean isVideo) {
        super(mountpoint,isVideo);
        this.source = source;

        HttpServer.source_connections++;

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
            if (System.currentTimeMillis() - lasttime > 600000) {
                stop();
            }
            return;
        }
        if (mountpoint == null) return;
        me = new Thread(this);
        me.start();
    }

    @Override
    public void run() {
        lasttime = System.currentTimeMillis();

        Vector http_header = new Vector();
        Page[] pages = new Page[10];
        int page_count = 0;

        String _source = source;
        try {
            if (_source.endsWith(".pls")) {
                _source = Pls.getURL(_source);
            }
            URL url = new URL(_source);
            URLConnection urlc = url.openConnection();

            setURLProperties(urlc);

            String foo;
            int i = 0;
            String s = null;
            String t = null;
            while (true) {
                s = urlc.getHeaderField(i);
                t = urlc.getHeaderFieldKey(i);
                if (s == null) break;
                http_header.addElement((t == null ? s : (t + ": " + s)));
                i++;
            }

            int index = 0;
            foo = "jroar-source." + index + ": ";
            i = 0;
            for (; i < http_header.size(); i++) {
                s = (String) (http_header.elementAt(i));
                if (s.startsWith(foo)) {
                    index++;
                    foo = "jroar-source." + index + ": ";
                    i = 0;
                    continue;
                }
                break;
            }
            http_header.addElement(foo + source);

            bitStream = urlc.getInputStream();
        } catch (Exception ee) {
            System.err.println(ee);
            me = null;
            stop();
            return;
        }

        init_ogg();

        int serialno = -1;

        ByteArrayOutputStream _header = new ByteArrayOutputStream();
        byte[] header = null;

        retry = RETRY;

        while (me != null) {
            boolean eos = false;
            header = null;
            while (!eos) {
                int index = oy.buffer(BUFSIZE);
                buffer = oy.data;
                try {
                    bytes = bitStream.read(buffer, index, BUFSIZE);
                } catch (Exception e) {
                    System.err.println(e);
                    bytes = -1;
                    break;
                }
                if (bytes == -1 || bytes == 0) break;
                oy.wrote(bytes);

                lasttime = System.currentTimeMillis();

                try {
                    Thread.sleep(1);
                }  // sleep for green thread.
                catch (Exception e) {
                    System.err.println(e);
                }

                while (!eos) {
                    int result = oy.pageout(og);

                    if (result == 0) break; // need more data
                    if (result != -1) {
                        retry = RETRY;
                        if ((og.granulepos() == 0)
                                || (og.granulepos() == -1)          // hack for Speex
                        ) {
                            if (header != null) {
                                header = null;
                            }
                            if (pages.length <= page_count) {
                                Page[] foo = new Page[pages.length * 2];
                                System.arraycopy(pages, 0, foo, 0, pages.length);
                                pages = foo;
                            }
                            pages[page_count++] = og.copy();
                        } else {
                            if (header == null) {
                                Page foo;
                                for (int i = 0; i < page_count; i++) {
                                    foo = pages[i];
                                    _header.write(foo.header_base, foo.header, foo.header_len);
                                    _header.write(foo.body_base, foo.body, foo.body_len);
                                }
                                header = _header.toByteArray();
                                _header.reset();
                                page_count = 0;
                            }
                        }

                        int size = listeners.size();

                        if (size == 0) {
                            eos = true;
                            stop();
                            break;
                        }

                        Client c = null;
                        for (int i = 0; i < size; ) {
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
                        if (og.eos() != 0) eos = true;
                    }
                }
            }

            if (bytes == -1) {
                retry--;
                if (retry > 0) {
                    System.out.println("Connection to " + _source + " is dropped. Retry(" + retry + ")");
                    header = null;
                    serialno = -1;
                    init_ogg();
                    try {
                        if (bitStream != null) bitStream.close();
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.err.println(e);
                    }

                    try {
                        URL url = new URL(_source);
                        URLConnection urlc = url.openConnection();

                        setURLProperties(urlc);

                        bitStream = urlc.getInputStream();
                        continue;
                    } catch (Exception e) {
                        retry = 0;
                    }
                } else {
                    stop();
                }
                break;
            }
        }
        stop();
    }

    private void setURLProperties(URLConnection urlc) {
        if (HttpServer.myURL != null) {
            urlc.setRequestProperty("jroar-proxy", HttpServer.myURL + mountpoint);
            //System.out.println(HttpServer.myURL+mountpoint);
            if (JRoar.comment != null)
                urlc.setRequestProperty("jroar-comment", JRoar.comment);
        }
    }

    void stop() {
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
                    System.err.println(e);
                }
            }
            listeners.removeAllElements();
        }
    }

    @Override
    void drop() {
        drop_clients();
        super.drop();
    }

    public String toString() {
        return super.toString() + " me=" + me + ", bitStream=" + bitStream;
    }
}
