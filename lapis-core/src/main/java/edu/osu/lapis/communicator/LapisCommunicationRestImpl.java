package edu.osu.lapis.communicator;

import java.io.IOException;
import java.io.InputStream;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.LapisDatum;
import edu.osu.lapis.serialize.LapisSerializationInterface;

public class LapisCommunicationRestImpl implements LapisCommunication {
	
	//TODO RE-ORDER MEMBERS
	
	private NetworkTable networkTable;
	private LapisSerializationInterface lapisSerializationInterface;
	private String variableValuePath;
	private String variableMetaDataPath;
	
	//TODO MOVE AND RENAME
	public void setLapisSerializationInterface(LapisSerializationInterface lapisSerializationInterface) {
		this.lapisSerializationInterface = lapisSerializationInterface;
	}

	//TODO MOVE
	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	@Override
	public VariableMetaData getVariableMetaData(VariableFullName varName) {
		validateModelInVariableName(varName);
		ClientResource clientResource = getVariableMetaDataClientResource(varName);
		try (InputStream input = clientResource.get().getStream()) {
			return lapisSerializationInterface.deserializeVariableMetaData(input);
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public LapisDatum getVariableValue(VariableFullName varName) {
		validateModelInVariableName(varName);
		ClientResource clientResource = getVariableValueClientResource(varName);
		try (InputStream input = clientResource.get().getStream()) {
			return lapisSerializationInterface.deserializeLapisDatum(input);
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void validateModelInVariableName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = networkTable.getNode(modelName);
		if(lapisNode == null) {
			//check with coordinator to see if address of node has changed
			//TODO IMPLEMENT
		}
	}
	
	private ClientResource getVariableMetaDataClientResource(VariableFullName varName) {
		return new ClientResource(getUri(varName, variableMetaDataPath));
	}
	
	private ClientResource getVariableValueClientResource(VariableFullName varName) {
		return new ClientResource(getUri(varName, variableValuePath));
	}
	
	private String getUri(VariableFullName varName, String path) {
		LapisNode lapisNode = networkTable.getNode(varName.getModelName());
		String url = lapisNode.getUrl();
		StringBuilder sb = new StringBuilder(url);
		if(!url.endsWith("/")) {
			sb.append("/");
		}
		sb.append(path);
		return sb.toString();
	}
}