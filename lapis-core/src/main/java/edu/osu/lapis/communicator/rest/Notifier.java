package edu.osu.lapis.communicator.rest;

import org.restlet.data.Method;
import org.restlet.representation.Representation;
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
	
	/*

	1. node base URL -- will come from network table
	2. node relative URL -- will come from method called
	3. action/REST method -- will come from method called 

	for each node in network, create and start thread with runnable

	 */

	public void notifyNetworkOfUpdate(LapisNode updatedNode) {
		somethingOrOther(updatedNode, Method.POST, lapisSerialization.serialize(updatedNode));
	}
	
	public void notifyNetworkOfNewNode(LapisNode newNode) {
		somethingOrOther(newNode, Method.PUT, lapisSerialization.serialize(newNode));
	}
	
	public void notifyNetworkOfDelete(LapisNode node) {
		somethingOrOther(node, Method.DELETE, null);
	}
	
	private void somethingOrOther(LapisNode changedNode, Method methodToUse, byte[] data) {
		for(LapisNode node : networkTable.getNodesList()) {
			if(!changedNode.equals(node)) {
				String relativeUrl = "network/" + changedNode.getNodeName();
				UglyInnerClassRenameMe runnable = new UglyInnerClassRenameMe(node, relativeUrl, data, methodToUse); 
				Thread t = new Thread(runnable);
				t.start();
			}
		}
	}

	private static class UglyInnerClassRenameMe implements Runnable {
		
		private final LapisNode nodeToNotify;
		private final String relativeUrl;
		private final byte[] dataToSend;
		private final Method methodToUse;
		
		public UglyInnerClassRenameMe(LapisNode lapisNode, String relativeUrl, byte[] serializedData, Method method) {
			this.nodeToNotify = lapisNode;
			this.relativeUrl = relativeUrl;
			this.dataToSend = serializedData;
			this.methodToUse = method;
		}

		@Override public void run() {
			String uri = LapisRestletUtils.buildUri(nodeToNotify.getUrl(), relativeUrl);
			Representation entity = null;
			if(dataToSend != null) {
				entity = LapisRestletUtils.createRepresentation(dataToSend);
			}
			ClientResource clientResource = new ClientResource(uri);
			if(Method.PUT.equals(methodToUse)) {
				assert entity != null;
				clientResource.put(entity);
			} else if(Method.POST.equals(methodToUse)) {
				assert entity != null;
				clientResource.post(entity);
			} else if(Method.DELETE.equals(methodToUse)) {
				clientResource.delete();
			} else {
				throw new IllegalStateException("No way to handle method " + methodToUse);
			}
		}
	}
}
