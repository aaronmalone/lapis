package edu.osu.lapis.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;

//TODO REMOVE PRINT STATEMENTS
public class JsonSerialization implements LapisSerialization {
	
	private static final String
		NAME = "name",
		DATA = "data",
		ORIGINAL_TYPE = "originalType";
	
	private static final TypeAdapterFactory classTypeAdapterFactory = new TypeAdapterFactory() {
		
		final TypeAdapter<Class<?>> typeAdapter = new TypeAdapter<Class<?>>() {
			@Override
			public void write(JsonWriter out, Class<?> value) throws IOException {
				System.out.println("writing out class name..."); //TODO REMOVE
				out.value(value.getName());
			}
			@Override
			public Class<?> read(JsonReader in) throws IOException {
				System.out.println("Why am I reading in a class?"); //TODO REMOVE
				return null;
			}
		};
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			System.out.print("type is " + type.getRawType()); //TODO REMOVE
			if(type.getRawType().equals(Class.class)) {
				System.out.println(" returning adapter"); //TODO REMOVE
				return (TypeAdapter<T>) typeAdapter;
			} else
				System.out.println(" returning null."); //TODO REMOVE
				return null;
		}
	};
	
	private Gson gson;
	private boolean prettyPrinting = false;
	
	
	public JsonSerialization() {
		setGson(newGson());
	}
	
	private Gson newGson() {
		GsonBuilder builder = new GsonBuilder();
		if(this.prettyPrinting) builder.setPrettyPrinting();
		builder.registerTypeAdapterFactory(classTypeAdapterFactory);
		return builder.create();
	}
	
	private synchronized void setGson(Gson gson) {
		this.gson = gson;
	}
	
	private synchronized Gson getGson() {
		return gson;
	}

	public synchronized boolean isPrettyPrinting() {
		return prettyPrinting;
	}
	
	public synchronized void setPrettyPrinting(boolean prettyPrinting) {
		if(this.prettyPrinting != prettyPrinting) {
			this.prettyPrinting = prettyPrinting;
			this.setGson(newGson());
		}
	}
	
	@Override
	public byte[] serialize(List<VariableMetaData> variableMetaDataList) {
		return getGson().toJson(variableMetaDataList).getBytes();
	}

	@Override
	public byte[] serialize(LapisNode lapisNode) {
		return getGson().toJson(lapisNode).getBytes();
	}
	
	@Override
	public byte[] serialize(LapisNode[] lapisNodes) {
		return getGson().toJson(lapisNodes).getBytes();
	}
	
	@Override
	public byte[] serialize(SerializationObject serializationObject) {
		System.out.println("serialize(SerializationObject): " + serializationObject); //TODO REMOVE
		return getGson().toJson(serializationObject).getBytes();
	}
	
	@Override
	public byte[] serialize(VariableMetaData variableMetaData) {
		return getGson().toJson(variableMetaData).getBytes();
	}	
	
	@Override
	public SerializationObject deserializeModelData(byte[] serialized) {
		System.out.println("deserializeModelData: serialized: " + new String(serialized)); //TODO REMOVE
		return deserializeModelData(new ByteArrayInputStream(serialized));
	}

	@Override
	public SerializationObject deserializeModelData(InputStream inputStream) {
		return deserializeModelData(new InputStreamReader(inputStream));
	}

	private SerializationObject deserializeModelData(Reader reader) {
		JsonObject jsonObject = getGson().fromJson(reader, JsonObject.class);
		String name = jsonObject.get(NAME).getAsString();
		String originalTypeString = jsonObject.get(ORIGINAL_TYPE).getAsString();
		Class<?> originalType = null;
		try {
			originalType = Class.forName(originalTypeString);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to get class for \"" + originalTypeString + "\"", e);
		}
		JsonElement jsonData = jsonObject.get(DATA);
		Object data = getGson().fromJson(jsonData, originalType);
		return new SerializationObject(name, originalType, data);
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
		return getGson().fromJson(reader, VariableMetaData.class);
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
		return getGson().fromJson(reader, LapisNode.class);
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
		return getGson().fromJson(reader, type);
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
		return getGson().fromJson(reader, type);
	}
}