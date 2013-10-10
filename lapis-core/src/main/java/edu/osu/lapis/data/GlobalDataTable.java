package edu.osu.lapis.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.osu.lapis.network.NetworkTable;

public class GlobalDataTable implements GlobalDataInterface {
	
	private final Map<VariableFullName, VariableMetaData> globalDataMap = 
			Collections.synchronizedMap(new HashMap<VariableFullName, VariableMetaData>());
	private NetworkTable networkTable; //TODO SET
	
	public void put(String fullName, VariableMetaData variableMetaData) {
		VariableFullName varName = new VariableFullName(fullName);
		put(varName, variableMetaData);
	}
	
	public void put(VariableFullName varName, VariableMetaData variableMetaData) {
		globalDataMap.put(varName, variableMetaData);
	}
	
	public void remove(String fullName) {
		remove(new VariableFullName(fullName));
	}
	
	public void remove(VariableFullName varName) {
		globalDataMap.remove(varName);
	}

	public int getInt(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getByte(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getBoolean(String fullName) {
		// TODO Auto-generated method stub
		return false;
	}

	public int[] getArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}
}
