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
	
	private final Map<String, LapisVariable> localDataMap = 
			Collections.synchronizedMap(new HashMap<String, LapisVariable>());
	
	public void put(String localName, LapisVariable variable) {
		log.info("Adding variable {} to local data table.",localName);
		localDataMap.put(localName, variable);
	}
	
	public LapisVariable get(String localName) {
		return localDataMap.get(localName);
	}
	
	public List<LapisVariable> getAll() {
		return new ArrayList<LapisVariable>(localDataMap.values());
	}

	public void remove(String localName) {
		localDataMap.remove(localName);
	}
}