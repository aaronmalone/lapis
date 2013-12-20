package edu.osu.lapis;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.Settable;

public class MatlabLapis {
	
	static {
		LapisLogging.init();
	}
	
	private final LapisCore lapisCoreApi;
	private final MatlabDataCache dataCache = new MatlabDataCache();
	
	public MatlabLapis(String name, String coordinatorAddress) {
		this(name, coordinatorAddress, coordinatorAddress, Boolean.TRUE.toString());
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String myAddress) {
		
		this(name, coordinatorAddress, myAddress, 
				Boolean.toString(StringUtils.equals(coordinatorAddress, myAddress)));
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String myAddress, String isCoordinator) {
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("coordinator.url", coordinatorAddress);
		properties.setProperty("isCoordinator", isCoordinator.toLowerCase());
		properties.setProperty("localNodeAddress", myAddress);
		lapisCoreApi = new LapisCore(properties);
	}
	
	/* PUBLISH */

	public void publish(String localVariableName, Object initialValue) {
		Validate.notNull(initialValue, "Initial value of published variable cannot be null.");
		dataCache.setCachedValue(localVariableName, initialValue);
		lapisCoreApi.publish(localVariableName, createNewLapisVariable(localVariableName));
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

	/* REMOTE METHODS */
	
	public Object get(String variableFullName) {
		return lapisCoreApi.getRemoteValue(variableFullName);
	}
	
	public void set(String variableFullName, Object value) {
		lapisCoreApi.setRemoteValue(variableFullName, value);
	}

	/* CACHED VALUE METHODS */
	
	public Object retrieveCachedValue(String name) {
		return dataCache.getCachedValue(name);
	}
	
	public void setCachedValue(String name, Object value) {
		dataCache.setCachedValue(name, value);
	}
	
	public void setLoggingLevel(String category, String levelName) {
		Logger.setLevel(category, levelName);
	}
	
	public void setLoggingLevel(String category, org.apache.log4j.Level level) {
		Logger.setLevel(category, level);
	}
		
	public void shutdown() {
		lapisCoreApi.shutdown();
	}
}
