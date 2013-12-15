package edu.osu.lapis.comm;

import static edu.osu.lapis.transmission.ClientCall.RestMethod.DELETE;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.GET;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.POST;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.PUT;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.ClientCall;
import edu.osu.lapis.transmission.ClientCall.RestMethod;
import edu.osu.lapis.transmission.LapisTransmission;
import edu.osu.lapis.util.LapisRestletUtils;

/**
 * Class used by LAPIS network coordinator to notify network nodes of changes  
 * in the network.
 * This class is in the package with the "communication layer" objects, but 
 * probably shouldn't be considered part of the communication layer right now.
 */
public class Notifier {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private LapisTransmission lapisTransmission;
	
	/**
	 * Notifies all network nodes of a change in some node's information.
	 * @param updatedNode the updated node
	 */
	public void notifyNetworkOfUpdate(LapisNode updatedNode) {
		log.info("Notifying network of updated node: {}", updatedNode);
		byte[] nodeData = lapisSerialization.serialize(updatedNode);
		notifyInternal(updatedNode, POST, nodeData);
	}
	
	public void notifyNetworkOfNewNode(LapisNode newNode) {
		log.info("Notifying network of new node: {}", newNode);
		byte[] nodeData = lapisSerialization.serialize(newNode);
		notifyInternal(newNode, PUT, nodeData);
	}
	
	public void notifyNetworkOfDelete(LapisNode node) {
		log.info("Notifying network of deleted node: {}", node);
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
			if(!changedNode.equals(node)) {
				String path = "network/" + changedNode.getNodeName();
				Runnable notificationRunnable = getNotificationRunnable(node, path, data, methodToUse); 
				Thread thread = new Thread(notificationRunnable);
				//TODO LOOK AT THREAD GROUPS
				//TODO MAYBE SET UNCAUGHT EXCEPTION HANDLER?
				thread.start();
			}
		}
	}
	
	private Runnable getNotificationRunnable(final LapisNode nodeToNotify, final String relativeUrl, 
			final byte[] dataToSend, final RestMethod methodToUse) {
		Validate.notNull(methodToUse, "REST method must not be null.");
		Validate.isTrue(methodToUse != GET, "GET method not applicable for notifier.");
		return new Runnable() {
			@Override public void run() {
				String uri = LapisRestletUtils.buildUri(nodeToNotify.getUrl(), relativeUrl);
				ClientCall clientCall = new ClientCall(methodToUse, uri, dataToSend);
				try {
					log.debug("About to execute notification with client call {}", clientCall);
					lapisTransmission.executeClientCall(clientCall);					
				} catch(Throwable t) {
					log.error("Error executing notification with client call " + clientCall + ".", t);
					throw new RuntimeException(t);
				}
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
