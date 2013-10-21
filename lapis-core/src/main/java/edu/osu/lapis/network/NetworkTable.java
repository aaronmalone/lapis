package edu.osu.lapis.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkTable {

	private final Map<String, LapisNode> nodeMap = Collections.synchronizedMap(new HashMap<String, LapisNode>());
	private LapisNode coordinator;
	private LapisNode localNode;
	
	public void addNode(LapisNode lapisNode) {
		if (nodeMap.containsKey(lapisNode.getNodeName())) {
			if (nodeMap.get(lapisNode.getNodeName()).getUrl() != lapisNode.getUrl()) {
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
			throw new IllegalArgumentException("Network table cannot update node \"" 
					+ lapisNode.getNodeName() + "\". The node is not present in the table.");
		}
	}
	
	public void updateAllNodes(List<LapisNode> lapisNodes) {
		synchronized (nodeMap) {
			nodeMap.clear();
			for(LapisNode node : lapisNodes) {
				nodeMap.put(node.getNodeName(), node);
			}
		}
	}
	
	public LapisNode removeNode(String nodeName) {
		return nodeMap.remove(nodeName);
	}
	
	public LapisNode removeNode(LapisNode lapisNode) {
		return removeNode(lapisNode.getNodeName());
	}
	
	public LapisNode getCoordinator() {
		return this.coordinator;
	}
	
	public void setCoordinator(LapisNode coordinator) {
		this.coordinator = coordinator;
	}

	public LapisNode getLocalNode() {
		return localNode;
	}

	public void setLocalNode(LapisNode localNode) {
		this.localNode = localNode;
	}
}