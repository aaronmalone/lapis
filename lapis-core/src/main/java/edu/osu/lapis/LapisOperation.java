package edu.osu.lapis;

import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

public class LapisOperation {
	private final UUID uuid = UUID.randomUUID();
	private LapisOperationType operationType;
	private String variableName;
	private Object data;
	private int hashcode = Integer.MIN_VALUE;
	
	public LapisOperation() {
		//default
	}
	
	public LapisOperation(LapisOperationType type, String variableName, Object data) {
		this.operationType = type;
		this.variableName = variableName;
		this.data = data;
	}
	
	public LapisOperation(String variableName) {
		this(LapisOperationType.GET, variableName, null);
	}
	
	public LapisOperation(String variableName, Object data) {
		this(LapisOperationType.SET, variableName, data);
	}
	
	public LapisOperationType getOperationType() {
		return operationType;
	}
	public void setOperationType(LapisOperationType operationType) {
		this.operationType = operationType;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public int hashCode() {
		if(hashcode == Integer.MIN_VALUE) {
			hashcode = (uuid.toString() + operationType + variableName).hashCode();
		}
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LapisOperation) {
			LapisOperation that = (LapisOperation) obj;
			return this.uuid.equals(that.uuid)
					&& this.operationType == that.operationType
					&& this.variableName.equals(that.variableName);
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return operationType + " '" + variableName + "'"
				+ (operationType == LapisOperationType.SET ? ": " + dataToString(data) : "");
	}
	
	private String dataToString(Object obj) {
		if(obj == null) {
			return "null";
		} else {
			Class<?> cls = obj.getClass();
			if(cls.isArray()) {
				return ArrayUtils.toString(obj);
			} else {
				return cls.toString() + ": " + obj.toString();
			}
		}
	}
}
