package edu.osu.lapis.network;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LapisNode implements Serializable {
	private String nodeName;
	private String url;

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	@Override
	public String toString() {
		return "LapisNode(" + nodeName + ", " + url + ")";
	}
}
