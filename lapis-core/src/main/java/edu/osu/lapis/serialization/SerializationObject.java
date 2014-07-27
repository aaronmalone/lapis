package edu.osu.lapis.serialization;

import java.io.Serializable;

@SuppressWarnings("serial")

/**
 * Class used for serializing and deserializing LAPIS data that is moved around the network. 
 */
public class SerializationObject implements Serializable {

	public static final String
			NAME = "name",
			DATA = "data",
			ORIGINAL_TYPE = "originalType";

	private final String name;
	private final Class<?> originalType;
	private final Object data;

	public SerializationObject(String name, Object data) {
		this(name, data.getClass(), data);
	}

	public SerializationObject(String name, Class<?> originalType, Object data) {
		this.name = name;
		this.originalType = originalType;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public Object getData() {
		return data;
	}

	public Class<?> getOriginalType() {
		return originalType;
	}
}
