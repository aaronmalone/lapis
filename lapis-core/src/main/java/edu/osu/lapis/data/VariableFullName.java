package edu.osu.lapis.data;

import org.apache.commons.lang3.Validate;

public class VariableFullName implements Comparable<VariableFullName> {
	private final String localName;
	private final String modelName;
	private final int hash;
	
	public VariableFullName(String fullName) {
		Validate.isTrue(fullName.contains("@"), "Full name must contain '@' character.");
		String localName = fullName.substring(0, fullName.indexOf('@')).trim();
		String modelName = fullName.substring(fullName.indexOf('@')+1).trim();
		Validate.notEmpty(localName, "Must have local name in full name before '@' character.");
		Validate.notEmpty(modelName, "Must have model name in full name after '@' character.");
		this.localName = localName;
		this.modelName = modelName;
		this.hash = (localName + '@' + modelName).hashCode();
	}
	
	public VariableFullName(String localName, String modelName) {
		Validate.isTrue(!(localName + modelName).contains("@"), "Local and model names cannot contain '@' character.");
		Validate.notEmpty(localName.trim(), "Must have local name in full name.");
		Validate.notEmpty(modelName.trim(), "Must have model name in full name.");
		this.localName = localName;
		this.modelName = modelName;
		this.hash = (localName + '@' + modelName).hashCode();
	}

	public String getLocalName() {
		return localName;
	}

	public String getModelName() {
		return modelName;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public String toString() {
		return localName + '@' + modelName;
	}

	public int compareTo(VariableFullName arg) {
		int value = this.localName.compareTo(arg.localName);
		if(value == 0) {
			value = this.modelName.compareTo(arg.modelName);
		}
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof VariableFullName) {
			VariableFullName other = (VariableFullName) obj;
			return this.localName.equals(other.localName)
					&& this.modelName.equals(other.modelName);
		}
		return false;
	}
}
