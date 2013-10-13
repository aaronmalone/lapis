package edu.osu.lapis;

import edu.osu.lapis.communicator.RESTCommunicatorLayer;
import edu.osu.lapis.data.GlobalDataInterface;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.LocalVariableMetaData;
import edu.osu.lapis.network.NetworkTable;

//TODO FINISH IMPLEMENTATION

public class Lapis implements GlobalDataInterface {
	
	private static GlobalDataTable globalDataTable = new GlobalDataTable();
	private static LocalDataTable localDataTable = new LocalDataTable() ;
	private static NetworkTable networkTable = new NetworkTable();
	
	//TODO: Implement overrides for initialize that handles config file loading
	public void initialize(String modelName, String modelAddress, String coordinatorAddress) {
		
		//TODO: Redo this and make it so it actually fits in with our architecture
		
		RESTCommunicatorLayer com = new RESTCommunicatorLayer();
		
		//These are just here for completeness
		//LocalDataTable ldt = new LocalDataTable();
		//GlobalDataTable gdt = new GlobalDataTable();
		
		
		// Initializes and starts the com with just the network route (for now)
		com.initialize(localDataTable, globalDataTable, networkTable, modelName, modelAddress);
		
		
		
	}
	
	public void publish(String localName, Object reference) {
		publish(localName, reference, LapisPermission.READ_WRITE, true);
	}
	
	public void publish(String localName, Object reference, LapisPermission lapisPermission) {
		publish(localName, reference, lapisPermission, true);
	}
	
	public void publish(String localName, Object reference, boolean isReady) {
		publish(localName, reference, LapisPermission.READ_WRITE, isReady);
	}
	
	public void publish(String localName, Object reference, LapisPermission lapisPermission, boolean isReady) {
		LocalVariableMetaData meta = getLocalVariableMetaData(reference, lapisPermission, isReady);
		localDataTable.put(localName, meta);
	}

	private LocalVariableMetaData getLocalVariableMetaData(Object reference, LapisPermission lapisPermission, boolean isReady) {
		LocalVariableMetaData meta = new LocalVariableMetaData(reference);
		meta.setLapisPermission(lapisPermission);
		meta.setReady(isReady);
		return meta;
	}

	//all the get methods
	public int getInt(String fullName) {
		return globalDataTable.getInt(fullName);
	}

	public long getLong(String fullName) {
		return globalDataTable.getLong(fullName);
	}

	public double getDouble(String fullName) {
		return globalDataTable.getDouble(fullName);
	}

	public byte getByte(String fullName) {
		return globalDataTable.getByte(fullName);
	}

	public boolean getBoolean(String fullName) {
		return globalDataTable.getBoolean(fullName);
	}

	public int[] getArrayOfInt(String fullName) {
		return globalDataTable.getArrayOfInt(fullName);
	}

	public long[] getArrayOfLong(String fullName) {
		return globalDataTable.getArrayOfLong(fullName);
	}

	public double[] getArrayOfDouble(String fullName) {
		return globalDataTable.getArrayOfDouble(fullName);
	}

	public byte[] getArrayOfByte(String fullName) {
		return globalDataTable.getArrayOfByte(fullName);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return globalDataTable.getArrayOfBoolean(fullName);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return globalDataTable.getTwoDimensionalArrayOfInt(fullName);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return globalDataTable.getTwoDimensionalArrayOfLong(fullName);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return globalDataTable.getTwoDimensionalArrayOfDouble(fullName);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return globalDataTable.getTwoDimensionalArrayOfByte(fullName);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return globalDataTable.getTwoDimensionalArrayOfBoolean(fullName);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return globalDataTable.getThreeDimensionalArrayOfInt(fullName);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return globalDataTable.getThreeDimensionalArrayOfLong(fullName);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return globalDataTable.getThreeDimensionalArrayOfDouble(fullName);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		return globalDataTable.getThreeDimensionalArrayOfByte(fullName);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		return globalDataTable.getThreeDimensionalArrayOfBoolean(fullName);
	}	
}
