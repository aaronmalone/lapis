package edu.osu.lapis;

import edu.osu.lapis.communication.DataClientCommunicationImpl;
import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;

public class LapisDataClient {
	
	private GlobalDataTable globalDataTable;
	private DataClientCommunicationImpl renameMe;

	//TODO CONSIDER USE OF GENERICS FOR METHOD BELOW
	//TODO ADD JAVADOC COMMENT
	public Object getRemoteVariableValue(String fullName, LapisDataType expectedType) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		validateVariableExistenceAndType(variableFullName, expectedType);
		return renameMe.getVariableValue(variableFullName);
	}
	
	public void setRemoteVariableValue(String fullName, Object value) {
		LapisDataType expectedType = LapisDataType.getTypeForObject(value);
		VariableFullName variableFullName = new VariableFullName(fullName);
		validateVariableExistenceAndType(variableFullName, expectedType);
		renameMe.setVariableValue(variableFullName, value);
	}
		
	/**
	 * Checks that the variable meta-data is cached locally (in the GlobalDatatTable)
	 * and that the type of the cached meta-data agrees with the expected type. If
	 * the variable meta-data is not present, or does not have the expected type,
	 * retrieves the variable meta-data from the remote LAPIS node and attempts to 
	 * validate the type of the retrieved meta-data. An exception is thrown if the 
	 * remote variable does not exist or does not match the expected type.
	 * @param variableFullName the VariableFullName object for the variable
	 * @param expectedType the expected type
	 */
	void validateVariableExistenceAndType(VariableFullName variableFullName, LapisDataType expectedType) {
		VariableMetaData metaData = globalDataTable.get(variableFullName);
		if(metaData == null || metaData.getType() != expectedType) {
			metaData = getRemoteVariableMetadata(variableFullName);
			globalDataTable.put(variableFullName, metaData);
			checkRemoteVariableAgainstExpectedType(metaData, expectedType);
		}
	}
	
	private VariableMetaData getRemoteVariableMetadata(VariableFullName variableFullName) {
		return renameMe.getVariableMetaData(variableFullName);
	}

	/**
	 * Validates that the variable meta-data has the expected type.
	 * @param metaData the variable meta-data
	 * @param expectedType the expected type
	 */
	private void checkRemoteVariableAgainstExpectedType(VariableMetaData metaData, LapisDataType expectedType) {		
		if(metaData.getType() != expectedType) {
			throw new IllegalArgumentException("Remote variable  has type " 
					+ metaData.getType() 
					+ " but we attempted to retrieve it as "
					+ expectedType + ".");
		}
	}

	public void setGlobalDataTable(GlobalDataTable globalDataTable) {
		this.globalDataTable = globalDataTable;
	}
	
	public void setRenameMe(DataClientCommunicationImpl renameMe) {
		this.renameMe = renameMe;
	}
}