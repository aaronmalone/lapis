package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;

import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.serialization.SerializationObject;

public class VariableValueExtractor extends RequestPayloadExtractor {

	public static final String DESERIALIZED_VARIABLE_VALUE = 
			"DESERIALIZED_VARIABLE_VALUE";
	
	private LapisSerialization lapisSerialization;
	
	public VariableValueExtractor(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	@Override
	protected int beforeHandle(Request request, Response response, byte[] requestPayload) {
		SerializationObject deserialized = lapisSerialization.deserializeModelData(requestPayload);
		request.getAttributes().put(DESERIALIZED_VARIABLE_VALUE, deserialized);
		return CONTINUE;
	}
}
