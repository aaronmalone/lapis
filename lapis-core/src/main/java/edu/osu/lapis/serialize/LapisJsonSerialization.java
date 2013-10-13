package edu.osu.lapis.serialize;

import java.util.EnumMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.network.LapisNode;

public class LapisJsonSerialization implements LapisSerialization {

	//TODO organize members
	
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

	private static final String
		NAME = "name",
		TYPE = "type",
		DIMENSION = "dimension",
		DATA = "data";
	
	private static final EnumMap<LapisDataType, Class<?>> typeMap
		= new EnumMap<LapisDataType, Class<?>>(LapisDataType.class);
	
	static {
		typeMap.put(LapisDataType.INTEGER, Integer.class);
		typeMap.put(LapisDataType.LONG, Long.class);
		typeMap.put(LapisDataType.BOOLEAN, Boolean.class);
		typeMap.put(LapisDataType.BYTE, Byte.class);
		typeMap.put(LapisDataType.DOUBLE, Double.class);
		typeMap.put(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER, int[].class);
		typeMap.put(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG, long[].class);
		typeMap.put(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE, double[].class);
		typeMap.put(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN, boolean[].class);
		typeMap.put(LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE, byte[].class);
		typeMap.put(LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER, int[][].class);
	}

	public byte[] serialize(LapisDatum lapisDatum) {
		return getGson().toJson(lapisDatum).getBytes();
	}
	
	
	public String serialize(LapisNode lapisNode) {

		return getGson().toJson(lapisNode);		
	}
	
	

	public LapisDatum deserialize(byte[] serialized) {
		String string = new String(serialized);
		JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
		LapisDatum ld = new LapisDatum();
		ld.setName(gson.fromJson(jsonObject.get(NAME), String.class));
		ld.setType(gson.fromJson(jsonObject.get(TYPE), LapisDataType.class));
		ld.setDimension(gson.fromJson(jsonObject.get(DIMENSION), int[].class));
		ld.setData(gson.fromJson(jsonObject.get(DATA), typeMap.get(ld.getType())));
		return ld;
	}
}
