package edu.osu.lapis.comm.client;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;

import edu.osu.lapis.comm.serial.DataClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.exception.LapisClientException;

public class LapisDataClient {
	
	private final GlobalDataTable globalDataTable;
	private final DataClientCommunicationImpl dataClientCommunicationImpl;
	
	public LapisDataClient(GlobalDataTable globalDataTable, DataClientCommunicationImpl communicationImpl) {
		this.globalDataTable = globalDataTable;
		this.dataClientCommunicationImpl = communicationImpl;
	}
	
	/**
	 * Retrieve the value of the remote variable.
	 * @param fullName the fully qualified (name@node) name of the published variable.
	 * @param expectedType
	 * @return the value of the remote variable
	 */
	public <T> T getRemoteVariableValue(String fullName, Class<T> cls) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		validateVariableExistence(variableFullName);
		return dataClientCommunicationImpl.getVariableValue(variableFullName, cls);
	}
	
	public VariableMetaData getRemoteVariableMetaData(String fullName) {
		return dataClientCommunicationImpl.getVariableMetaData(new VariableFullName(fullName));
	}
	
	public List<VariableMetaData> getVariableMetaDataForNode(String nodeName) {
		return dataClientCommunicationImpl.getVariableMetaDataForNode(nodeName);
	}
	
	/**
	 * Set the value of the remote variable.
	 * @param fullName the fully qualified (name@node) name of the published variable.
	 * @param value the value to set
	 */
	public void setRemoteVariableValue(String fullName, Object value) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		validateVariableExistence(variableFullName);
		VariableMetaData meta = globalDataTable.get(fullName); 
		validateNotReadOnly(meta, fullName);
		dataClientCommunicationImpl.setVariableValue(variableFullName, value);
	}
		
	private void validateNotReadOnly(VariableMetaData meta, String fullName) {
		if(meta.isReadOnly()) {
			throw new LapisClientException("Attempting to set variable " 
					+ fullName + ", which is read-only.");
		}
	}

	@VisibleForTesting
	void validateVariableExistence(VariableFullName variableFullName) {
		VariableMetaData metaData = globalDataTable.get(variableFullName);
		if(metaData == null) {
			metaData = getRemoteVariableMetaData(variableFullName.toString());
			globalDataTable.put(variableFullName, metaData);
		}
	}
}