package edu.osu.lapis.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteStreams;

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
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	@SuppressWarnings("unchecked")
	private <T> T deserializeInternal(byte[] serialized) {
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(bais);
			return (T) objectInputStream.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private byte[] toByteArray(InputStream inputStream) {
		try {
			return ByteStreams.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	public SerializationObject deserializeModelData(InputStream inputStream) {
		return deserializeModelData(toByteArray(inputStream));
	}

	@Override
	public SerializationObject deserializeModelData(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public VariableMetaData deserializeMetaData(InputStream inputStream) {
		return deserializeMetaData(toByteArray(inputStream));
	}

	@Override
	public VariableMetaData deserializeMetaData(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public LapisNode deserializeLapisNode(InputStream inputStream) {
		return deserializeLapisNode(toByteArray(inputStream));
	}

	@Override
	public LapisNode deserializeLapisNode(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public List<LapisNode> deserializeNetworkData(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public List<LapisNode> deserializeNetworkData(InputStream inputStream) {
		return deserializeNetworkData(toByteArray(inputStream));
	}

	@Override
	public byte[] serialize(List<VariableMetaData> variableMetaDataList) {
		return serializeInternal(new ArrayList<>(variableMetaDataList));
	}

	@Override
	public byte[] serialize(LapisNode lapisNode) {
		return serializeInternal(lapisNode);
	}

	@Override
	public List<VariableMetaData> deserializeMetaDataList(byte[] serialized) {
		return deserializeInternal(serialized);
	}

	@Override
	public List<VariableMetaData> deserializeMetaDataList(InputStream inputStream) {
		return deserializeMetaDataList(toByteArray(inputStream));
	}
}
