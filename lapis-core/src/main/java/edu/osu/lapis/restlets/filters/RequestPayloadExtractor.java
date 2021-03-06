package edu.osu.lapis.restlets.filters;

import edu.osu.lapis.util.ClientUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.routing.Filter;

public class RequestPayloadExtractor extends Filter {

	public static final String EXTRACTED_REQUEST_PAYLOAD_ATTR
			= "EXTRACTED_REQUEST_PAYLOAD_ATTR";

	@Override
	protected final int beforeHandle(Request request, Response response) {
		byte[] bytes = ClientUtils.getMessageEntityAsBytes(request);
		request.getAttributes().put(EXTRACTED_REQUEST_PAYLOAD_ATTR, bytes);
		return beforeHandle(request, response, bytes);
	}

	protected int beforeHandle(Request request, Response response, byte[] requestPayload) {
		//can be overriden
		return CONTINUE;
	}
}
