package edu.osu.lapis.communication;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;
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
public class DataClientCommunicationImpl { //TODO RENAME at some point
	//TODO MAYBE IT SHOULDN'T BE CALLED THE COMMUNICATION LAYER?

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

	//TODO MAYBE REMOVE SERIALIZATION OBJECT AT THIS LEVEL
	public Object getVariableValue(VariableFullName fullName) {
		try {
			byte[] data = lapisDataTransmission.getVariableValue(fullName);
			SerializationObject serializationObj = lapisSerialization.deserializeModelData(data);
			return serializationObj.getData();
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving variable value: " + fullName, e);
		}
	}

	public void setVariableValue(VariableFullName fullName, Object value) {
		SerializationObject obj = new SerializationObject();
		obj.setName(fullName.getLocalName());
		obj.setData(value);
		obj.setType(LapisDataType.getTypeForObject(value));
		obj.setDimension(Dimensions.getDimensions(value));
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