package edu.osu.lapis.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisNetworkTransmission;

//communication layer
public class NetworkClientCommunicationImpl {
	
	private LapisNetworkTransmission lapisNetworkTransmission; //TODO SET
	private LapisSerialization lapisSerialization; //TODO SET
	
	public List<LapisNode> getAllLapisNodesOnNetwork() {
		try(InputStream stream = lapisNetworkTransmission.getAllLapisNodesOnNetwork()) {			
			return lapisSerialization.deserializeNetworkData(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public LapisNode getLapisNode(String nodeName) {
		try (InputStream stream = lapisNetworkTransmission.getLapisNode(nodeName)) {
			return lapisSerialization.deserializeLapisNode(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
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

	public void setLapisNetworkTransmission(
			LapisNetworkTransmission lapisNetworkTransmission) {
		this.lapisNetworkTransmission = lapisNetworkTransmission;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
