package edu.osu.lapis.serialize;

import java.io.Serializable;

import edu.osu.lapis.data.LapisDataType;

@SuppressWarnings("serial")
public class LapisDatum implements Serializable{
	private String name;
	private LapisDataType type;
	private int[] dimension;
	private Object data;

	public LapisDatum() {
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
}