package edu.osu.lapis.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkTable {
	private final Map<String, LapisNode> nodeMap = Collections.synchronizedMap(new HashMap<String, LapisNode>());
	
	public void addNode(LapisNode lapisNode) {
		
		if (nodeMap.containsKey(lapisNode.getNodeName())){
			if (nodeMap.get(lapisNode.getNodeName()).getUrl() == lapisNode.getUrl()){
				throw new IllegalArgumentException();
			}
		}
		
		nodeMap.put(lapisNode.getNodeName(), lapisNode);
	}
	
	public LapisNode getNode(String nodeName) {
		return nodeMap.get(nodeName);
	}
	
	public List<LapisNode> getNodesList() {
		return new ArrayList<LapisNode>(nodeMap.values());
	}
	
	public void updateNode(LapisNode lapisNode, LapisNode newLapisNode) {
		
		if (nodeMap.containsKey(lapisNode.getNodeName())){
			nodeMap.put(lapisNode.getNodeName(), newLapisNode);
		}else{
			
			throw new IllegalArgumentException();
			
		}
		
	}
	

}