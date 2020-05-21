package jroar.web.model;

import java.util.List;

public class InfoSource {
    private String name;
    private String mountpoint;
    private String m3u;
    private int listeners;
    private int connections;
    private boolean isVideo;
    private int likes;
    private int dislikes;
    private boolean canRate;
    private List<Comment> comments;
    private boolean hasComments;
    private List<InfoProxy> proxyList;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMountpoint() {
		return mountpoint;
	}

	public void setMountpoint(String mountpoint) {
		this.mountpoint = mountpoint;
	}

	public String getM3u() {
		return m3u;
	}

	public void setM3u(String m3u) {
		this.m3u = m3u;
	}

	public int getListeners() {
		return listeners;
	}

	public void setListeners(int listeners) {
		this.listeners = listeners;
	}

	public int getConnections() {
		return connections;
	}

	public void setConnections(int connections) {
		this.connections = connections;
	}

	public boolean isVideo() {
		return isVideo;
	}

	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public boolean isCanRate() {
		return canRate;
	}

	public void setCanRate(boolean canRate) {
		this.canRate = canRate;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
		this.setHasComments(!comments.isEmpty());
	}
	
	public boolean isHasComments() {
		return hasComments;
	}

	public void setHasComments(boolean hasComments) {
		this.hasComments = hasComments;
	}

	public List<InfoProxy> getProxyList() {
		return proxyList;
	}

	public void setProxyList(List<InfoProxy> proxyList) {
		this.proxyList = proxyList;
	}
    
	
    
}
