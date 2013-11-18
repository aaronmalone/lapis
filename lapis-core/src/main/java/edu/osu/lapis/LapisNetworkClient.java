package edu.osu.lapis;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.osu.lapis.communication.NetworkClientCommunicationImpl;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;

public class LapisNetworkClient {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private NetworkTable networkTable;
	private NetworkClientCommunicationImpl networkClientCommunicationImpl;
	
	/**
	 * Retrieves all nodes on the LAPIS network.
	 * @return all nodes on the network
	 */
	public List<LapisNode> getAllNetworkNodes() {
		List<LapisNode> nodes = networkTable.getNodesList();
		if(nodes.isEmpty()) {
			return getAllNetworkNodesForceRefresh();
		} else {
			return nodes;
		}
	}
	
	/**
	 * Forces LAPIS to retrieve all of the nodes on the network from the coordinator.
	 * The local network table is updated and the list of nodes is returned.
	 */
	public List<LapisNode> getAllNetworkNodesForceRefresh() {
		List<LapisNode> nodesList = networkClientCommunicationImpl.getAllLapisNodesOnNetwork();
		networkTable.updateAllNodes(nodesList);
		return nodesList;
	}

	/**
	 * Gets the information for a single LAPIS node.
	 * @param nodeName the name of the node
	 */
	public LapisNode getLapisNode(String nodeName) {
		LapisNode node = networkTable.getNode(nodeName);
		if(node == null) {
			log.debug("Node {} not found in network table. Retrieving from coordinator.", nodeName);
			node = networkClientCommunicationImpl.getLapisNode(nodeName);
			networkTable.addNode(node);
		}
		return node;
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	public void setNetworkClientCommunicationImpl(NetworkClientCommunicationImpl networkClientCommunicationImpl) {
		this.networkClientCommunicationImpl = networkClientCommunicationImpl;
	}
}