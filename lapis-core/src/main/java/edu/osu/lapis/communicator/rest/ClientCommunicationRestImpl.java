package edu.osu.lapis.communicator.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import edu.osu.lapis.communicator.ClientCommunication;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.SerializationObject;
import edu.osu.lapis.serialize.LapisSerialization;

public class ClientCommunicationRestImpl implements ClientCommunication {
	
	private NetworkTable networkTable;
	private LapisSerialization lapisSerialization;
	private String variableValuePath;
	private String variableMetaDataPath;
	private String networkDataPath;

	@Override
	public VariableMetaData getVariableMetaData(VariableFullName varName) {
		validateModelInVariableName(varName);
		ClientResource clientResource = getVariableMetaDataClientResource(varName);
		try (InputStream input = clientResource.get().getStream()) {
			return lapisSerialization.deserializeMetaData(input);
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public SerializationObject getVariableValue(VariableFullName varName) {
		validateModelInVariableName(varName);
		ClientResource clientResource = getVariableValueClientResource(varName);
		try (InputStream input = clientResource.get().getStream()) {
			return lapisSerialization.deserializeModelData(input);
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void validateModelInVariableName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = networkTable.getNode(modelName);
		//TODO REFACTOR/CLEAN-UP AT SOME POINT
		if(lapisNode == null) {
			//check with coordinator to see if address of node has changed
			LapisNode coordinatorNode = networkTable.getCoordinator();
			ClientResource clientResource = getNetworkDataClientResource(coordinatorNode);
			try (InputStream input = clientResource.get().getStream()) {
				List<LapisNode> nodes = lapisSerialization.deserializeNetworkData(input);
				networkTable.updateAllNodes(nodes);
				if(networkTable.getNode(modelName) == null) {
					throw new IllegalStateException("Model \"" + modelName + "\" is not on the network.");
				}
			} catch (ResourceException | IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private ClientResource getVariableMetaDataClientResource(VariableFullName varName) {
		LapisNode lapisNode = networkTable.getNode(varName.getModelName());
		return new ClientResource(getUri(lapisNode, variableMetaDataPath));
	}
	
	private ClientResource getVariableValueClientResource(VariableFullName varName) {
		LapisNode lapisNode = networkTable.getNode(varName.getModelName());
		return new ClientResource(getUri(lapisNode, variableValuePath));
	}
	
	private ClientResource getNetworkDataClientResource(LapisNode coordinatorNode) {
		return new ClientResource(getUri(coordinatorNode, networkDataPath));
	}
	
	private String getUri(LapisNode lapisNode, String path) {
		String url = lapisNode.getUrl();
		StringBuilder sb = new StringBuilder(url);
		if(!url.endsWith("/")) {
			sb.append("/");
		}
		sb.append(path);
		return sb.toString();
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setVariableValuePath(String variableValuePath) {
		this.variableValuePath = variableValuePath;
	}

	public void setVariableMetaDataPath(String variableMetaDataPath) {
		this.variableMetaDataPath = variableMetaDataPath;
	}

	public void setNetworkDataPath(String networkDataPath) {
		this.networkDataPath = networkDataPath;
	}
}