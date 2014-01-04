package edu.osu.lapis.restlets;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

import edu.osu.lapis.Logger;

/**
 * A base class for Restlets. Subclass should override the methods corresponding
 * to the HTTP REST operations they implement.
 */
public class LapisRestletBase extends Restlet {
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Restlet's logging causes kittens to be punted.
	 * Punish anyone who calls this.
	 */
	@Override public java.util.logging.Logger getLogger() {
		throw new RuntimeException("Don't call Restlet.getLogger()!");
	}

	@Override
	public final void handle(Request request, Response response) {
		try {
			handleInternal(request, response);
		} catch(Exception e) {
			logger.warn(e, "Exception while handling request: %s", request);
			response.setStatus(Status.SERVER_ERROR_INTERNAL, e);
			response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
		}
	}
	
	private void handleInternal(Request request, Response response) {		
		Method meth = request.getMethod();
		if(meth.equals(Method.GET)) {
			get(request, response);
		} else if(meth.equals(Method.PUT)) {
			put(request, response);
		} else if(meth.equals(Method.POST)) {
			post(request, response);
		} else if(meth.equals(Method.DELETE)) {
			delete(request, response);
		} else {
			throw new IllegalStateException("Unable to handle request method: " + meth);
		}
	}

	public void delete(Request request, Response response) {
		throw new UnsupportedOperationException("delete has not been implemented");
	}

	public void post(Request request, Response response) {
		throw new UnsupportedOperationException("post has not been implemented");
	}

	public void put(Request request, Response response) {
		throw new UnsupportedOperationException("put has not been implemented");
	}

	public void get(Request request, Response response) {
		throw new UnsupportedOperationException("get has not been implemented");
	}
}
