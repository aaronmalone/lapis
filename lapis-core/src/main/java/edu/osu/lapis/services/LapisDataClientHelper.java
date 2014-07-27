package edu.osu.lapis.services;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import edu.osu.lapis.client.Client;
import edu.osu.lapis.client.ClientMethod;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNetwork;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.util.ClientUtils;

import java.util.List;

public class LapisDataClientHelper {

	private static final String
			VARIABLE_VALUE_PATH = "model",
			VARIABLE_META_DATA_PATH = "metadata";

	private final LapisSerialization lapisSerialization;
	private final LapisNetwork lapisNetwork;
	private final Client client;

	public LapisDataClientHelper(LapisSerialization lapisSerialization, LapisNetwork lapisNetwork, Client client) {
		this.lapisSerialization = lapisSerialization;
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
		String uri = ClientUtils.buildUri(array);
		return client.doCall(ClientMethod.GET, uri);
	}

	public List<VariableMetaData> getVariableMetaDataForNode(String nodeName) {
		LapisNode remoteNode = lapisNetwork.getNode(nodeName);
		byte[] bytes = doGet(remoteNode, VARIABLE_META_DATA_PATH);
		return lapisSerialization.deserializeMetaDataList(bytes);
	}

	public <T> T getVariableValue(VariableFullName variableFullName, Class<T> expectedClass) {
		byte[] bytes = doGet(getValidLapisNodeInVarName(variableFullName),
				VARIABLE_VALUE_PATH, variableFullName.getLocalName());
		SerializationObject serializationObj = lapisSerialization.deserializeModelData(bytes);
		Object dataObj = serializationObj.getData();
		if (expectedClass.isInstance(dataObj)) {
			return expectedClass.cast(dataObj);
		} else if (expectedClass.equals(double[].class) && dataObj instanceof Double) {
			double value = (Double) dataObj;
			return (T) new double[]{value};
		} else {
			throw new IllegalStateException("Couldn't cast dataObj " + dataObj + " of type "
					+ dataObj.getClass() + " to " + expectedClass + ".");
		}
	}

	public VariableMetaData getVariableMetaData(VariableFullName variableFullName) {
		byte[] bytes = doGet(getValidLapisNodeInVarName(variableFullName),
				VARIABLE_META_DATA_PATH, variableFullName.getLocalName());
		return lapisSerialization.deserializeMetaData(bytes);
	}

	public void setVariableValue(VariableFullName variableFullName, Object valueToSet) {
		String name = variableFullName.getLocalName();
		SerializationObject obj = new SerializationObject(name, valueToSet);
		byte[] serialized = lapisSerialization.serialize(obj);
		LapisNode remoteNode = getValidLapisNodeInVarName(variableFullName);
		client.doCall(ClientMethod.POST, serialized, remoteNode.getUrl(), VARIABLE_VALUE_PATH, name);
	}

	private LapisNode getValidLapisNodeInVarName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = lapisNetwork.getNode(modelName);
		Verify.verifyNotNull(lapisNode, "Node %s is not on the network!", modelName);
		return lapisNode;
	}
}
