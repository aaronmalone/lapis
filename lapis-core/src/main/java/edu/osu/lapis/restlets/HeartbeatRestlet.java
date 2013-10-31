package edu.osu.lapis.restlets;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class HeartbeatRestlet extends LapisRestletBase {

	@Override
	/**
	 * Returns HTTP 200 OK.
	 */
	public void get(Request request, Response response) {
		response.setStatus(Status.SUCCESS_OK);
	}
}
