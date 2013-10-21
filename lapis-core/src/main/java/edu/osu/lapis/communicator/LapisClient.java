package edu.osu.lapis.communicator;

import edu.osu.lapis.data.GlobalDataTable;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;

//TODO PULL NETWORK-CHECKING FUNCTIONALITY UP INTO THIS CLASS

public class LapisClient {
	
	private GlobalDataTable globalDataTable;
	private ClientCommunication clientCommunication;

	
	//TODO CONSIDER USE OF GENERICS FOR METHOD BELOW
	//TODO ADD JAVADOC COMMENT
	public Object getRemoteVariableValue(String fullName, LapisDataType expectedType) {
		VariableFullName variableFullName = new VariableFullName(fullName);
		validateVariableType(variableFullName, expectedType);
		return clientCommunication.getVariableValue(variableFullName);
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
	private void validateVariableType(VariableFullName variableFullName, LapisDataType expectedType) {
		VariableMetaData metaData = globalDataTable.get(variableFullName);
		if(metaData == null || metaData.getType() != expectedType) {
			metaData = clientCommunication.getVariableMetaData(variableFullName);
			globalDataTable.put(variableFullName, metaData);
			checkRemoteVariableAgainstExpectedType(metaData, expectedType);
		}
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

	public void setLapisCommunication(ClientCommunication clientCommunication) {
		this.clientCommunication = clientCommunication;
	}
}