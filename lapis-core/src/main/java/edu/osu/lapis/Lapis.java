package edu.osu.lapis;

import edu.osu.lapis.communicator.CommunicationLayerInterface;
import edu.osu.lapis.communicator.RESTCommunicatorLayer;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.LocalVariableMetaData;
import edu.osu.lapis.network.NetworkTable;

//TODO FINISH IMPLEMENTATION

public class Lapis {
	
	private LapisClient lapisClient; //TODO SET
	
	private GlobalDataTable globalDataTable = new GlobalDataTable();
	private LocalDataTable localDataTable = new LocalDataTable() ;
	private NetworkTable networkTable = new NetworkTable();
	
	
	//TODO: Implement overrides for initialize that handles config file loading
	public void initialize(String modelName, String modelAddress, String coordinatorAddress) {
		
		//TODO: Redo this and make it so it actually fits in with our architecture
		
		CommunicationLayerInterface com = new RESTCommunicatorLayer();
		
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
		return (int) lapisClient.getRemoteVariableValue(fullName, LapisDataType.INTEGER);
	}

	public long getLong(String fullName) {
		return (long) lapisClient.getRemoteVariableValue(fullName, LapisDataType.LONG);
	}

	public double getDouble(String fullName) {
		return (double) lapisClient.getRemoteVariableValue(fullName, LapisDataType.DOUBLE);
	}

	public byte getByte(String fullName) {
		return (byte) lapisClient.getRemoteVariableValue(fullName, LapisDataType.BYTE);
	}

	public boolean getBoolean(String fullName) {
		return (boolean) lapisClient.getRemoteVariableValue(fullName, LapisDataType.BOOLEAN);
	}

	public int[] getArrayOfInt(String fullName) {
		return (int[]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[] getArrayOfLong(String fullName) {
		return (long[]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[] getArrayOfDouble(String fullName) {
		return (double[]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[] getArrayOfByte(String fullName) {
		return (byte[]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return (boolean[]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return (int[][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return (long[][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return (double[][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return (byte[][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return (int[][][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return (long[][][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return (double[][][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		return (byte[][][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][][]) lapisClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}
}
