package edu.osu.lapis.services;

import edu.osu.lapis.Logger;
import edu.osu.lapis.client.Client;
import edu.osu.lapis.client.ClientMethod;
import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.network.LapisNetwork;
import edu.osu.lapis.network.LapisNode;

public class HeartbeatClient {

	private Logger logger = Logger.getLogger(getClass());

	private final LapisNetwork lapisNetwork;
	private final Client client;

	public HeartbeatClient(LapisNetwork lapisNetwork, Client client) {
		this.lapisNetwork = lapisNetwork;
		this.client = client;
	}

	/**
	 * Checks whether the specified node is still up by accessing the node's
	 * heartbeat resource. Returns true if the node is still up.
	 * <p/>
	 * If the node is not on the network, returns false.
	 * <p/>
	 * Note this does not guarantee that the node is in a good state, only that
	 * the heartbeat resource is accessible.
	 */
	public boolean checkHeartbeat(String nodeName) {
		LapisNode node = lapisNetwork.getNode(nodeName);
		if (node != null) {
			try {
				client.doCall(ClientMethod.GET, node.getUrl(), "heartbeat");
				return true;
			} catch (LapisClientException e) {
				logger.warn("Node " + nodeName + " did not respond to heartbeat.", e);
				return false;
			}
		} else {
			return false;
		}
	}
}
