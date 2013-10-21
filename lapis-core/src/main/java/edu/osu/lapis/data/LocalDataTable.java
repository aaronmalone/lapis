package edu.osu.lapis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDataTable {
	private final Map<String, LocalVariable> localDataMap = new HashMap<String, LocalVariable>();
	
	public void put(String localName, LocalVariable localVariable) {
		localDataMap.put(localName, localVariable);
	}
	
	public LocalVariable get(String localName) {
		return localDataMap.get(localDataMap);
	}
	
	public List<LocalVariable> getAll() {
		return new ArrayList<>(localDataMap.values());
	}

	public void remove(String localName) {
		localDataMap.remove(localName);
	}
}
