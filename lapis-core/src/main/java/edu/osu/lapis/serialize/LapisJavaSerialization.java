package edu.osu.lapis.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.osu.lapis.network.LapisNode;

public class LapisJavaSerialization implements LapisSerializationInterface {

	public byte[] serialize(LapisDatum lapisDatum) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(baos);
			objectOut.writeObject(lapisDatum);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	public LapisDatum deserialize(byte[] serialized) {
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(bais);
			return (LapisDatum) objectInputStream.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LapisNode deserializeNetworkMessage(String serialized) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//TODO look at objectInput,W objectOutput
	
}
