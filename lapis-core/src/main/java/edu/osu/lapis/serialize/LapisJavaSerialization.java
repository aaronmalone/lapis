package edu.osu.lapis.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public class LapisJavaSerialization implements LapisSerializationInterface {

	//TODO RE-ORDER MEMBERS	
	
	@Override
	public byte[] serialize(LapisDatum lapisDatum) {
		return serializeInternal(lapisDatum);
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return serializeInternal(variableMetaData);
	}
	
	private byte[] serializeInternal(Serializable serializable) {
		//TODO LOOK AT TIGHTENING THIS UP
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(baos);
			objectOut.writeObject(serializable);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	@Override
	public LapisDatum deserializeLapisDatum(byte[] serialized) {
		return deserializeInternal(serialized);
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
	public LapisNode deserializeNetworkMessage(String serialized) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LapisDatum deserializeLapisDatum(InputStream inputStream) {
		return deserializeLapisDatum(LapisUtils.toByteArray(inputStream));
	}

	@Override
	public VariableMetaData deserializeVariableMetaData(byte[] serialized) {
		return deserializeInternal(serialized);
	}

	@Override
	public VariableMetaData deserializeVariableMetaData(InputStream inputStream) {
		return deserializeVariableMetaData(LapisUtils.toByteArray(inputStream));
	}
}
