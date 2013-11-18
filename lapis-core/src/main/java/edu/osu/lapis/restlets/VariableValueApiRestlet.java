package edu.osu.lapis.restlets;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import com.google.gson.Gson;

import edu.osu.lapis.data.LapisVariable;
import edu.osu.lapis.data.LocalDataTable;
import edu.osu.lapis.restlets.filters.DeserializedTypeAndDimensionValidator;
import edu.osu.lapis.restlets.filters.VariableNameAttrValidator;
import edu.osu.lapis.restlets.filters.VariablePresentValidator;
import edu.osu.lapis.restlets.filters.VariableValueExtractor;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;
import edu.osu.lapis.util.Attributes;

public class VariableValueApiRestlet extends LapisRestletBase {
	
	private LocalDataTable localDataTable;
	private LapisSerialization lapisSerialization;
	private MediaType responseMediaType;

	public Restlet getVariableValueRestletWithFilters() {
		LapisFilterChainRestletBase filter = new LapisFilterChainRestletBase();
		
		filter.setPostFilters(
				new VariableNameAttrValidator(),
				new VariablePresentValidator(localDataTable),
				new OptionalTypeValidator(localDataTable),
				new VariableValueExtractor(lapisSerialization),
				new DeserializedTypeAndDimensionValidator(localDataTable));
		filter.setPostTargetRestlet(this);
	
		filter.setGetFilters(
				new VariableNameAttrValidator(),
				new VariablePresentValidator(localDataTable),
				new OptionalTypeValidator(localDataTable));
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
		assert localVariable.getType() == serializationObject.getType();
		assert Arrays.equals(localVariable.getDimension(), serializationObject.getDimension());
		if(localVariable.getType().isArrayType()) {
			Object src = serializationObject.getData();
			Object dest = localVariable.getReference();
			assert Array.getLength(src) == Array.getLength(dest);
			System.arraycopy(src, 0, dest, 0, Array.getLength(dest));
		} else {
			throw new UnsupportedOperationException("Non-array types not currently supported.");
		}
	}

	@Override
	public void get(Request request, Response response) {
		String variableName = Attributes.getVariableName(request);
		LapisVariable localVariable = localDataTable.get(variableName);
		response.setEntity(getResponseRepresentation(variableName, localVariable));
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
		serializationObject.setType(localVariable.getType());
		serializationObject.setDimension(localVariable.getDimension());
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