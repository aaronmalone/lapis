package edu.osu.lapis.transmission;

//import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import edu.osu.lapis.LapisNetworkClient;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;

public class LapisDataTransmission {
	
	private LapisNetworkClient lapisNetworkClient; //TODO SET
	private String variableValuePath;
	private String variableMetaDataPath; //TODO SET
	private MediaType serializationMediaType; //TODO SET
	
	public InputStream getVariableValue(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableValuePath, name);
		ClientResource clientResource = new ClientResource(uri);
		return LapisRestletUtils.callGetAndReturnStream(clientResource);
	}
	
	public InputStream getVariableMetaData(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableMetaDataPath, name);
		ClientResource clientResource = new ClientResource(uri);
		return LapisRestletUtils.callGetAndReturnStream(clientResource);
	}
	
	public void setVariableValue(VariableFullName variableFullName, byte[] serialized) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableValuePath, name);
		ClientResource clientResource = new ClientResource(uri);
		Representation entity = LapisRestletUtils.createRepresentation(serialized, serializationMediaType);
		clientResource.post(entity);
		//TODO SERVER MUST RETURN SUCCESS OR EXCEPTION IS THROWN
	}
	
	private LapisNode getValidLapisNodeInVarName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = lapisNetworkClient.getLapisNode(modelName);
		assert lapisNode != null;
		return lapisNode;
	}

	public void setLapisNetworkClient(LapisNetworkClient lapisNetworkClient) {
		this.lapisNetworkClient = lapisNetworkClient;
	}

	public void setVariableValuePath(String variableValuePath) {
		this.variableValuePath = variableValuePath;
	}

	public void setVariableMetaDataPath(String variableMetaDataPath) {
		this.variableMetaDataPath = variableMetaDataPath;
	}

	public void setSerializationMediaType(MediaType serializationMediaType) {
		this.serializationMediaType = serializationMediaType;
	}
}