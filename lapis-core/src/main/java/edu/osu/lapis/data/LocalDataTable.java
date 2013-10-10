package edu.osu.lapis.data;

import java.util.HashMap;
import java.util.Map;

public class LocalDataTable {
	private final Map<String, LocalVariableMetaData> localDataMap = new HashMap<String, LocalVariableMetaData>();
	
	public void put(String localName, LocalVariableMetaData localVariableMetaData) {
		validateLocalName(localName);
		localDataMap.put(localName, localVariableMetaData);
	}

	public void remove(String localName) {
		validateLocalName(localName);
		localDataMap.remove(localName);
	}
	
	private void validateLocalName(String localName) {
		// TODO implement
	}
}
