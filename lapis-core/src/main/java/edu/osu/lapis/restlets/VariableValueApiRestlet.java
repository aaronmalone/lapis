package edu.osu.lapis.restlets;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.osu.lapis.data.LapisPermission;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.restlets.filters.NotReadOnlyValidator;
import edu.osu.lapis.restlets.filters.VariableNameAttrValidator;
import edu.osu.lapis.restlets.filters.VariablePresentValidator;
import edu.osu.lapis.restlets.filters.VariableValueExtractor;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.util.Attributes;
import edu.osu.lapis.util.StackTraceUtil;

public class VariableValueApiRestlet extends LapisRestletBase {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private LocalDataTable localDataTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

	public Restlet getVariableValueRestletWithFilters() {
		LapisFilterChainRestletBase filter = new LapisFilterChainRestletBase();
		
		filter.setPostFilters(
				new VariableNameAttrValidator(),
				new VariablePresentValidator(localDataTable),
				new NotReadOnlyValidator(localDataTable),
				new VariableValueExtractor(lapisSerialization));
		filter.setPostTargetRestlet(this);
	
		filter.setGetFilters(
				new VariableNameAttrValidator(),
				new VariablePresentValidator(localDataTable));
		filter.setGetTargetRestlet(this);
		
		return filter;
		
	}
	
	@Override
	public void post(Request request, Response response) {
		//note: not going to validate name WITHIN serialization object
		SerializationObject serializationObject = Attributes.getAttribute(request, 
				VariableValueExtractor.DESERIALIZED_VARIABLE_VALUE, SerializationObject.class);
		String variableName = Attributes.getVariableName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		updateValue(localVariable, serializationObject);
	}
	
	private void updateValue(LapisVariable localVariable, SerializationObject serializationObject) {
		Validate.isTrue(localVariable.getLapisPermission() == LapisPermission.READ_WRITE, "Cannot update read-only variable");
		localVariable.setValue(serializationObject.getData());
	}

	@Override
	public void get(Request request, Response response) {
		try {
			String variableName = Attributes.getVariableName(request);
			logger.debug("Call to get value of variable '{}'", variableName);
			LapisVariable localVariable = localDataTable.get(variableName);
			response.setEntity(getResponseRepresentation(variableName, localVariable));
		} catch (Exception e) {
			logger.error("Error while retrieving variable value.", e);
			//TODO CAN PROBABLY IMPROVE THIS
			Throwable cause = e.getCause();
			if(cause instanceof TimeoutException) {
				response.setStatus(Status.SERVER_ERROR_INTERNAL, cause, cause.getMessage());
				response.setEntity(cause.getMessage(), MediaType.TEXT_PLAIN);
			} else {
				String stackTrace = StackTraceUtil.getStrackTraceAsString(e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL, e, "Unable to retrieve variable value.");
				response.setEntity("Unable to retrieve variable value:\n" + stackTrace, MediaType.TEXT_PLAIN);				
			}
		}
	}
	
	private Representation getResponseRepresentation(String name, LapisVariable localVariable) {
		SerializationObject serializationObject = createSerializationObject(name, localVariable);
		byte[] serialized = lapisSerialization.serialize(serializationObject);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(serialized);
		return new InputRepresentation(inputStream, responseMediaType, serialized.length);
	}
	
	private SerializationObject createSerializationObject(String name, LapisVariable localVariable) {
		Object value = localVariable.getValue();
		SerializationObject serializationObject = new SerializationObject(name, value);
		return serializationObject;
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