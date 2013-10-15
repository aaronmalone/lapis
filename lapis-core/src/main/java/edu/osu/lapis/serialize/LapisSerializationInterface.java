package edu.osu.lapis.serialize;

import java.io.InputStream;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public interface LapisSerializationInterface {
	//serialize
	public byte[] serialize(LapisDatum lapisDatum);
	public byte[] serialize(VariableMetaData variableMetaData);
	
	//deserialize
	public LapisDatum deserializeLapisDatum(byte[] serialized);
	public LapisDatum deserializeLapisDatum(InputStream inputStream);
	public VariableMetaData deserializeVariableMetaData(byte[] serialized);
	public VariableMetaData deserializeVariableMetaData(InputStream inputStream);
	public LapisNode deserializeNetworkMessage(String serialized);
}
