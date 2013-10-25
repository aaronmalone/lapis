package edu.osu.lapis.communicator.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;

public class VariableValueApiRestlet extends LapisRestletBase {
	
	private LocalDataTable localDataTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

	@Override
	public void post(Request request, Response response) {
		FlattenedRequest flattenedRequest = new FlattenedRequest(request);
		LapisVariable localVariable = getValidLocalVariable(flattenedRequest);
		SerializationObject serializationObject = getDeserializedData(request);
		validatePostedData(flattenedRequest, localVariable, serializationObject);
		setValueInLocalDataTable(serializationObject);
		response.setStatus(Status.SUCCESS_OK);
	}

	@Override
	public void get(Request request, Response response) {
		FlattenedRequest flattenedRequest = new FlattenedRequest(request);
		LapisVariable localVariable = getValidLocalVariable(flattenedRequest);
		response.setEntity(getResponseRepresentation(flattenedRequest.variableName, localVariable));
		response.setStatus(Status.SUCCESS_OK);
	}
	
	private LapisVariable getValidLocalVariable(FlattenedRequest req) {
		String name = req.variableName;
		LapisVariable localVariable = localDataTable.get(name);
		LapisDataType localVariableType = localVariable.getVariableMetaData().getType();
		Validate.notNull(localVariable, "There is no variable named \"" + name + "\" in this model.");
		Validate.isTrue(req.lapisDataType == localVariableType, name + " has the type " 
				+ localVariableType + " but " + req.lapisDataType + " was requested.");
		return localVariable;
	}
	
	private SerializationObject getDeserializedData(Request request) {
		try (InputStream input = request.getEntity().getStream()){
			return lapisSerialization.deserializeModelData(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void validatePostedData(FlattenedRequest req, LapisVariable variable, SerializationObject serializationObject) {
		Validate.isTrue(req.variableName.equals(serializationObject.getName()), "Names do not match.");
		Validate.isTrue(variable.getVariableMetaData().getType() == serializationObject.getType());
	}
	
	private void setValueInLocalDataTable(SerializationObject serializationObject) {
		LapisVariable localVariable = new LapisVariable(serializationObject.getData());
		localDataTable.put(serializationObject.getName(), localVariable);
	}
	private Representation getResponseRepresentation(String name, LapisVariable localVariable) {
		SerializationObject serializationObject = createSerializationObject(name, localVariable);
		byte[] serialized = lapisSerialization.serialize(serializationObject);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(serialized);
		return new InputRepresentation(inputStream, responseMediaType, serialized.length);
	}
	
	private SerializationObject createSerializationObject(String name, LapisVariable localVariable) {
		SerializationObject serializationObject = new SerializationObject();
		serializationObject.setName(name);
		serializationObject.setData(localVariable.getReference());
		serializationObject.setType(localVariable.getVariableMetaData().getType());
		serializationObject.setDimension(localVariable.getVariableMetaData().getDimension());
		return serializationObject;
	}
	
	private static class FlattenedRequest {
		final String variableName;
		final LapisDataType lapisDataType;
		
		public FlattenedRequest(Request request) {
			variableName = (String) request.getAttributes().get("variableName");
			Validate.notEmpty(variableName, "Variable name must not be null.");
			Form form =  request.getResourceRef().getQueryAsForm();
			Validate.notNull(form, "Must provide a query with type specified.");
			lapisDataType = LapisDataType.valueOf(form.getFirstValue("type"));
		}
	}
}