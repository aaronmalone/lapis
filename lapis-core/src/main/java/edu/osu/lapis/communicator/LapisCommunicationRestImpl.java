package edu.osu.lapis.communicator;

import org.restlet.resource.ClientResource;

import edu.osu.lapis.data.VariableFullName;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.LapisDatum;

public class LapisCommunicationRestImpl implements LapisCommunication {
	
	private NetworkTable networkTable; 
	
	//TODO MOVE
	public void setNetworkTable(NetworkTable networkTable) {
		this.networkTable = networkTable;
	}

	@Override
	public VariableMetaData getVariableMetaData(VariableFullName fullName) {
		// TODO Auto-generated method stub
		
		LapisNode lapisNode = getValidNodeFromVariableName(fullName);
		ClientResource clientResource = 
		
		return null;
	}

	@Override
	public LapisDatum getVariableValue(VariableFullName fullName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private LapisNode getValidNodeFromVariableName(VariableFullName varName) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = networkTable.getNode(modelName);
		if(lapisNode == null) {
			//TODO HANDLE
		}
		return lapisNode;
	}
}