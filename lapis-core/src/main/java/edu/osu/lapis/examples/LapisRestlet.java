package edu.osu.lapis.examples;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;

public class LapisRestlet extends Restlet {

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
			throw new IllegalStateException("SHUT. DOWN. EVERYTHING!");
		}
	}

	public void delete(Request request, Response response) {
		
	}

	public void post(Request request, Response response) {
		
	}

	public void put(Request request, Response response) {
		
	}

	public void get(Request request, Response response) {
		
	}
}
