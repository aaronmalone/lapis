package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;
import edu.osu.lapis.util.Attributes;
import org.restlet.Request;
import org.restlet.Response;

public class LapisNodeExtractor extends RequestPayloadExtractor {

	public static final String DESERIALIZED_LAPIS_NODE_ATTR
			= "DESERIALIZED_LAPIS_NODE_ATTR";

	private LapisSerialization lapisSerialization;

	public LapisNodeExtractor() {
		// default
	}

	public LapisNodeExtractor(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}

	@Override
	protected int beforeHandle(Request request, Response response, byte[] requestPayload) {
		LapisNode lapisNode = lapisSerialization.deserializeLapisNode(requestPayload);
		request.getAttributes().put(DESERIALIZED_LAPIS_NODE_ATTR, lapisNode);
		//this assertion... what do we want to do with it?
		assert Attributes.getModelName(request).equals(lapisNode.getNodeName());
		return CONTINUE;
	}

	public void setLapisSerialization(LapisSerialization lapisSerialization) {
		this.lapisSerialization = lapisSerialization;
	}
}
