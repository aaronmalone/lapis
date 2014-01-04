package edu.osu.lapis.transmission;

import static edu.osu.lapis.transmission.ClientCall.RestMethod.GET;
import static edu.osu.lapis.transmission.ClientCall.RestMethod.POST;
import edu.osu.lapis.comm.client.LapisNetworkClient;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.util.LapisRestletUtils;

public class LapisDataTransmission {
	
	private LapisTransmission lapisTransmission;
	private LapisNetworkClient lapisNetworkClient;
	private String variableValuePath;
	private String variableMetaDataPath;
	
	private byte[] doGet(String uri) {
		return lapisTransmission.executeClientCallReturnBytes(new ClientCall(GET, uri));
	}
	
	public byte[] getVariableMetaDataForNode(String nodeName) {
		LapisNode remoteNode = lapisNetworkClient.getLapisNode(nodeName);
		String uri = LapisRestletUtils.buildUri(remoteNode.getUrl(), variableMetaDataPath);
		return doGet(uri);
	}
	
	public byte[] getVariableValue(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableValuePath, name);
		return doGet(uri);
	}
	
	public byte[] getVariableMetaData(VariableFullName variableFullName) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		String uri = LapisRestletUtils.buildUri(host, variableMetaDataPath, name);
		return doGet(uri);
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