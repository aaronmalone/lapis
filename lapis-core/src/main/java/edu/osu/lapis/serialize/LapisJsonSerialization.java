package edu.osu.lapis.serialize;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

public class LapisJsonSerialization implements LapisSerializationInterface {
	
	//TODO RE-ORDER MEMBERS
	
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
	public byte[] serialize(LapisDatum lapisDatum) {
		return getGson().toJson(lapisDatum).getBytes();
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return getGson().toJson(variableMetaData).getBytes();
	}

	public String serialize(LapisNode lapisNode) {
		return getGson().toJson(lapisNode);		
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
		LapisDatum ld = new LapisDatum();
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
	public VariableMetaData deserializeVariableMetaData(InputStream inputStream) {
		try {
			return deserializeVariableMetaData(ByteStreams.toByteArray(inputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public VariableMetaData deserializeVariableMetaData(byte[] serialized) {
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
}