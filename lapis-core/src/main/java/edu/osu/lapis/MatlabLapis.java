package edu.osu.lapis;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.osu.lapis.data.Dimensions;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable2;
import edu.osu.lapis.data.Settable;
import edu.osu.lapis.util.Sleep;

public class MatlabLapis {
	
	static {
		LapisLogging.init();
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LapisOperationThing lapisOperationThing = new LapisOperationThing();
	private final LapisCoreApi lapisCoreApi;
	
	public MatlabLapis(String name, String coordinatorAddress, String port, String isCoordinator) {
		Properties properties = new Properties();
		properties.setProperty("name", name);
		properties.setProperty("coordinator.url", coordinatorAddress);
		properties.setProperty("port", port);
		properties.setProperty("isCoordinator", isCoordinator.toLowerCase());
		lapisCoreApi = new LapisCoreApi(properties);
	}
	
	/* PUBLISH */

	public void publish(String localVariableName, Object initialValue) {
		logger.trace("Publishing variable {} with initial value {}.", localVariableName, initialValue);
		Validate.notNull("Initial value of published variable cannot be null.");
		lapisCoreApi.publish(localVariableName, createNewLapisVariable(localVariableName, initialValue));
	}
	
	/* REMOTE METHODS */

	@SuppressWarnings("deprecation")
	public Object get(String variableFullName) {
		logger.trace("Called get({})", variableFullName);
		return lapisCoreApi.getRemoteValue(variableFullName);
	}
	
	public void set(String variableFullName, Object value) {
		logger.trace("Called set({}, {})", variableFullName, value);
		lapisCoreApi.setRemoteValue(variableFullName, value);
	}
	
	/* OPERATION METHODS */
	
	public boolean hasOperation() {
		boolean hasOp = lapisOperationThing.hasOperation();
		if(hasOp) {
			logger.trace("hasOperation() returning true...");
		}
		return hasOp;
	}
	
	public LapisOperation retrieveOperation() {
		LapisOperation op = lapisOperationThing.retrieveOperation();
		logger.trace("Called retrieveOperation(). Returning {}.", op);
		return op;
	}
	
	public void operationResult(LapisOperation operation, Object resultData) {
		logger.trace("Called operationResult({}, {})", operation, resultData);
		lapisOperationThing.operationResult(operation, resultData);
	}
	
	private LapisVariable2 createNewLapisVariable(String name, Object initialValue) {
		LapisDataType lapisDataType = LapisDataType.getTypeForObject(initialValue);
		Validate.notNull(lapisDataType); //TODO FIGURE OUT IF WE NEED THIS CHECK
		int[] dimension = Dimensions.getDimensions(initialValue);
		return new LapisVariable2(name, lapisDataType, dimension, 
				createCallableForMatlabVariable(name), createSettableForMatlabVariable(name));
	}
	
	private Callable<Object> createCallableForMatlabVariable(final String name) {
		return new Callable<Object>() {
			@Override public Object call() throws Exception {
				LapisOperation getOperation = new LapisOperation(name);
				lapisOperationThing.addOperation(getOperation);
				return waitForOperationResult(getOperation);
			}
		};
	}
	
	private Settable createSettableForMatlabVariable(final String name) {
		return new Settable() {
			@Override public void set(Object value) {
				LapisOperation setOperation = new LapisOperation(name, value);
				lapisOperationThing.addOperation(setOperation);
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
		logger.trace("Waiting for result of operation {}.", operation);
		Object result = null;
		final long timeToWaitMillis = 1999; //TODO MAKE CONFIGURABLE
		final long initialTimeMillis = System.currentTimeMillis();
		while(result == null && System.currentTimeMillis() - initialTimeMillis < timeToWaitMillis) {
			result = lapisOperationThing.retrieveOperationResult(operation);
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
