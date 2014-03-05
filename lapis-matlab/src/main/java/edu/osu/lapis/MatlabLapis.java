package edu.osu.lapis;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.Settable;

public class MatlabLapis {

	private final LapisCore lapisCoreApi;
	private final MatlabDataCache dataCache = new MatlabDataCache();
	
	/**
	 * Constructor for MatlabLapis instance. This will initialize this LAPIS node as a coordinator.
	 * @param name the name of this LAPIS node
	 * @param thisNodeAddress this node's address (which also happens to be the address of the coordinator)
	 */
	public MatlabLapis(String name, String thisNodeAddress) {
		this(name, thisNodeAddress, thisNodeAddress, Boolean.TRUE.toString());
	}
	
	/**
	 * Constructor for MatlabLapis. This node will not be the coordinator on the LAPIS
	 * network unless the 'coordinatorAddress' and 'myAddress' properties are the same.
	 * @param name the name of this LAPIS node
	 * @param coordinatorAddress the address of the LAPIS network's coordinator
	 * @param myAddress this node's address
	 */
	public MatlabLapis(String name, String coordinatorAddress, String myAddress) {
		this(name, coordinatorAddress, myAddress, 
				Boolean.toString(StringUtils.equals(coordinatorAddress, myAddress)));
	}

	/**
	 * Constructor for a MatlabLapis. This constructor initializes a LAPIS node.
	 * @param name the name of this LAPIS node
	 * @param coordinatorAddress the address of the LAPIS network's coordinator
	 * @param myAddress this node's address
	 * @param isCoordinator "true" if this node should be initialized as a coordinator
	 */
	public MatlabLapis(String name, String coordinatorAddress, String myAddress, String isCoordinator) {
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("coordinator.url", coordinatorAddress);
		properties.setProperty("isCoordinator", isCoordinator.toLowerCase());
		properties.setProperty("localNodeAddress", myAddress);
		lapisCoreApi = new LapisCore(properties);
	}
	
	/**
	 * Publish a variable. LAPIS will cache the value of the variable to serve get and
	 * set requests.
	 * @param publishedVariableName the name of the published variable
	 * @param initialValue the initial value of the variable
	 */
	public void publish(String publishedVariableName, Object initialValue) {
		publishRegularData(publishedVariableName, initialValue, false);
	}
	
	/**
	 * Publish a variable. LAPIS will cache the value of the variable to serve 
	 * get requests. Other nodes will not be able to set the variable.
	 * @param publishedVariableName the name of the published variable
	 * @param initialValue the initial value of the variable
	 */
	public void publishReadOnly(String publishedVariableName, Object initialValue) {
		publishRegularData(publishedVariableName, initialValue, true);
	}
	
	private void publishRegularData(String name, Object initialValue, boolean readOnly) {
		Validate.notNull(initialValue, "Initial value of published variable cannot be null.");
		this.setCachedValue(name, initialValue);
		Settable settable = readOnly ? null : createSettableForMatlabVariable(name);
		LapisVariable lapisVariable = new LapisVariable(name, readOnly, 
				createCallableForMatlabVariable(name), settable);
		lapisCoreApi.publish(name, lapisVariable);
	}
	
	public void publishNewMap(String mapName) {
		publishNewMap(mapName, false);
	}
	
	public void publishNewReadOnlyMap(String mapName) {
		publishNewMap(mapName, true);
	}
	
	private void publishNewMap(final String mapName, boolean readOnly) {
		setCachedValue(mapName, Maps.newHashMap());
		Settable settable = readOnly ? null : createLapisMatlabMapSettable(mapName);
		LapisVariable lapisVariable = new LapisVariable(mapName, readOnly, 
				createCallableForMatlabVariable(mapName), settable);
		lapisCoreApi.publish(mapName, lapisVariable);
	}
	
	private Callable<Object> createCallableForMatlabVariable(final String name) {
		return new Callable<Object>() {
			@Override public Object call() {
				return dataCache.getCachedValue(name);
			}
		};
	}
	
	private Settable createSettableForMatlabVariable(final String name) {
		return new Settable() {
			@Override public void set(Object value) {
				dataCache.setCachedValue(name, value);
			}
		};
	}
	
	private Settable createLapisMatlabMapSettable(final String mapName) {
		return new Settable() {
			@Override public void set(Object mapValue) {
				Validate.isTrue(mapValue instanceof Map, "Tried to set a published Map " 
						+ "object with a value of  %s", mapValue.getClass());
				dataCache.setCachedValue(mapName, mapValue);
			}
		};
	}

