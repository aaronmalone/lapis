package edu.osu.lapis.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.common.io.ByteStreams;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public class LapisJavaSerialization implements LapisSerializationInterface {

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
	
	@Override
	public byte[] serialize(LapisDatum lapisDatum) {
		return serializeInternal(lapisDatum);
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return serializeInternal(variableMetaData);
	}
	
	@Override
	public LapisDatum deserializeLapisDatum(InputStream inputStream) {
		try {
			return deserializeLapisDatum(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LapisDatum deserializeLapisDatum(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public VariableMetaData deserializeVariableMetaData(InputStream inputStream) {
		try {
			return deserializeVariableMetaData(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public VariableMetaData deserializeVariableMetaData(byte[] serialized) {
		return deserializeInternal(serialized);
	}
	
	@Override
	public LapisNode deserializeLapisNode(InputStream inputStream) {
		try {
			return deserializeLapisNode(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LapisNode deserializeLapisNode(byte[] serialized) {
		return deserializeInternal(serialized);
	}
}
