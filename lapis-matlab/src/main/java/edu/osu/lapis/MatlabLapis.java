package edu.osu.lapis;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.LapisPermission;
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
		Validate.notNull(initialValue, "Initial value of published variable cannot be null.");
		dataCache.setCachedValue(publishedVariableName, initialValue);
		lapisCoreApi.publish(publishedVariableName, createNewLapisVariable(publishedVariableName));
	}
	
	private LapisVariable createNewLapisVariable(String name) {
		return new LapisVariable(name, LapisPermission.READ_WRITE, 
				createCallableForMatlabVariable(name), createSettableForMatlabVariable(name));
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
	
	public void redact(String variableName) {
		lapisCoreApi.redact(variableName);
	}
	
	public void ready() {
		lapisCoreApi.ready();
	}
	
	public void notReady() {
		lapisCoreApi.notReady();
	}
	
	public void waitForReadyNode(String nodeName) {
		lapisCoreApi.waitForReadyNode(nodeName);
	}
	
	public void waitForReadyNode(String nodeName, double millisToWait) {
		lapisCoreApi.waitForReadyNode(nodeName, (long)millisToWait);
	}
}