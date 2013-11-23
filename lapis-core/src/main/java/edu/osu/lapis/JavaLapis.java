package edu.osu.lapis;

import java.util.Properties;

import org.apache.commons.lang3.Validate;

import com.google.common.util.concurrent.Callables;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisSettable;
import edu.osu.lapis.data.LapisVariable2;

public class JavaLapis {
	
	static {
		LapisLogging.init();
	}
	
	private final LapisCoreApi lapisCoreApi;

	public JavaLapis(String propertiesFileName) {
		this.lapisCoreApi = new LapisCoreApi(propertiesFileName);
	}
	
	public JavaLapis(Properties properties) {
		this.lapisCoreApi = new LapisCoreApi(properties);
	}

	/**
	 * Publish a LAPIS variable. This exposes the variable to the LAPIS 
	 * network, allowing it to be retrieved or set by other nodes.
	 * @param variableName the name of the variable (other nodes will 
	 * access this variable using ${variableName}@${nodeName}
	 * @param reference the object to publish
	 */
	public void publish(String variableName, Object reference) {
		LapisVariable2 lapisVariable = createLapisVariable(variableName, reference);
		lapisCoreApi.publish(variableName, lapisVariable);
	}

	private LapisVariable2 createLapisVariable(String name, Object reference) {
		LapisDataType type = LapisDataType.getTypeForObject(reference);
		Validate.notNull(type, "Type cannot be null."); //TODO IMPROVE ERROR MESSAGE
		return new LapisVariable2(name, type, Dimensions.getDimensions(reference), 
				Callables.returning(reference), new LapisSettable(reference));
	}
	
	//all the get methods
	public double getDouble(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, Double.TYPE);
	}
	
	public int[] getArrayOfInt(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, int[].class);
	}

	public long[] getArrayOfLong(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, long[].class);
	}

	public double[] getArrayOfDouble(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, double[].class);
	}

	public byte[] getArrayOfByte(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, byte[].class);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, boolean[].class);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, int[][].class);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, long[][].class);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, double[][].class);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, byte[][].class);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, boolean[][].class);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, int[][][].class);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, long[][][].class);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return lapisCoreApi.getRemoteValue(fullName, double[][][].class);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String variableFullName) {
		return lapisCoreApi.getRemoteValue(variableFullName, byte[][][].class);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String variableFullName) {
		return lapisCoreApi.getRemoteValue(variableFullName, boolean[][][].class);
	}
	
	/**
	 * Set the value of the remote variable. The type and dimensions of the 
	 * new value must match that of the remote variable, or an exception will be thrown. 
	 * @param variableFullName the full name of the variable ${variableName}@${nodeName}
	 * @param value the new value
	 */
	public void set(String variableFullName, Object value) {
		lapisCoreApi.setRemoteValue(variableFullName, value);
	}
	
//   TODO figure out if we really need this.
//	/**
//	 * Update the value of a published LAPIS variable.
//	 * 
//	 * Essentially, this replaces LAPIS's own copy of the value with the new value.
//	 * This is necessary when the type of the published variable is a Java primitive
//	 * or a Java primitive wrapper (e.g. int or java.lang.Integer). It is not necessary
//	 * to use this method when values within an array have been modified. Changes within
//	 * an are visible to LAPIS. 
//	 * @param localName the name of the published variable
//	 * @param value the new value of the variable
//	 */
//	public void updateValue(String localName, Object value) {
//		LapisVariable2 localVariable = localDataTable.get(localName);
//		Validate.notNull(localVariable, "The variable \"%s\" has not been published.", localName);
//		localDataTable.put(localName, createLapisVariable(localName, value, LapisPermission.READ_WRITE, true));
//	}
}
