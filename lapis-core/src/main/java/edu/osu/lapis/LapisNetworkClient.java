package edu.osu.lapis;

import java.util.List;

import edu.osu.lapis.communication.NetworkClientCommunicationImpl;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;

public class LapisNetworkClient {
	
	private NetworkTable networkTable; //TODO SET
	private NetworkClientCommunicationImpl renameMe; //TODO RENAME and SET
	
	public List<LapisNode> getAllNetworkNodes() {
		return networkTable.getNodesList();
	}
	
	public List<LapisNode> getAllNetworkNodesForceRefresh() {
		List<LapisNode> nodesList = renameMe.getAllLapisNodesOnNetwork();
		networkTable.updateAllNodes(nodesList);
		return nodesList;
	}
	
	public LapisNode getLapisNode(String nodeName) {
		LapisNode node = networkTable.getNode(nodeName);
		if(node == null) {
			node = renameMe.getLapisNode(nodeName);
			networkTable.addNode(node);
		}
		return node;
	}
}