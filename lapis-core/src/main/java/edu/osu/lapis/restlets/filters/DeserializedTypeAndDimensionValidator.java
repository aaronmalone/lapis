package edu.osu.lapis.restlets.filters;

import java.util.Arrays;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

import edu.osu.lapis.data.LapisDataType;
import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.util.Attributes;

public class DeserializedTypeAndDimensionValidator extends Filter {
	
	private static final String DIMENSION_MSG = 
			"The dimensions of the object in the payload, %s, do not match the dimensions of the local variable, %s.";
	
	private LocalDataTable localDataTable;
	
	public DeserializedTypeAndDimensionValidator(LocalDataTable localDataTable) {
		this.localDataTable = localDataTable;
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		LapisDataType deserializedType = getTypeFromDeserializedObject(request); 
		LapisDataType localVariableType = getTypeOfLocalVariable(request);
		if(deserializedType == localVariableType) {
			int[] deserializedDimensions = getDimensionsFromDeserializedObject(request);
			int[] localVariableDimensions = getDimensionsOfLocalVariable(request);
			if(Arrays.equals(localVariableDimensions, deserializedDimensions)) {
				return CONTINUE;
			} else {
				String entity = getFormattedDimensionMessage(deserializedDimensions, localVariableDimensions);
				response.setEntity(entity, MediaType.TEXT_PLAIN);
				response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return SKIP;
			}
		} else {
			String entity = "The type of the object in the payload does not match the type of the local variable.";
			response.setEntity(entity, MediaType.TEXT_PLAIN);
			response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return SKIP;
		}
	}
	
	private String getFormattedDimensionMessage(int[] payloadDims, int[] localDims) {
		return String.format(DIMENSION_MSG, 
				Arrays.toString(payloadDims),
				Arrays.toString(localDims));
	}

	private LapisDataType getTypeFromDeserializedObject(Request request) {
		SerializationObject serializationObject = Attributes.getAttribute(request, 
				VariableValueExtractor.DESERIALIZED_VARIABLE_VALUE, SerializationObject.class);
		return serializationObject.getType();
	}
	
	private LapisDataType getTypeOfLocalVariable(Request request) {
		String variableName = Attributes.getVariableName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		return localVariable.getType();
	}
	
	private int[] getDimensionsFromDeserializedObject(Request request) {
		SerializationObject serializationObject = Attributes.getAttribute(request, 
				VariableValueExtractor.DESERIALIZED_VARIABLE_VALUE, SerializationObject.class);
		return serializationObject.getDimension();
	}
	
	private int[] getDimensionsOfLocalVariable(Request request) {
		String variableName = Attributes.getVariableName(request);
		LapisVariable var = localDataTable.get(variableName);
		return var.getDimension();
	}
}
