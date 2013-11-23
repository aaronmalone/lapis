package edu.osu.lapis.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDataTable {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final Map<String, LapisVariable2> localDataMap = 
			Collections.synchronizedMap(new HashMap<String, LapisVariable2>());
	
	public void put(String localName, LapisVariable2 variable) {
		log.info("Adding variable {} to local data table.",localName);
		localDataMap.put(localName, variable);
	}
	
	public LapisVariable2 get(String localName) {
		return localDataMap.get(localName);
	}
	
	public List<LapisVariable2> getAll() {
		return new ArrayList<LapisVariable2>(localDataMap.values());
	}

	public void remove(String localName) {
		localDataMap.remove(localName);
	}
}