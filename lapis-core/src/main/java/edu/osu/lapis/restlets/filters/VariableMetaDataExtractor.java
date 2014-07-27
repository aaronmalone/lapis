package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.data.VariableMetaData;
import edu.osu.lapis.serialization.LapisSerialization;
import org.restlet.Request;
import org.restlet.Response;

public class VariableMetaDataExtractor extends RequestPayloadExtractor {

	public static final String DESERIALIZED_META_DATA_ATTRIBUTE
			= "DESERIALIZED_META_DATA_ATTRIBUTE";

	private LapisSerialization lapisSerialization;

	public VariableMetaDataExtractor(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	public VariableMetaDataExtractor() {
		// default		
	}

	@Override
	protected int beforeHandle(Request request, Response response, byte[] requestPayload) {
		VariableMetaData meta = lapisSerialization.deserializeMetaData(requestPayload);
		request.getAttributes().put(DESERIALIZED_META_DATA_ATTRIBUTE, meta);
		return CONTINUE;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
