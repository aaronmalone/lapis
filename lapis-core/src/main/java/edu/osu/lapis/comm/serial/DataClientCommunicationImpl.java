package edu.osu.lapis.comm.serial;

import java.util.List;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.transmission.LapisDataTransmission;

/**
 * The "communication layer" object for LAPIS data.
 * The communication layer handles serialization and deserialization, but does
 * not deal directly with the underlying protocols used for communication.
 */
public class DataClientCommunicationImpl {

	private LapisSerialization lapisSerialization;
	private LapisDataTransmission lapisDataTransmission;
	
	public List<VariableMetaData> getVariableMetaDataForNode(String nodeName) {
		byte[] data = lapisDataTransmission.getVariableMetaDataForNode(nodeName);
		return lapisSerialization.deserializeMetaDataList(data);
	}
	
	public VariableMetaData getVariableMetaData(VariableFullName fullName) {
		byte[] data = lapisDataTransmission.getVariableMetaData(fullName);
		return lapisSerialization.deserializeMetaData(data);
	}

	public <T> T getVariableValue(VariableFullName fullName, Class<T> cls) {
		byte[] data = lapisDataTransmission.getVariableValue(fullName);
		SerializationObject serializationObj = lapisSerialization.deserializeModelData(data);
		Object dataObj = serializationObj.getData();
		if(cls.isInstance(dataObj)) {
			return cls.cast(dataObj);
		} else {
			throw new RuntimeException("Couldn't cast dataObj " + dataObj + " of type " 
					+ dataObj.getClass() + " to " + cls + ".");
		}
	}

	public  <T> void setVariableValue(VariableFullName fullName, T value) {
		SerializationObject obj = new SerializationObject(fullName.getLocalName(), value);
		byte[] serialized = lapisSerialization.serialize(obj);
		lapisDataTransmission.setVariableValue(fullName, serialized);
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setLapisDataTransmission(LapisDataTransmission lapisDataTransmission) {
		this.lapisDataTransmission = lapisDataTransmission;
	}
}