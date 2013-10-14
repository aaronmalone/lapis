package edu.osu.lapis.data;

import java.io.Serializable;


@SuppressWarnings("serial")
public class VariableMetaData implements Serializable {
	private LapisDataType type;
	private int[] dimension;
	private LapisPermission lapisPermission = LapisPermission.READ_WRITE;
	
	public LapisDataType getType() {
		return type;
	}
	public void setType(LapisDataType type) {
		this.type = type;
	}
	public LapisPermission getLapisPermission() {
		return lapisPermission;
	}
	public void setLapisPermission(LapisPermission lapisPermission) {
		this.lapisPermission = lapisPermission;
	}
	public int[] getDimension() {
		return dimension;
	}
	public void setDimension(int[] dimension) {
		this.dimension = dimension;
	}
}