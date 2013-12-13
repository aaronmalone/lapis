package edu.osu.lapis;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.Settable;
import edu.osu.lapis.util.Sleep;

public class MatlabLapis {
	
	static {
		LapisLogging.init();
	}
	
	private final LapisOperationHandler lapisOperationHandler = new LapisOperationHandler();
	private final LapisCore lapisCoreApi;
	
	public MatlabLapis(String name, String coordinatorAddress) {
		this(name, coordinatorAddress, getPort(coordinatorAddress), Boolean.TRUE.toString());
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String modelAddress) {
		//TODO ACTUALLY USE MODEL ADDRESS
		this(name, coordinatorAddress, getPort(modelAddress), Boolean.FALSE.toString());
	}
	
	private static String getPort(String url) {
		try {
			URL urlObj = new URL(url);
			return Integer.toString(urlObj.getPort());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Unable to parse port from address: " + url);
		}
	}
	
	public MatlabLapis(String name, String coordinatorAddress, String port, String isCoordinator) {
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("coordinator.url", coordinatorAddress);
		properties.setProperty("port", port);
		properties.setProperty("isCoordinator", isCoordinator.toLowerCase());
		lapisCoreApi = new LapisCore(properties);
	}
	
	/* PUBLISH */

	public void publish(String localVariableName, Object initialValue) {
		Validate.notNull(initialValue, "Initial value of published variable cannot be null.");
		lapisCoreApi.publish(localVariableName, createNewLapisVariable(localVariableName));
	}
	
	/* REMOTE METHODS */

	public Object get(String variableFullName) {
		return lapisCoreApi.getRemoteValue(variableFullName);
	}
	
	public void set(String variableFullName, Object value) {
		lapisCoreApi.setRemoteValue(variableFullName, value);
	}
	
	/* OPERATION METHODS */
	
	public boolean hasOperation() {
		return lapisOperationHandler.hasOperation();
	}
	
	public LapisOperation retrieveOperation() {
		return lapisOperationHandler.retrieveOperation();
	}
	
	public void operationResult(LapisOperation operation, Object resultData) {
		lapisOperationHandler.operationResult(operation, resultData);
	}
	
	private LapisVariable createNewLapisVariable(String name) {
		return new LapisVariable(name, LapisPermission.READ_WRITE, 
				createCallableForMatlabVariable(name), createSettableForMatlabVariable(name));
	}
	
	private Callable<Object> createCallableForMatlabVariable(final String name) {
		return new Callable<Object>() {
			@Override public Object call() throws Exception {
				LapisOperation getOperation = new LapisOperation(name);
				lapisOperationHandler.addOperation(getOperation);
				return waitForOperationResult(getOperation);
			}
		};
	}
	
	private Settable createSettableForMatlabVariable(final String name) {
		return new Settable() {
			@Override public void set(Object value) {
				LapisOperation setOperation = new LapisOperation(name, value);
				lapisOperationHandler.addOperation(setOperation);
				Object result = null;
				try {
					result = waitForOperationResult(setOperation);
				} catch (TimeoutException e) {
					throw new RuntimeException(e);
				}
				if(result == null) {
					throw new RuntimeException("Result for operation " + setOperation + " was null.");
				} else {
					//TODO FIGURE OUT WHAT TO DO HERE
					System.out.println(
							"result class: " + result.getClass()
							+"\nresult toString: " + result);
				}
			}
		};
	}
	
	private Object waitForOperationResult(LapisOperation operation) throws TimeoutException {
		Object result = null;
		final long timeToWaitMillis = 1750; //TODO MAKE CONFIGURABLE
		final long initialTimeMillis = System.currentTimeMillis();
		while(result == null && System.currentTimeMillis() - initialTimeMillis < timeToWaitMillis) {
			result = lapisOperationHandler.retrieveOperationResult(operation);
			if(result == null) {
				Sleep.sleep(5);
			}
		}
		if(result != null)
			return result;
		else
			throw new TimeoutException("Timed out while waiting for result of operation " + operation);
	}

	public void shutdown() {
		lapisCoreApi.shutdown();
	}
}
