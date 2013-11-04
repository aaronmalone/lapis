package edu.osu.lapis.communicator.rest;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.resource.ClientResource;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.transmission.LapisRestletUtils;

/**
 * Class used by LAPIS network coordinator to notify network nodes of changes  
 * in the network.
 */
public class Notifier {
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private MediaType mediaType;
	
	public void notifyNetworkOfUpdate(LapisNode updatedNode) {
		byte[] nodeData = lapisSerialization.serialize(updatedNode);
		notifyInternal(updatedNode, Method.POST, nodeData);
	}
	
	public void notifyNetworkOfNewNode(LapisNode newNode) {
		byte[] nodeData = lapisSerialization.serialize(newNode);
		notifyInternal(newNode, Method.PUT, nodeData);
	}
	
	public void notifyNetworkOfDelete(LapisNode node) {
		notifyInternal(node, Method.DELETE, null);
	}
	
	private void notifyInternal(LapisNode changedNode, Method methodToUse, byte[] data) {
		//creates a new thread to notify each network node... 
		// a future/better implementation might use a thread pool instead
		for(LapisNode node : networkTable.getNodesList()) {
			if(!changedNode.equals(node)) {
				String relativeUrl = "network/" + changedNode.getNodeName();
				Runnable notificationRunnable = getNotificationRunnable(node, relativeUrl, data, methodToUse); 
				Thread thread = new Thread(notificationRunnable);
				thread.start();
			}
		}
	}
	
	private Runnable getNotificationRunnable(final LapisNode nodeToNotify, final String relativeUrl, 
			final byte[] dataToSend, final Method methodToUse) {
		return new Runnable() {
			@Override public void run() {
				String uri = LapisRestletUtils.buildUri(nodeToNotify.getUrl(), relativeUrl);
				ClientResource clientResource = new ClientResource(uri);
				if(Method.PUT.equals(methodToUse)) {
					clientResource.put(LapisRestletUtils.createRepresentation(dataToSend, mediaType));
				} else if(Method.POST.equals(methodToUse)) {
					clientResource.post(LapisRestletUtils.createRepresentation(dataToSend, mediaType));
				} else if(Method.DELETE.equals(methodToUse)) {
					clientResource.delete();
				} else {
					throw new IllegalStateException("No way to handle method " + methodToUse);
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

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
}
