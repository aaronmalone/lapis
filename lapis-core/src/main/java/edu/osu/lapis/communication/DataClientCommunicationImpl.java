package edu.osu.lapis.communication;

import java.io.IOException;
import java.io.InputStream;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.transmission.LapisDataTransmission;

//communication layer
public class DataClientCommunicationImpl { //TODO RENAME

	private LapisSerialization lapisSerialization; //TODO SET
	private LapisDataTransmission lapisDataTransmission;
	
	public VariableMetaData getVariableMetaData(VariableFullName fullName) {
		try(InputStream in = lapisDataTransmission.getVariableMetaData(fullName)) {
			return lapisSerialization.deserializeMetaData(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO MAYBE REMOVE SERIALIZATION OBJECT AT THIS LEVEL
	public LapisVariable getVariableValue(VariableFullName fullName) {
		try (InputStream in = lapisDataTransmission.getVariableValue(fullName)) {
			SerializationObject serializationObj = lapisSerialization.deserializeModelData(in);
			return serializationObj.toLapisVariable();
		} catch (IOException e) {
			throw new RuntimeException(e);
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