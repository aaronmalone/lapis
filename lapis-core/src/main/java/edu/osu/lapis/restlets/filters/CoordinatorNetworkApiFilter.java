package edu.osu.lapis.restlets.filters;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.routing.Filter;

/**
 * PUT, POST, and DELETE methods should not be used on the /network API of a 
 * LAPIS coordinator node. This filter can be applied to error out any operation
 * that does not apply to the /network API of the coordinator.
 */
public class CoordinatorNetworkApiFilter extends Filter{

	@Override protected int beforeHandle(Request request, Response response) {
		Method method = request.getMethod();
		if(!method.equals(Method.GET)) {
			response.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
			response.setEntity(method + " method should not be called on coordinator's /network API.", 
					MediaType.TEXT_PLAIN);
			return SKIP;
		} else {
			return CONTINUE;
		}
	}

	
}
