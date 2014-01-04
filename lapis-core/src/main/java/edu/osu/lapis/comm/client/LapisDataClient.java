package edu.osu.lapis.comm.client;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.google.common.annotations.VisibleForTesting;

import edu.osu.lapis.comm.serial.DataClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;

public class LapisDataClient {
	
	private GlobalDataTable globalDataTable;
	private DataClientCommunicationImpl dataClientCommunicationImpl;

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
		Validate.isTrue(globalDataTable.get(fullName).getLapisPermission() == LapisPermission.READ_WRITE, 
				"The remote variable %s is read-only.", fullName);
		dataClientCommunicationImpl.setVariableValue(variableFullName, value);
	}
		
	/**
	 * Checks that the variable meta-data is cached locally (in the GlobalDataTable)
	 * and that the type of the cached meta-data agrees with the expected type. If
	 * the variable meta-data is not present, or does not have the expected type,
	 * retrieves the variable meta-data from the remote LAPIS node and attempts to 
	 * validate the type of the retrieved meta-data. An exception is thrown if the 
	 * remote variable does not exist or does not match the expected type.
	 * @param variableFullName the VariableFullName object for the variable
	 * @param expectedType the expected type
	 */
	@VisibleForTesting
	void validateVariableExistence(VariableFullName variableFullName) {
		VariableMetaData metaData = globalDataTable.get(variableFullName);
		if(metaData == null) {
			metaData = getRemoteVariableMetaData(variableFullName.toString());
			globalDataTable.put(variableFullName, metaData);
		}
	}

	public void setGlobalDataTable(GlobalDataTable globalDataTable) {
		this.globalDataTable = globalDataTable;
	}

	public void setDataClientCommunicationImpl(DataClientCommunicationImpl dataClientCommunicationImpl) {
		this.dataClientCommunicationImpl = dataClientCommunicationImpl;
	}
}