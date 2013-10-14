package edu.osu.lapis.serialize;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public interface LapisSerializationInterface {
	//serialize
	public byte[] serialize(LapisDatum lapisDatum);
	public byte[] serialize(VariableMetaData variableMetaData);
	
	//deserialize
	public LapisDatum deserializeLapisDatum(byte[] serialized);
	public VariableMetaData deserializeVariableMetaData(String serialized);
	public LapisNode deserializeNetworkMessage(String serialized);
}
