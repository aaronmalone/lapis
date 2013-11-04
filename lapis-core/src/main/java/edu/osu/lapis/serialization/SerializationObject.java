package edu.osu.lapis.serialization;

import java.io.Serializable;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;

@SuppressWarnings("serial")

/**
 * Class used for serializing and deserializing LAPIS data that is moved around the network. 
 */
public class SerializationObject implements Serializable{
	
	private String name;
	private LapisDataType type;
	private int[] dimension;
	private Object data;

	public SerializationObject() {
		//no-args constructor
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LapisDataType getType() {
		return type;
	}

	public void setType(LapisDataType type) {
		this.type = type;
	}

	public int[] getDimension() {
		return dimension;
	}

	public void setDimension(int[] dimension) {
		this.dimension = dimension;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public LapisVariable toLapisVariable() {
		//TODO SHOULD I USE OTHER CONSTRUCTOR?
		return new LapisVariable(name,data);
	}
}