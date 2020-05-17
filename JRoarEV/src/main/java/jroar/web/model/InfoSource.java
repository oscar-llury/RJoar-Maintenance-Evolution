package jroar.web.model;

public class InfoSource {
    String name;
    String mountpoint;
    String m3u;
    int listeners;
    int connections;

    public InfoSource(String mountpoint, String name, String m3u, int listeners, int connections) {
        this.name = name;
        this.mountpoint = mountpoint;
        this.m3u = m3u;
        this.connections = connections;
        this.listeners = listeners;
    }

    public InfoSource() {
    }
}
