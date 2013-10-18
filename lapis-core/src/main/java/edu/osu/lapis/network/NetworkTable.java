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
				throw new IllegalArgumentException("Attempted to add node " + lapisNode 
						+ " but already had an existing node  with the same name: " 
						+ nodeMap.get(lapisNode.getNodeName()));
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
	
	public void updateNode(LapisNode lapisNode) {
		
		if (nodeMap.containsKey(lapisNode.getNodeName())){
			nodeMap.put(lapisNode.getNodeName(), lapisNode);
		}else{
			
			throw new IllegalArgumentException(/*TODO add message*/);
			
		}
		
	}
	
	public LapisNode getCoordinator() {
		return null; //TODO IMPLEMENT
	}
}
