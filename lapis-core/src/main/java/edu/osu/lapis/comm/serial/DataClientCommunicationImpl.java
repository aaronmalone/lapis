package edu.osu.lapis.comm.serial;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.transmission.LapisDataTransmission;

import java.util.List;

/**
 * The "communication layer" object for LAPIS data.
 * The communication layer handles serialization and deserialization, but does
 * not deal directly with the underlying protocols used for communication.
 */
public class DataClientCommunicationImpl {

	private final LapisSerialization lapisSerialization;
	private final LapisDataTransmission lapisDataTransmission;

	public DataClientCommunicationImpl(LapisSerialization lapisSerialization,
									   LapisDataTransmission lapisDataTransmission) {
		this.lapisSerialization = lapisSerialization;
		this.lapisDataTransmission = lapisDataTransmission;
	}

	public List<VariableMetaData> getVariableMetaDataForNode(String nodeName) {
		byte[] data = lapisDataTransmission.getVariableMetaDataForNode(nodeName);
		return lapisSerialization.deserializeMetaDataList(data);
	}

	public VariableMetaData getVariableMetaData(VariableFullName fullName) {
		byte[] data = lapisDataTransmission.getVariableMetaData(fullName);
		return lapisSerialization.deserializeMetaData(data);
	}

	@SuppressWarnings("unchecked")
	public <T> T getVariableValue(VariableFullName fullName, Class<T> expectedClass) {
		byte[] data = lapisDataTransmission.getVariableValue(fullName);
		SerializationObject serializationObj = lapisSerialization.deserializeModelData(data);
		Object dataObj = serializationObj.getData();
		if (expectedClass.isInstance(dataObj)) {
			return expectedClass.cast(dataObj);
		} else if (expectedClass.equals(double[].class) && dataObj instanceof Double) {
			double value = ((Double) dataObj).doubleValue();
			return (T) new double[]{value};
		} else {
			throw new RuntimeException("Couldn't cast dataObj " + dataObj + " of type "
					+ dataObj.getClass() + " to " + expectedClass + ".");
		}
	}

	public <T> void setVariableValue(VariableFullName fullName, T value) {
		SerializationObject obj = new SerializationObject(fullName.getLocalName(), value);
		byte[] serialized = lapisSerialization.serialize(obj);
		lapisDataTransmission.setVariableValue(fullName, serialized);
	}
}
