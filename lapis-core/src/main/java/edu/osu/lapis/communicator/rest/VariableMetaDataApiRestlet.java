package edu.osu.lapis.communicator.rest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;

public class VariableMetaDataApiRestlet extends LapisRestletBase {

	private LocalDataTable localDataTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;
	
	@Override
	public void get(Request request, Response response) {
		Object variableNameObj = request.getAttributes().get("variableName");
		if(variableNameObj == null) {
			respondWithMetaDataForAllVariables(response);
		} else {
			String variableName = variableNameObj.toString();
			respondWithMetaDataForOneVariable(response, variableName);
		}
	}
	
	private void respondWithMetaDataForOneVariable(Response response, String name) {
		LapisVariable localVariable = localDataTable.get(name);
		Validate.notNull(localVariable, "Variable \"" + name + "\n does not exist");
		byte[] serialized = lapisSerialization.serialize(localVariable.getVariableMetaData());
		response.setEntity(createResponseEntity(serialized));
		response.setStatus(Status.SUCCESS_OK);
	}
	/*

	http://hostname/metaData
	http://hostname/metaData/${variableName}
	
	 */
	
	//TODO ADD API CALL FOR THIS
	private void respondWithMetaDataForAllVariables(Response response) {
		//TODO IMPLEMENT
		List<VariableMetaData> metaList = new ArrayList<>();
		for(LapisVariable local : localDataTable.getAll()) {
			metaList.add(local.getVariableMetaData());
		}
		byte[] serialized = lapisSerialization.serialize(metaList);
		response.setEntity(createResponseEntity(serialized));
		response.setStatus(Status.SUCCESS_OK);
	}

	private Representation createResponseEntity(byte[] serializedData) {
		ByteArrayInputStream stream = new ByteArrayInputStream(serializedData);
		return new InputRepresentation(stream, responseMediaType, serializedData.length);
	}	
}
