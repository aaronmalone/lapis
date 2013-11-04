package edu.osu.lapis.transmission;

import java.io.InputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class LapisNetworkTransmission {
	
	private String coordinatorBaseUrl; //TODO SET
	private MediaType serializationMediaType; //TODO SET

	public void deleteNodeFromNetwork(String nodeName) {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, nodeName);
		new ClientResource(uri).delete();
	}
	
	public void addNodeToNetwork(String nodeName, byte[] nodeData) {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, nodeName);
		ClientResource clientResource = new ClientResource(uri);
		Representation entity = LapisRestletUtils.createRepresentation(nodeData, serializationMediaType);
		clientResource.put(entity);
	}
	
	public InputStream getAllLapisNodesOnNetwork() {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl);
		ClientResource clientResource = new ClientResource(uri);
		return LapisRestletUtils.callGetAndReturnStream(clientResource);
	}
	
	public InputStream getLapisNode(String nodeName) {
		String uri = LapisRestletUtils.buildUri(coordinatorBaseUrl, nodeName);
		ClientResource clientResource = new ClientResource(uri);
		return LapisRestletUtils.callGetAndReturnStream(clientResource);
	}

	public String getCoordinatorBaseUrl() {
		return coordinatorBaseUrl;
	}

	public void setCoordinatorBaseUrl(String coordinatorBaseUrl) {
		this.coordinatorBaseUrl = coordinatorBaseUrl;
	}

	public MediaType getSerializationMediaType() {
		return serializationMediaType;
	}

	public void setSerializationMediaType(MediaType serializationMediaType) {
		this.serializationMediaType = serializationMediaType;
	}
}