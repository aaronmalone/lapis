package edu.osu.lapis.network;

import java.net.URL;


public class LapisNode {
	private String nodeName;
	private URL url;

	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
