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
	
	//TODO MOVE AND RENAME
	public void setLapisSerializationInterface(
			LapisSerializationInterface lapisSerializationInterface) {
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
		InputStream inputStream = doCallAndGetInputStream(clientResource);
		return lapisSerializationInterface.deserializeVariableMetaData(inputStream);
	}
	
	@Override
	public LapisDatum getVariableValue(VariableFullName varName) {
		validateModelInVariableName(varName);
		ClientResource clientResource = getVariableValueClientResource(varName);
		InputStream inputStream = doCallAndGetInputStream(clientResource);
		return lapisSerializationInterface.deserializeLapisDatum(inputStream);
	}
	
	private void validateModelInVariableName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = networkTable.getNode(modelName);
		if(lapisNode == null) {
			//TODO HANDLE
		}
	}
	
	private ClientResource getVariableMetaDataClientResource(VariableFullName varName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ClientResource getVariableValueClientResource(VariableFullName varName) {
		//TODO auto-generated method stub
		return null;
	}

	InputStream doCallAndGetInputStream(ClientResource clientResource) {
		try {
			return clientResource.get().getStream();
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}