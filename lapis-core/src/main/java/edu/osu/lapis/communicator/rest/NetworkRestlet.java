package edu.osu.lapis.communicator.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.network.NetworkTable;
import edu.osu.lapis.serialize.LapisSerialization;

public class NetworkRestlet extends LapisRestletBase {

	//TODO ADD SETTERS
	private LapisSerialization lapisSerialization;
	private NetworkTable networkTable;
	private MediaType responseMediaType;
	
	@Override
	public void delete(Request request, Response response) {
		LapisNode node = getLapisNodeFromRequestBody(request);
		LapisNode removedNode = networkTable.removeNode(node);
		if(removedNode != null) {
			response.setStatus(Status.SUCCESS_NO_CONTENT);
			//do we want to use this?
		} else {
			response.setStatus(Status.CLIENT_ERROR_NOT_FOUND /* 404 */);
			//do we want to use this?
		}
	}

	@Override
	public void post(Request request, Response response) {
		LapisNode lapisNode = getLapisNodeFromRequestBody(request);
		networkTable.updateNode(lapisNode);
		response.setStatus(Status.SUCCESS_OK);
	}

	@Override
	public void put(Request request, Response response) {
		LapisNode lapisNode = getLapisNodeFromRequestBody(request);
		networkTable.addNode(lapisNode);
		response.setStatus(Status.SUCCESS_CREATED);
	}

	@Override
	public void get(Request request, Response response) {
		LapisNode me = networkTable.getLocalNode();
		byte[] serialized = lapisSerialization.serialize(me);
		ByteArrayInputStream baos = new ByteArrayInputStream(serialized);
		Representation resposeEntity = new InputRepresentation(baos, responseMediaType, serialized.length);
		response.setEntity(resposeEntity);
		//do we need to set status here?
	}
	
	private LapisNode getLapisNodeFromRequestBody(Request request) {
		try (InputStream stream = request.getEntity().getStream()) {
			return lapisSerialization.deserializeLapisNode(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
