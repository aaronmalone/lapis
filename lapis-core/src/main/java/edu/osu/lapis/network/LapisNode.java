package edu.osu.lapis.network;


public class LapisNode {
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
