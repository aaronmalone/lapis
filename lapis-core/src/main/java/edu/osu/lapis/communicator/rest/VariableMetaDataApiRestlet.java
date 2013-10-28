package edu.osu.lapis.communicator.rest;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import edu.osu.lapis.Constants;
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
		Object variableNameObj = request.getAttributes().get(Constants.VARIABLE_NAME_ATTRIBUTE);
		if(variableNameObj == null) {
			respondWithMetaDataForAllVariables(response);
		} else {
			String variableName = variableNameObj.toString();
			respondWithMetaDataForOneVariable(response, variableName);
		}
	}
	
	private void respondWithMetaDataForOneVariable(Response response, String name) {
		LapisVariable localVariable = localDataTable.get(name);
		if(localVariable != null) {
			byte[] serialized = lapisSerialization.serialize(localVariable.getVariableMetaData());
			response.setEntity(createResponseEntity(serialized));
			response.setStatus(Status.SUCCESS_OK);
		} else {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			response.setEntity(String.format("The variable \"%s\" does not exist in this node.", name), MediaType.TEXT_PLAIN);
		}
	}
	
	//TODO ADD API CALL FOR THIS
	private void respondWithMetaDataForAllVariables(Response response) {
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
	
	public void setLocalDataTable(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public void setResponseMediaType(MediaType responseMediaType) {
		this.responseMediaType = responseMediaType;
	}
}
