package edu.osu.lapis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.Settable;
import edu.osu.lapis.data.VariableFullName;

public class MatlabLapis {
	
	static {
		LapisLogging.init();
	}
	
	private final LapisCore lapisCoreApi;
	private final MatlabDataCache dataCache = new MatlabDataCache();
	private final String nodeName;
	
	public MatlabLapis(String name, String coordinatorAddress) {
		this(name, coordinatorAddress, getPort(coordinatorAddress), Boolean.TRUE.toString());
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String modelAddress) {
		//TODO ACTUALLY USE MODEL ADDRESS
		this(name, coordinatorAddress, getPort(modelAddress), Boolean.FALSE.toString());
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String port, String isCoordinator) {
		this.nodeName = name;
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("coordinator.url", coordinatorAddress);
		properties.setProperty("port", port);
		properties.setProperty("isCoordinator", isCoordinator.toLowerCase());
		lapisCoreApi = new LapisCore(properties);
	}
	
	private static String getPort(String url) {
		try {
			URL urlObj = new URL(url);
			return Integer.toString(urlObj.getPort());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to parse port from address: " + url);
		}
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

	public Object get(String variableFullName) {
		VariableFullName variableNameObject = new VariableFullName(variableFullName); 
		String nodeName = variableNameObject.getModelName();
		if(this.nodeName.equals(nodeName)) {
			return dataCache.getCachedValue(variableNameObject.getLocalName());
		} else {
			return lapisCoreApi.getRemoteValue(variableFullName);			
		}
	}
	
	public void set(String variableFullName, Object value) {
		VariableFullName variableNameObject = new VariableFullName(variableFullName); 
		String nodeName = variableNameObject.getModelName();
		if(this.nodeName.equals(nodeName)) {
			dataCache.setCachedValue(variableNameObject.getLocalName(), value);
		} else {
			lapisCoreApi.setRemoteValue(variableFullName, value);
		}
	}
		
	public void shutdown() {
		lapisCoreApi.shutdown();
	}
}
