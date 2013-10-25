package edu.osu.lapis.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.io.ByteStreams;
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
	public byte[] serialize(SerializationObject serializationObject) {
		return getGson().toJson(serializationObject).getBytes();
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return getGson().toJson(variableMetaData).getBytes();
	}
	
	@Override
	public SerializationObject deserializeModelData(InputStream inputStream) {
		try {
			return deserializeModelData(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public SerializationObject deserializeModelData(byte[] serialized) {
		SerializationObject ld = new SerializationObject();
		String string = new String(serialized);
		JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
		ld.setName(gson.fromJson(jsonObject.get(NAME), String.class));
		ld.setType(gson.fromJson(jsonObject.get(TYPE), LapisDataType.class));
		ld.setDimension(gson.fromJson(jsonObject.get(DIMENSION), int[].class));
		Class<?> dataType = LapisDataType.getClassForType(ld.getType());
		ld.setData(gson.fromJson(jsonObject.get(DATA), dataType));
		return ld;
	}
	
	@Override
	public VariableMetaData deserializeMetaData(InputStream inputStream) {
		try {
			return deserializeMetaData(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public VariableMetaData deserializeMetaData(byte[] serialized) {
		return gson.fromJson(new String(serialized), VariableMetaData.class);
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
		return gson.fromJson(new String(serialized), LapisNode.class);
	}

	@Override
	public List<LapisNode> deserializeNetworkData(byte[] serialized) {
		Type type = new TypeToken<List<LapisNode>>(){}.getType();
		return gson.fromJson(new String(serialized), type);
	}

	@Override
	public List<LapisNode> deserializeNetworkData(InputStream inputStream) {
		try {
			return deserializeNetworkData(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	public List<VariableMetaData> deserializeMetaDataList(byte[] serialized) {
		return gson.fromJson(new String(serialized), new TypeToken<List<VariableMetaData>>() {}.getType());
	}

	@Override
	public List<VariableMetaData> deserializeMetaDataList(InputStream inputStream) {
		try {
			return deserializeMetaDataList(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] serialize(LapisNode[] lapisNodes) {
		// TODO Auto-generated method stub
		return null;
	}
}