package edu.osu.lapis.transmission;

import static edu.osu.lapis.transmission.ClientCall.RestMethod.GET;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.POST;
import edu.osu.lapis.LapisNetworkClient;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.util.LapisRestletUtils;

public class LapisDataTransmission {
	
	private LapisTransmission lapisTransmission;
	private LapisNetworkClient lapisNetworkClient;
	private String variableValuePath;
	private String variableMetaDataPath;
	
	public byte[] getVariableValue(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableValuePath, name);
		return lapisTransmission.executeClientCallReturnBytes(new ClientCall(GET, uri));
	}
	
	public byte[] getVariableMetaData(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableMetaDataPath, name);
		return lapisTransmission.executeClientCallReturnBytes(new ClientCall(GET, uri));
	}
	
	public void setVariableValue(VariableFullName variableFullName, byte[] serialized) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableValuePath, name);
		ClientCall clientCall = new ClientCall(POST, uri, serialized);
		lapisTransmission.executeClientCall(clientCall);
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

	public void setLapisTransmission(LapisTransmission lapisTransmission) {
		this.lapisTransmission = lapisTransmission;
	}
}