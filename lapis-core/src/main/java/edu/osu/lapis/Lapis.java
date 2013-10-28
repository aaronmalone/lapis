package edu.osu.lapis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import edu.osu.lapis.communicator.rest.RestletServer;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;

public class Lapis {
	
	private LocalDataTable localDataTable;
	private LapisDataClient lapisDataClient;
	
	public Lapis() {
		System.out.println("ZERO"); //TODO REMOVE
		ApplicationContext context = new AnnotationConfigApplicationContext(LapisConfiguration.class);
		System.out.println("ONE"); //TODO REMOVE
		localDataTable = context.getBean(LocalDataTable.class);
		System.out.println("TWO"); //TODO REMOVE
		lapisDataClient = context.getBean(LapisDataClient.class);
		System.out.println("THREE"); //TODO REMOVE
		RestletServer restletServer = context.getBean(RestletServer.class);
		System.out.println("FOUR"); //TODO REMOVE
		restletServer.initialize();
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
		LapisVariable meta = createLapisVariable(localName, reference, lapisPermission, isReady);
		System.out.println("localDataTable = " + localDataTable); //TODO REMOVE
		localDataTable.put(localName, meta);
	}

	private LapisVariable createLapisVariable(String name, Object reference, LapisPermission lapisPermission, boolean isReady) {
		LapisVariable meta = new LapisVariable(name, reference);
		meta.getVariableMetaData().setLapisPermission(lapisPermission);
		meta.setReady(isReady);
		return meta;
	}

	//all the get methods
	public int getInt(String fullName) {
		return (int) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.INTEGER);
	}

	public long getLong(String fullName) {
		return (long) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.LONG);
	}

	public double getDouble(String fullName) {
		return (double) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.DOUBLE);
	}

	public byte getByte(String fullName) {
		return (byte) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.BYTE);
	}

	public boolean getBoolean(String fullName) {
		return (boolean) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.BOOLEAN);
	}

	public int[] getArrayOfInt(String fullName) {
		return (int[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[] getArrayOfLong(String fullName) {
		return (long[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[] getArrayOfDouble(String fullName) {
		return (double[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[] getArrayOfByte(String fullName) {
		return (byte[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return (boolean[]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return (int[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return (long[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return (double[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return (byte[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return (int[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_INTEGER);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return (long[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_LONG);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return (double[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_DOUBLE);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		return (byte[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BYTE);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		return (boolean[][][]) lapisDataClient.getRemoteVariableValue(fullName, LapisDataType.THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN);
	}
}
