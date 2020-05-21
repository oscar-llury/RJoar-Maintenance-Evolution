package jroar.web.model;

import java.util.List;

public class InfoSource {
    String name;
    String mountpoint;
    String m3u;
    int listeners;
    int connections;
    boolean isVideo;
    List<InfoProxy> proxyList;

    public InfoSource(String mountpoint, String name, String m3u, int listeners, int connections,boolean isVideo, List<InfoProxy> proxyList, boolean hasProxy) {
        this.name = name;
        this.mountpoint = mountpoint;
        this.m3u = m3u;
        this.connections = connections;
        this.listeners = listeners;
        this.isVideo=isVideo;
        this.proxyList = proxyList;
    }

    public InfoSource() {
    }
}
