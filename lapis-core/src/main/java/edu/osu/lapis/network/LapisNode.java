package edu.osu.lapis.network;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LapisNode implements Serializable {
	
	public LapisNode() {
		// default constructor
	}
	
	public LapisNode(String nodeName, String url) {
		this.nodeName = nodeName;
		this.url = url;
	}
	
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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LapisNode) {
			LapisNode otherNode = (LapisNode)obj;
			return this.nodeName.equals(otherNode.nodeName);
		} else {
			return false;
		}
	}
}