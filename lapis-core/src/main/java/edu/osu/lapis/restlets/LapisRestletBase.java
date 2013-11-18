package edu.osu.lapis.restlets;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

/**
 * A base class for Restlets. Subclass should override the methods corresponding
 * to the HTTP REST operations they implement.
 */
public class LapisRestletBase extends Restlet {

	@Override
	public final void handle(Request request, Response response) {
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
			throw new IllegalStateException("SHUT. DOWN. EVERYTHING!"); //TODO BETTER MESSAGE
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
