package edu.osu.lapis.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import edu.osu.lapis.Logger;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonSerialization implements LapisSerialization {

	private static final Logger logger = Logger.getLogger(JsonSerialization.class);

	private static final TypeAdapterFactory classTypeAdapterFactory = new TypeAdapterFactory() {

		final TypeAdapter<Class<?>> typeAdapter = new TypeAdapter<Class<?>>() {
			@Override
			public void write(JsonWriter out, Class<?> value) throws IOException {
				out.value(value.getName());
			}

			@Override
			public Class<?> read(JsonReader in) throws IOException {
				return null;
			}
		};

		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (type.getRawType().equals(Class.class)) {
				return (TypeAdapter<T>) typeAdapter;
			} else
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
		if (this.prettyPrinting) builder.setPrettyPrinting();
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
		if (this.prettyPrinting != prettyPrinting) {
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
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			return getGson().toJson(serializationObject).getBytes();
		} finally {
			sw.stop();
			logger.trace("Serialization of '%s' took %d milliseconds.",
					serializationObject.getName(), sw.getTime());
		}
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
		JsonObject jsonObject = getGson().fromJson(reader, JsonObject.class);
		String name = getSerializationObjectName(jsonObject);
		String originalTypeString = getSerializationObjectTypeString(jsonObject);
		Class<?> originalType = getSerializationObjectType(originalTypeString);
		Object data = getSerializationObjectData(jsonObject, originalType);
		return new SerializationObject(name, originalType, data);
	}

	private String getSerializationObjectName(JsonObject jsonObject) {
		return jsonObject.get(SerializationObject.NAME).getAsString();
	}

	private String getSerializationObjectTypeString(JsonObject jsonObject) {
		return jsonObject.get(SerializationObject.ORIGINAL_TYPE).getAsString();
	}

	private Class<?> getSerializationObjectType(String typeString) {
		try {
			return Class.forName(typeString);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to get class for \"" + typeString + "\"", e);
		}
	}

	private Object getSerializationObjectData(JsonObject jsonObject, Class<?> type) {
		JsonElement jsonData = jsonObject.get(SerializationObject.DATA);
		if ("java.util.HashMap".equals(type.getName())) {
			return JsonMapDeserializer.deserializeMap(jsonData.getAsJsonObject());
		} else {
			return getGson().fromJson(jsonData, type);
		}
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
		Type type = new TypeToken<List<LapisNode>>() {
		}.getType();
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
		Type type = new TypeToken<List<VariableMetaData>>() {
		}.getType();
		return getGson().fromJson(reader, type);
	}
}
