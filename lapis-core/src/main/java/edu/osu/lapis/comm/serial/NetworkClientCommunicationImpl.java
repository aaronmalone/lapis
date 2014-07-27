package edu.osu.lapis.comm.serial;

/**
 * The "communication layer" object for the network data.
 * This layer handles serialization and deserialization, but does not deal with
 * the underlying network protocols.
 * This layer also wraps exceptions with more useful messages.
 */
@Deprecated
public class NetworkClientCommunicationImpl {
	/*
	private final LapisNetworkTransmission lapisNetworkTransmission;
	private final LapisSerialization lapisSerialization;
	
	public NetworkClientCommunicationImpl(
			LapisNetworkTransmission lapisNetworkTransmission,
			LapisSerialization lapisSerialization) {
		this.lapisNetworkTransmission = lapisNetworkTransmission;
		this.lapisSerialization = lapisSerialization;
	}
	
	public List<LapisNode> getAllLapisNodesOnNetwork() {
		byte[] data = lapisNetworkTransmission.getAllLapisNodesOnNetwork();
		return lapisSerialization.deserializeNetworkData(data);
	}
	
	public LapisNode getLapisNode(String nodeName) {
		byte[] data = lapisNetworkTransmission.getLapisNode(nodeName);
		return lapisSerialization.deserializeLapisNode(data);
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
	}*/
}
