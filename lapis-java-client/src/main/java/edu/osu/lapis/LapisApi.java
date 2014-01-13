package edu.osu.lapis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;

import com.google.common.util.concurrent.Callables;

import edu.osu.lapis.data.LapisSettable;
import edu.osu.lapis.data.LapisVariable;

/**
 * LAPIS API exposed to Java clients.
 * 
 * Note that when this object is used to get and set values of variables 
 * published by other nodes, the "full name" of the variable should be used in 
 * the get or set method. The full name is the name of the published variable on
 * the other node and the name of the other node, joined together with a '@' 
 * character. For example, if a LAPIS node named "NodeX" published a variable 
 * with the name "var1", the full name of that variable would be "var1@NodeX".
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
	
	//TODO ADD COMMENT
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
		Validate.isTrue(reference.getClass().isArray(), "Published variables must be arrays.");
		LapisVariable lapisVariable = createLapisVariable(variableName, reference);
		lapisCore.publish(variableName, lapisVariable);
	}

	private LapisVariable createLapisVariable(String name, Object reference) {
		return new LapisVariable(name, false,
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

	/**
	 * Wait for a node to declare that it is ready. This method blocks indefinitely
	 * until the specified node has joined the network and declared that it is ready.
	 * @param nodeName the name of the node
	 */
	public void waitForReadyNode(String nodeName) {
		lapisCore.waitForReadyNode(nodeName);
	}
	
	/**
	 * Wait for a node to declare that it is ready. This method blocks until the 
	 * specified node has joined the network and declared itself ready, or until
	 * the timeout is reached. If the timeout is reached before the node is ready,
	 * an exception is thrown.
	 * @param nodeName the name of the node to wait for
	 * @param millisToWait the number of milliseconds to wait
	 * @throws TimeoutException if the timeout is reached
	 */
	public void waitForReadyNode(String nodeName, long millisToWait) throws TimeoutException {
		lapisCore.waitForReadyNode(nodeName, millisToWait);
	}
	
	/**
	 * Declare this node 'ready'. Applications do not need to declare themselves
	 * ready in order to use LAPIS functionality, but they can use ready(), notReady(),
	 * and waitForReadyNode() to facilitate coordination among multiple nodes on 
	 * a LAPIS network.
	 */
	public void ready() {
		lapisCore.ready();
	}
	
	/**
	 * Declare this node 'not ready'.
	 */
	public void notReady() {
		lapisCore.notReady();
	}
	
	/**
	 * Get the name of this LAPIS node.
	 */
	public String getName() {
		return this.lapisCore.getName();
	}
	
	/**
	 * Un-publish a variable.
	 * @param variableName the name of the published variable
	 */
	public void redact(String variableName) {
		this.lapisCore.redact(variableName);
	}
}