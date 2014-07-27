package edu.osu.lapis.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlobalDataTable {

	private final Map<VariableFullName, VariableMetaData> globalDataMap =
			Collections.synchronizedMap(new HashMap<VariableFullName, VariableMetaData>());

	public void put(String fullName, VariableMetaData variableMetaData) {
		VariableFullName varName = new VariableFullName(fullName);
		put(varName, variableMetaData);
	}

	public void put(VariableFullName varName, VariableMetaData variableMetaData) {
		globalDataMap.put(varName, variableMetaData);
	}

	public VariableMetaData get(String fullName) {
		return get(new VariableFullName(fullName));
	}

	public VariableMetaData get(VariableFullName fullName) {
		return globalDataMap.get(fullName);
	}

	public void remove(String fullName) {
		remove(new VariableFullName(fullName));
	}

	public void remove(VariableFullName varName) {
		globalDataMap.remove(varName);
	}
}
