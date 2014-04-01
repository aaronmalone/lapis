package edu.osu.lapis.restlets;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

public class HeartbeatRestlet extends LapisRestletBase {

	@Override
	/**
	 * Returns HTTP 204 SUCCESS_NO_CONTENT.
	 */
	public void get(Request request, Response response) {
		response.setStatus(Status.SUCCESS_NO_CONTENT);
	}
}
