package edu.osu.lapis.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

//TODO ADD PROTECTION FOR LOCAL NODE
public class NetworkTable {

	private final Map<String, LapisNode> nodeMap = Collections.synchronizedMap(new HashMap<String, LapisNode>());
	private LapisNode coordinator;
	private LapisNode localNode;
	
	public void addNode(LapisNode newNode) { //TODO TEST
		Validate.isTrue(!localNode.equals(newNode), "Attempting to add new node %s that " 
				+ "has same name as local (this) node %s.", newNode, localNode); //TODO REMOVE VALIDATION at some point
		String nodeName = newNode.getNodeName();
		LapisNode existingNode = nodeMap.get(nodeName);
		if(existingNode == null) {
			nodeMap.put(nodeName, newNode);
		} else {
			//allow adding a node if it has the same URL
			String existingUrl = existingNode.getUrl();
			if(!existingUrl.equals(newNode.getUrl())) {
				throw new IllegalArgumentException("Attempted to add node " + newNode 
						+ " but already had an existing node with the same name: " 
						+ existingNode);
			}
		}
		nodeMap.put(newNode.getNodeName(), newNode);
	}
	
	public LapisNode getNode(String nodeName) {
		if(localNode.getNodeName().equals(nodeName))
			return localNode;
		else 
			return nodeMap.get(nodeName);
	}
	
	/**
	 * Returns all the nodes in the network except the local node (this node).
	 */
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