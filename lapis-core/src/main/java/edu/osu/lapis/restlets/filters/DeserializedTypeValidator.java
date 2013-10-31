package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.communicator.rest.Attributes;
import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.serialization.SerializationObject;

public class DeserializedTypeValidator extends Filter {
	
	private LocalDataTable localDataTable;
	
	public DeserializedTypeValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		LapisDataType deserializedType = getTypeFromDeserializedObject(request); 
		LapisDataType localVariableType = getTypeOfLocalVariable(request);
		if(deserializedType == localVariableType) {
			return CONTINUE;
		} else {
			String entity = "The type of the object in the payload does not match the type of the local variable.";
			response.setEntity(entity, MediaType.TEXT_PLAIN);
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return SKIP;
		}
	}
	
	private LapisDataType getTypeFromDeserializedObject(Request request) {
		SerializationObject serializationObject = Attributes.getAttribute(request, 
				VariableValueExtractor.DESERIALIZED_VARIABLE_VALUE, SerializationObject.class);
		return serializationObject.getType();
	}
	
	private LapisDataType getTypeOfLocalVariable(Request request) {
		String variableName = Attributes.getModelName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		return localVariable.getType();
	}
	
}
