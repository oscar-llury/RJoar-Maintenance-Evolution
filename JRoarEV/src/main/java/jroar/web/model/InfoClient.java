package jroar.web.model;

public class InfoClient {
    String srcmpoint;
    String dsthost;
    int dstport;
    String dstmpoint;
    String url;

    public InfoClient(String srcmpoint, String dsthost, int dstport, String dstmpoint, String url) {
        this.srcmpoint = srcmpoint;
        this.dsthost = dsthost;
        this.dstport = dstport;
        this.dstmpoint = dstmpoint;
        this.url = url;
    }
}