	/**
	 * Retrieve the value of a variable published on another LAPIS node in the network.
	 * @param variableFullName the full name of the variable: localName@nodeName
	 * @return the value of the variable on the remote node
	 */
	public Object get(String variableFullName) {
		return lapisCoreApi.getRemoteValue(variableFullName);
	}

	/**
	 * Sets the value of a variable that has been published by another LAPIS node in the network
	 * @param variableFullName the full name of the variable: localName@nodeName
	 * @param value the value to set
	 */
	public void set(String variableFullName, Object value) {
		lapisCoreApi.setRemoteValue(variableFullName, value);
	}
	
	public Object retrieveFromMap(String mapName, String key) {
		return getMap(mapName).get(key);
	}
	
	/**
	 * Retrieve set of keys for a published map.
	 */
	public Set<String> getKeysForMap(String mapName) {
		return getMap(mapName).keySet();
	}
	
	public void putInMap(String mapName, String key, Object value) {		
		getMap(mapName).put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(String mapName) {
		Object cached = dataCache.getCachedValue(mapName);
		Validate.isTrue(cached != null, "LAPIS data cache does not contain a map with this name: %s", mapName);
		Validate.isTrue(cached instanceof Map, "Type of published data for %s was not Map but %s", cached.getClass());
		return (Map<String, Object>) cached;
	}
	

	/**
	 * Retrieves the value of the published variable from LAPIS' cache.
	 * @param publishedVariableName the local (published) name of the variable
	 * @return the value from the cache
	 */
	public Object retrieveCachedValue(String publishedVariableName) {
		return dataCache.getCachedValue(publishedVariableName);
	}
	
	/**
	 * Sets the value of the variable in LAPIS' cache. The cache is used to serve 
	 * get and set calls.
	 * @param publishedVariableName the local (published) name of the variable
	 * @param value the value to set
	 */
	public void setCachedValue(String publishedVariableName, Object value) {
		dataCache.setCachedValue(publishedVariableName, value);
	}
	
	/**
	 * Set the logging level for a particular category. The specified level name
	 * should match the name of an enum constant in org.apache.log4j.Level.
	 * @param category the category, such as edu.osu
	 * @param levelName the level name, such as "WARN".
	 */
	public void setLoggingLevel(String category, String levelName) {
		Logger.setLevel(category, levelName);
	}
	
	/**
	 * Set the logging level for a particular category.
	 * @param category
	 * @param level
	 */
	public void setLoggingLevel(String category, org.apache.log4j.Level level) {
		Logger.setLevel(category, level);
	}
	
	/**
	 * Shut down this LAPIS node. This shuts down the server. This method is most useful 
	 * in testing, to allow the user to re-initialize the LAPIS node without restarting
	 * MATLAB.
	 */
	public void shutdown() {
		lapisCoreApi.shutdown();
	}
	
	/**
	 * Un-publish a variable.
	 * @param variableName the published name of the variable
	 */
	public void redact(String variableName) {
		lapisCoreApi.redact(variableName);
	}
	
	/**
	 * Declare this node 'ready'. Applications do not need to declare themselves
	 * ready in order to use LAPIS functionality, but they can use ready(), notReady(),
	 * and waitForReadyNode() to facilitate coordination among multiple nodes on 
	 * a LAPIS network.
	 */
	public void ready() {
		lapisCoreApi.ready();
	}
	
	/**
	 * Declare this node 'not ready'.
	 */
	public void notReady() {
		lapisCoreApi.notReady();
	}
	
	/**
	 * Wait for a node to declare that it is ready. This method blocks indefinitely
	 * until the specified node has joined the network and declared that it is ready.
	 * @param nodeName the name of the node
	 */
	public void waitForReadyNode(String nodeName) {
		lapisCoreApi.waitForReadyNode(nodeName);
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
	public void waitForReadyNode(String nodeName, double millisToWait) throws TimeoutException {
		lapisCoreApi.waitForReadyNode(nodeName, (long)millisToWait);
	}
	
	public static String getJavaTypeOfObject(Object obj) {
		String typeOfObject = obj.getClass().toString();
		System.out.println("type: " + typeOfObject);
		if(obj.getClass().isArray()) {
			System.out.println("  component type: " + obj.getClass().getComponentType());
			int length = Array.getLength(obj);
			for(int i = 0; i < length; ++i) {
				System.out.println("  [" + i + "] = " + Array.get(obj, i));
			}
		}
		return typeOfObject;
	}
}