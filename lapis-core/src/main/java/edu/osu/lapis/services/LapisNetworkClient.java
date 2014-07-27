package edu.osu.lapis.services;

import edu.osu.lapis.Logger;
import edu.osu.lapis.client.Client;
import edu.osu.lapis.client.ClientMethod;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;

import java.util.List;

public class LapisNetworkClient {

	static final String COORDINATOR = "coordinator";

	private Logger logger = Logger.getLogger(getClass());

	private final Client client;
	private final String coordinatorBaseUrl;
	private final LapisSerialization lapisSerialization;

	public LapisNetworkClient(Client client, LapisSerialization lapisSerialization, String coordinatorBaseUrl) {
		this.client = client;
		this.coordinatorBaseUrl = coordinatorBaseUrl;
		this.lapisSerialization = lapisSerialization;
	}

	public void deleteNodeFromNetwork(String nodeName) {
		client.doCall(ClientMethod.DELETE, coordinatorBaseUrl, COORDINATOR, nodeName);
	}

	public void addNodeToNetwork(String nodeName, byte[] nodeData) {
		client.doCall(ClientMethod.PUT, nodeData, coordinatorBaseUrl, COORDINATOR, nodeName);
	}

	public List<LapisNode> getAllLapisNodesOnNetwork() {
		logger.debug("Retrieving information for all nodes.");
		byte[] bytes = client.doCall(ClientMethod.GET, coordinatorBaseUrl, COORDINATOR);
		return lapisSerialization.deserializeNetworkData(bytes);
	}

	public LapisNode getLapisNode(String nodeName) {
		logger.debug("Retrieving node information for node: '%s'", nodeName);
		byte[] bytes = client.doCall(ClientMethod.GET, coordinatorBaseUrl, COORDINATOR, nodeName);
		return lapisSerialization.deserializeLapisNode(bytes);
	}
}
