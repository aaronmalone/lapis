package edu.osu.lapis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDataTable {
	private final Map<String, LapisVariable> localDataMap = new HashMap<String, LapisVariable>();
	
	public void put(String localName, LapisVariable variable) {
		localDataMap.put(localName, variable);
	}
	
	public LapisVariable get(String localName) {
		return localDataMap.get(localName);
	}
	
	public List<LapisVariable> getAll() {
		return new ArrayList<>(localDataMap.values());
	}

	public void remove(String localName) {
		localDataMap.remove(localName);
	}
}