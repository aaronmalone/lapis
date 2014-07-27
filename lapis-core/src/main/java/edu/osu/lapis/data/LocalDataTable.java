package edu.osu.lapis.data;

import edu.osu.lapis.Logger;

import java.util.*;

public class LocalDataTable {

	private final Logger logger = Logger.getLogger(getClass());

	private final Map<String, LapisVariable> localDataMap =
			Collections.synchronizedMap(new HashMap<String, LapisVariable>());

	public void put(String localName, LapisVariable variable) {
		logger.debug("Adding variable '%s' to local data table.", localName);
		localDataMap.put(localName, variable);
	}

	public LapisVariable get(String localName) {
		return localDataMap.get(localName);
	}

	public List<LapisVariable> getAll() {
		return new ArrayList<LapisVariable>(localDataMap.values());
	}

	public void remove(String localName) {
		logger.debug("Removing variable '%s' from local data table.", localName);
		localDataMap.remove(localName);
	}
}
