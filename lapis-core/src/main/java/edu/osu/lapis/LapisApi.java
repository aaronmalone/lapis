package edu.osu.lapis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.google.common.util.concurrent.Callables;

import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisSettable;
import edu.osu.lapis.data.LapisVariable;

/**
 * LAPIS API exposed to Java clients.
 */
public class LapisApi {
	
	private final LapisCore lapisCore;

	/**
	 * Construct using properties in properties file.
	 */
	public LapisApi(String propertiesFileName) {
		this.lapisCore = new LapisCore(propertiesFileName);
	}

	/**
	 * Construct using properties.
	 */
	public LapisApi(Properties properties) {
		this.lapisCore = new LapisCore(properties);
	}
	
	/**
	 * Construct using the specified properties. This resulting LAPIS node will 
	 * be a coordinator node if the coordinatorAddress and myAddress arguments 
	 * are the same.  
	 * @param nodeName the name of this node on the LAPIS network
	 * @param coordinatorAddress the address of the coordinator on the LAPIS network
	 * @param myAddress the address of the current node
	 */
	public LapisApi(String nodeName, String coordinatorAddress, String myAddress) {
		try {
			Properties properties = new Properties();
			properties.setProperty("name", nodeName);
			properties.setProperty("coordinator.url", coordinatorAddress);
			properties.setProperty("port", Integer.toString(new URL(myAddress).getPort()));
			properties.setProperty("isCoordinator", Boolean.toString(coordinatorAddress.equals(myAddress)));
			this.lapisCore = new LapisCore(properties);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public LapisApi(String nodeName, String coordinatorAddress) {
		this(nodeName, coordinatorAddress, coordinatorAddress);
	}

	/**
	 * Publish a LAPIS variable. This exposes the variable to the LAPIS 
	 * network, allowing it to be retrieved or set by other nodes.
	 * @param variableName the name of the variable (other nodes will 
	 * access this variable using ${variableName}@${nodeName}
	 * @param reference the object to publish
	 */
	public void publish(String variableName, Object reference) {
		LapisVariable lapisVariable = createLapisVariable(variableName, reference);
		lapisCore.publish(variableName, lapisVariable);
	}

	private LapisVariable createLapisVariable(String name, Object reference) {
		return new LapisVariable(name, LapisPermission.READ_WRITE,
				Callables.returning(reference), new LapisSettable(reference));
	}
	
	//all the get methods
	public double getDouble(String fullName) {
		return lapisCore.getRemoteValue(fullName, Double.TYPE);
	}
	
	public int[] getArrayOfInt(String fullName) {
		return lapisCore.getRemoteValue(fullName, int[].class);
	}

	public long[] getArrayOfLong(String fullName) {
		return lapisCore.getRemoteValue(fullName, long[].class);
	}

	public double[] getArrayOfDouble(String fullName) {
		return lapisCore.getRemoteValue(fullName, double[].class);
	}

	public byte[] getArrayOfByte(String fullName) {
		return lapisCore.getRemoteValue(fullName, byte[].class);
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		return lapisCore.getRemoteValue(fullName, boolean[].class);
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		return lapisCore.getRemoteValue(fullName, int[][].class);
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		return lapisCore.getRemoteValue(fullName, long[][].class);
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		return lapisCore.getRemoteValue(fullName, double[][].class);
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		return lapisCore.getRemoteValue(fullName, byte[][].class);
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		return lapisCore.getRemoteValue(fullName, boolean[][].class);
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		return lapisCore.getRemoteValue(fullName, int[][][].class);
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		return lapisCore.getRemoteValue(fullName, long[][][].class);
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		return lapisCore.getRemoteValue(fullName, double[][][].class);
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String variableFullName) {
		return lapisCore.getRemoteValue(variableFullName, byte[][][].class);
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String variableFullName) {
		return lapisCore.getRemoteValue(variableFullName, boolean[][][].class);
	}
	
	/**
	 * Set the value of the remote variable. The type and dimensions of the 
	 * new value must match that of the remote variable, or an exception will be thrown. 
	 * @param variableFullName the full name of the variable ${variableName}@${nodeName}
	 * @param value the new value
	 */
	public void set(String variableFullName, Object value) {
		lapisCore.setRemoteValue(variableFullName, value);
	}
}
