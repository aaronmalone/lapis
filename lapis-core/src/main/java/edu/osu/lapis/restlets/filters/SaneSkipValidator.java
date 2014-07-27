package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.routing.Validator;

public class SaneSkipValidator extends Validator {

	@Override
	protected int beforeHandle(Request request, Response response) {
		int returnValue = super.beforeHandle(request, response);
		if (returnValue == CONTINUE && response.getStatus().isClientError()) {
			returnValue = SKIP;
		}
		return returnValue;
	}

}
