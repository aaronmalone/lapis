package edu.osu.lapis.serialization;

import java.io.InputStream;
import java.util.List;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public interface LapisSerialization {
	
	//serialize
	public byte[] serialize(SerializationObject serializationObject);
	public byte[] serialize(VariableMetaData variableMetaData);
	public byte[] serialize(List<VariableMetaData> variableMetaDataList);
	public byte[] serialize(LapisNode lapisNode);
	public byte[] serialize(LapisNode[] lapisNodes); //hack b/c of erasure types
	
	//deserialize
	public SerializationObject deserializeModelData(byte[] serialized);
	public SerializationObject deserializeModelData(InputStream inputStream);
	public VariableMetaData deserializeMetaData(byte[] serialized);
	public VariableMetaData deserializeMetaData(InputStream inputStream);
	public LapisNode deserializeLapisNode(byte[] serialized);
	public LapisNode deserializeLapisNode(InputStream inputStream);
	public List<LapisNode> deserializeNetworkData(byte[] serialized);
	public List<LapisNode> deserializeNetworkData(InputStream inputStream);
	public List<VariableMetaData> deserializeMetaDataList(byte[] serialized);
	public List<VariableMetaData> deserializeMetaDataList(InputStream inputStream);
}
