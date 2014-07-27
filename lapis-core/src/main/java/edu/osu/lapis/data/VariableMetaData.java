package edu.osu.lapis.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VariableMetaData implements Serializable {

	private final String name;
	private final String type;
	private final boolean readOnly;

	public VariableMetaData(String name, String type, boolean readOnly) {
		this.name = name;
		this.type = type;
		this.readOnly = readOnly;
	}

	public VariableMetaData(String name, Class<?> cls, boolean readOnly) {
		this(name, cls.getName(), readOnly);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}
