package edu.osu.lapis.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkTable {
	private final Map<String, LapisNode> nodeMap = Collections.synchronizedMap(new HashMap<String, LapisNode>());
	
	public void addNode(LapisNode lapisNode) {
		nodeMap.put(lapisNode.getNodeName(), lapisNode);
	}
	
	public LapisNode getNode(String nodeName) {
		return nodeMap.get(nodeName);
	}
	
	public List<LapisNode> getNodesList() {
		return new ArrayList<LapisNode>(nodeMap.values());
	}
}
