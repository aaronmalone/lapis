package edu.osu.lapis.serialize;

import java.net.URL;

public class LapisNetworkDatum {

	private String nodeName;
	private URL nodeAddress;
	
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public URL getNodeAddress() {
		return nodeAddress;
	}
	public void setNodeAddress(URL nodeAddress) {
		this.nodeAddress = nodeAddress;
	}


}
