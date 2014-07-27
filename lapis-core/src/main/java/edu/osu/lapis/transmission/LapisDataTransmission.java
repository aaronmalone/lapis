package edu.osu.lapis.transmission;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import edu.osu.lapis.comm.client.Client;
import edu.osu.lapis.comm.client.Method;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.network.LapisNetwork;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.util.LapisRestletUtils;

public class LapisDataTransmission {

	private static final String
			VARIABLE_VALUE_PATH = "model",
			VARIABLE_META_DATA_PATH = "metadata";

	private final LapisNetwork lapisNetwork;
	private final Client client;

	public LapisDataTransmission(LapisNetwork lapisNetwork, Client client) {
		this.lapisNetwork = lapisNetwork;
		this.client = client;
	}

	private byte[] doGet(LapisNode remoteNode, String... pathParts) {
		Preconditions.checkNotNull(remoteNode, "remoteNode cannot be null");
		String[] array = new String[pathParts.length + 1];
		array[0] = remoteNode.getUrl();
		int i = 1;
		for (String part : pathParts) {
			array[i++] = part;
		}
		String uri = LapisRestletUtils.buildUri(array);
		return client.doCall(Method.GET, uri);
	}

	public byte[] getVariableMetaDataForNode(String nodeName) {
		LapisNode remoteNode = lapisNetwork.getNode(nodeName);
		return doGet(remoteNode, VARIABLE_META_DATA_PATH);
	}

	public byte[] getVariableValue(VariableFullName variableFullName) {
		return doGet(getValidLapisNodeInVarName(variableFullName),
				VARIABLE_VALUE_PATH, variableFullName.getLocalName());
	}

	public byte[] getVariableMetaData(VariableFullName variableFullName) {
		return doGet(getValidLapisNodeInVarName(variableFullName),
				VARIABLE_META_DATA_PATH, variableFullName.getLocalName());
	}

	public void setVariableValue(VariableFullName variableFullName, byte[] serialized) {
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		String host = remoteNode.getUrl();
		String name = variableFullName.getLocalName();
		client.doCall(Method.POST, serialized, host, VARIABLE_VALUE_PATH, name);
	}

	private LapisNode getValidLapisNodeInVarName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = lapisNetwork.getNode(modelName);
		Verify.verifyNotNull(lapisNode, "Node %s is not on the network!", modelName);
		return lapisNode;
	}
}
