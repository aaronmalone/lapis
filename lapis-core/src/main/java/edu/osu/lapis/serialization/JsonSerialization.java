package edu.osu.lapis.serialization;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public class JsonSerialization implements LapisSerialization {
	
	private static final String
		NAME = "name",
		TYPE = "type",
		DIMENSION = "dimension",
		DATA = "data";
	
	private final JsonParser jsonParser = new JsonParser();
	private Gson gson = new Gson();
	private boolean prettyPrinting = false;
	
	private synchronized Gson getGson() {
		return gson;
	}

	private synchronized void setGson(Gson gson) {
		this.gson = gson;
	}

	public synchronized boolean isPrettyPrinting() {
		return prettyPrinting;
	}

	public synchronized void setPrettyPrinting(boolean prettyPrinting) {
		if(this.prettyPrinting != prettyPrinting) {
			Gson gson = prettyPrinting ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
			this.setGson(gson);
			this.prettyPrinting = prettyPrinting;
		}
	}
	
	@Override
	public byte[] serialize(List<VariableMetaData> variableMetaDataList) {
		return gson.toJson(variableMetaDataList).getBytes();
	}

	@Override
	public byte[] serialize(LapisNode lapisNode) {
		return gson.toJson(lapisNode).getBytes();
	}
	
	@Override
	public byte[] serialize(LapisNode[] lapisNodes) {
		return gson.toJson(lapisNodes).getBytes();
	}
	
	@Override
	public byte[] serialize(SerializationObject serializationObject) {
		return getGson().toJson(serializationObject).getBytes();
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return getGson().toJson(variableMetaData).getBytes();
	}	
	
	@Override
	public SerializationObject deserializeModelData(byte[] serialized) {
		return deserializeModelData(new ByteArrayInputStream(serialized));
	}

	@Override
	public SerializationObject deserializeModelData(InputStream inputStream) {
		return deserializeModelData(new InputStreamReader(inputStream));
	}
	
	private SerializationObject deserializeModelData(Reader reader) {
		SerializationObject ld = new SerializationObject();
		JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject();
		ld.setName(gson.fromJson(jsonObject.get(NAME), String.class));
		ld.setType(gson.fromJson(jsonObject.get(TYPE), LapisDataType.class));
		ld.setDimension(gson.fromJson(jsonObject.get(DIMENSION), int[].class));
		Class<?> dataType = LapisDataType.getClassForType(ld.getType());
		ld.setData(gson.fromJson(jsonObject.get(DATA), dataType));
		return ld;
	}

	@Override
	public VariableMetaData deserializeMetaData(byte[] serialized) {
		return deserializeMetaData(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public VariableMetaData deserializeMetaData(InputStream inputStream) {
		return deserializeMetaData(new InputStreamReader(inputStream));
	}
	
	private VariableMetaData deserializeMetaData(Reader reader) {
		return gson.fromJson(reader, VariableMetaData.class);
	}

	@Override
	public LapisNode deserializeLapisNode(byte[] serialized) {
		return deserializeLapisNode(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public LapisNode deserializeLapisNode(InputStream inputStream) {
		return deserializeLapisNode(new InputStreamReader(inputStream));
	}
	
	public LapisNode deserializeLapisNode(Reader reader) {
		return gson.fromJson(reader, LapisNode.class);
	}

	@Override
	public List<LapisNode> deserializeNetworkData(byte[] serialized) {
		return deserializeNetworkData(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public List<LapisNode> deserializeNetworkData(InputStream inputStream) {
		return deserializeNetworkData(new InputStreamReader(inputStream));
	}
	
	private List<LapisNode> deserializeNetworkData(Reader reader) {
		Type type = new TypeToken<List<LapisNode>>(){}.getType();
		return gson.fromJson(reader, type);
	}

	@Override
	public List<VariableMetaData> deserializeMetaDataList(byte[] serialized) {
		return deserializeMetaDataList(new ByteArrayInputStream(serialized));
	}
	
	@Override
	public List<VariableMetaData> deserializeMetaDataList(InputStream inputStream) {
		return deserializeMetaDataList(new InputStreamReader(inputStream));
	}
	
	private List<VariableMetaData> deserializeMetaDataList(Reader reader) {
		Type type = new TypeToken<List<VariableMetaData>>(){}.getType();
		return gson.fromJson(reader, type);
	}
}