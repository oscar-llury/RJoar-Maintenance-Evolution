package jroar.web.model;

public class InfoProxy {
    String strProxy;
    String m3u;
    boolean hasHost;
    String host;

    public InfoProxy(String strProxy, String m3u, String host, boolean hasHost) {
        this.strProxy = strProxy;
        this.m3u = m3u;
        this.host = host;
        this.hasHost = hasHost;
    }
}
