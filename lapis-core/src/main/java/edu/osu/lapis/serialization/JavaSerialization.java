package edu.osu.lapis.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public class JavaSerialization implements LapisSerialization {

	private byte[] serializeInternal(Serializable serializable) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(baos);
			objectOut.writeObject(serializable);
			objectOut.close();
		} catch (IOException e) {
			throw new RuntimeException("Error serializing " + serializable, e);
		}
		return baos.toByteArray();
	}
	
	private <T> T deserializeInputStream(InputStream inputStream, Class<T> cls) {
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			Object object = objectInputStream.readObject();
			if(cls.isAssignableFrom(object.getClass())) {
				return cls.cast(object);
			} else {
				throw new IllegalArgumentException("Cannot deserialize " + object + " as " + cls);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing object.", e);
		}
	}
	
	@Override
	public byte[] serialize(SerializationObject serializationObject) {
		return serializeInternal(serializationObject);
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return serializeInternal(variableMetaData);
	}

	@Override
	public byte[] serialize(List<VariableMetaData> variableMetaDataList) {
		return serializeInternal(new ArrayList<VariableMetaData>(variableMetaDataList));
	}

	@Override
	public byte[] serialize(LapisNode lapisNode) {
		return serializeInternal(lapisNode);
	}
	
	@Override
	public byte[] serialize(LapisNode[] lapisNodes) {
		return serializeInternal(lapisNodes);
	}

	@Override
	public List<VariableMetaData> deserializeMetaDataList(byte[] serialized) {
		return deserializeMetaDataList(new ByteArrayInputStream(serialized));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VariableMetaData> deserializeMetaDataList(InputStream inputStream) {
		return deserializeInputStream(inputStream, List.class);
	}
	
	@Override
	public SerializationObject deserializeModelData(byte[] serialized) {
		return deserializeModelData(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public SerializationObject deserializeModelData(InputStream inputStream) {
		return deserializeInputStream(inputStream, SerializationObject.class);
	}
	
	@Override
	public VariableMetaData deserializeMetaData(byte[] serialized) {
		return deserializeMetaData(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public VariableMetaData deserializeMetaData(InputStream inputStream) {
		return deserializeInputStream(inputStream, VariableMetaData.class);
	}
	
	@Override
	public LapisNode deserializeLapisNode(byte[] serialized) {
		return deserializeLapisNode(new ByteArrayInputStream(serialized));
	}

	@Override
	public LapisNode deserializeLapisNode(InputStream inputStream) {
		return deserializeInputStream(inputStream, LapisNode.class);
	}
	
	@Override
	public List<LapisNode> deserializeNetworkData(byte[] serialized) {
		return deserializeNetworkData(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public List<LapisNode> deserializeNetworkData(InputStream inputStream) {
		return Arrays.asList(deserializeInputStream(inputStream, LapisNode[].class));
	}
}
