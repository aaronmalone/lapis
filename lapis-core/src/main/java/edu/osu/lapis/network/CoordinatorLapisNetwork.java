package edu.osu.lapis.network;

import com.google.common.base.Preconditions;

import java.util.*;

public class CoordinatorLapisNetwork implements LapisNetwork {

	private final Map<String, LapisNode> nodeMap = Collections.synchronizedMap(new HashMap<String, LapisNode>());
	private final LapisNode thisNode;

	public CoordinatorLapisNetwork(LapisNode thisNode) {
		Preconditions.checkNotNull(thisNode);
		this.thisNode = thisNode;
	}

	@Override
	public LapisNode getNode(String nodeName) {
		if (this.thisNode.getNodeName().equals(nodeName)) {
			return this.thisNode;
		} else {
			return nodeMap.get(nodeName);
		}
	}

	@Override
	public LapisNode getLocalNode() {
		return this.thisNode;
	}

	/**
	 * Adds a node to the coordinator's internal collection of nodes on the network.
	 */
	public void addNode(LapisNode newNode) {
		Preconditions.checkArgument(!this.thisNode.equals(newNode),
				"Attempted to add node to network that was actually the coordinator node.");
		this.nodeMap.put(newNode.getNodeName(), newNode);
	}

	/**
	 * Removes a node from coordinator's internal collection of nodes on the network.
	 */
	@Override
	public void removeNode(String nodeName) {
		Preconditions.checkNotNull(nodeName);
		this.nodeMap.remove(nodeName);
	}

	public List<LapisNode> allNodes() {
		return new ArrayList<LapisNode>(this.nodeMap.values());
	}
}
