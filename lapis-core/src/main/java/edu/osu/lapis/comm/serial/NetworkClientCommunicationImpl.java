package edu.osu.lapis.comm.serial;

import java.util.List;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisNetworkTransmission;

/**
 * The "communication layer" object for the network data.
 * This layer handles serialization and deserialization, but does not deal with 
 * the underlying network protocols.
 * This layer also wraps exceptions with more useful messages.
 *
 */
public class NetworkClientCommunicationImpl {
	
	private LapisNetworkTransmission lapisNetworkTransmission;
	private LapisSerialization lapisSerialization;
	
	public List<LapisNode> getAllLapisNodesOnNetwork() {
		try {
			byte[] data = lapisNetworkTransmission.getAllLapisNodesOnNetwork();
			return lapisSerialization.deserializeNetworkData(data);
		} catch(Exception e) {
			throw new RuntimeException("Error retrieving network information from coordinator.", e);
		}
	}
	
	public LapisNode getLapisNode(String nodeName) {
		try {
			byte[] data = lapisNetworkTransmission.getLapisNode(nodeName);
			return lapisSerialization.deserializeLapisNode(data);
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving data for node: " + nodeName, e);
		}
	}
	
	public void addNodeToNetwork(LapisNode lapisNode) {
		byte[] nodeDataSerialized = lapisSerialization.serialize(lapisNode);
		lapisNetworkTransmission.addNodeToNetwork(lapisNode.getNodeName(), nodeDataSerialized);
	}
	
	public void updateNodeOnNetwork(LapisNode lapisNode) {
		//maybe we implement this layer
		throw new UnsupportedOperationException("update not supported currently.");
	}
	
	public void deleteNodeFromNetwork(LapisNode lapisNode) {
		lapisNetworkTransmission.deleteNodeFromNetwork(lapisNode.getNodeName());
	}

	public void setLapisNetworkTransmission(LapisNetworkTransmission lapisNetworkTransmission) {
		this.lapisNetworkTransmission = lapisNetworkTransmission;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
