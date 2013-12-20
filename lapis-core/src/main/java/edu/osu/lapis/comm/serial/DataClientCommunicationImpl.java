package edu.osu.lapis.comm.serial;

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
	
	public VariableMetaData getVariableMetaData(VariableFullName fullName) {
		try {
			byte[] data = lapisDataTransmission.getVariableMetaData(fullName);
			return lapisSerialization.deserializeMetaData(data);
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving variable meta-data for " + fullName, e);
		}
	}

	public <T> T getVariableValue(VariableFullName fullName, Class<T> cls) {
		Object dataObj;
		try {
			byte[] data = lapisDataTransmission.getVariableValue(fullName);
			SerializationObject serializationObj = lapisSerialization.deserializeModelData(data);
			dataObj = serializationObj.getData();
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving variable value: " + fullName, e);
		}
		//TODO ADD HANDLING FOR EMPTY ARRAY
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