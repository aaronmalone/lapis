package edu.osu.lapis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LapisOperationHandler {
	
	private Map<LapisOperation, Object> resultDataMap = Collections.synchronizedMap(new HashMap<LapisOperation, Object>());
	private BlockingQueue<LapisOperation> operationsQueue = new LinkedBlockingQueue<LapisOperation>();	
	
	public void addOperation(LapisOperation lapisOperation) {
		try {
			operationsQueue.put(lapisOperation);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
	public Object retrieveOperationResult(LapisOperation operation) {
		return resultDataMap.get(operation);
	}
	
	public boolean hasOperation() {
		return !operationsQueue.isEmpty();
	}
	
	public LapisOperation retrieveOperation() {
		return operationsQueue.poll();
	}
	
	public void operationResult(LapisOperation operation, Object resultData) {
		resultDataMap.put(operation, resultData);
	}
}
