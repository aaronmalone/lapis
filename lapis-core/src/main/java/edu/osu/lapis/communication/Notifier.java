package edu.osu.lapis.communication;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.*;
import edu.osu.lapis.transmission.ClientCall;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.transmission.ClientCall.RestMethod;
import edu.osu.lapis.util.LapisRestletUtils;

/**
 * Class used by LAPIS network coordinator to notify network nodes of changes  
 * in the network.
 * This class is in the package with the "communication layer" objects, but 
 * probably shouldn't be considered part of the communication layer right now.
 */
public class Notifier { //TODO ADD UNIT TESTS FOR THIS
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private LapisTransmission lapisTransmission; //TODO SET
	
	/**
	 * Notifies all network nodes of a change in some node's information.
	 * @param updatedNode the updated node
	 */
	public void notifyNetworkOfUpdate(LapisNode updatedNode) {
		byte[] nodeData = lapisSerialization.serialize(updatedNode);
		notifyInternal(updatedNode, POST, nodeData);
	}
	
	public void notifyNetworkOfNewNode(LapisNode newNode) {
		byte[] nodeData = lapisSerialization.serialize(newNode);
		notifyInternal(newNode, PUT, nodeData);
	}
	
	public void notifyNetworkOfDelete(LapisNode node) {
		notifyInternal(node, DELETE, null);
	}
	
	/**
	 * Notifies all network nodes of some change in a node. The changed node is 
	 * NOT notified -- it is assumed that the changed node is already aware of 
	 * its changes.
	 * @param changedNode the node that has been changed (or is new or being 
	 * deleted)
	 * @param methodToUse the REST method to use
	 * @param data the serialized data to use in the notification
	 */
	private void notifyInternal(LapisNode changedNode, RestMethod methodToUse, byte[] data) {
		//creates a new thread to notify each network node... 
		// a future/better implementation might use a thread pool instead
		for(LapisNode node : networkTable.getNodesList()) {
			System.out.println("\nCHANGED NODE IS " + changedNode + "; loop node is" + node); //TODO REMOVE
			if(!changedNode.equals(node)) {
				System.out.println("These are not the same node"); //TODO REMOVE
				String path = "network/" + node.getNodeName();
				System.out.println("Will use relative URL " + path); //TODO REMOVE
				Runnable notificationRunnable = getNotificationRunnable(node, path, data, methodToUse); 
				Thread thread = new Thread(notificationRunnable);
				//TODO MAYbE SET UNCAUGHT EXCEPTION HANDLER?
				thread.start();
			}
		}
	}
	
	private Runnable getNotificationRunnable(final LapisNode nodeToNotify, final String relativeUrl, 
			final byte[] dataToSend, final RestMethod methodToUse) {
		Validate.notNull(methodToUse, "REST method must not be null.");
		Validate.isTrue(methodToUse != GET, "GET method not applicable for notiffier.");
		return new Runnable() {
			@Override public void run() {
				String uri = LapisRestletUtils.buildUri(nodeToNotify.getUrl(), relativeUrl);
				ClientCall clientCall = new ClientCall(methodToUse, uri, dataToSend);
				lapisTransmission.executeClientCall(clientCall);
			}
		};
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setLapisTransmission(LapisTransmission lapisTransmission) {
		this.lapisTransmission = lapisTransmission;
	}
}
