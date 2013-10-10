package edu.osu.lapis.data;


public class VariableMetaData {
	private LapisDataType lapisDataType;
	private int[] dimension;
	private LapisPermission lapisPermission = LapisPermission.READ_WRITE;
	
	public LapisDataType getLapisDataType() {
		return lapisDataType;
	}
	public void setLapisDataType(LapisDataType lapisDataType) {
		this.lapisDataType = lapisDataType;
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