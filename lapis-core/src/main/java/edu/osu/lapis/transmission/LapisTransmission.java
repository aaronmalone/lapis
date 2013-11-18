package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.common.io.ByteStreams;

public class LapisTransmission {
	
	public byte[] executeClientCallReturnBytes(ClientCall clientCall) {
		ClientResponse response = executeClientCall(clientCall);
		return response.getPayload();
	}
	
	public ClientResponse executeClientCall(ClientCall clientCall) {
		ClientResource clientResource = new ClientResource(clientCall.getUri());
		final Representation representation;
		try {
			switch(clientCall.getMethod()) {
			case GET:
				representation = clientResource.get();
				System.out.println("GET representation = " + representation); //TODO REMOVE
				System.out.println("GET representation.size = " + representation.getSize()); //TODO REMOVE
				break;
			case PUT:
				representation = clientResource.put(createRestletRepresentation(clientCall));
				break;
			case POST:
				representation = clientResource.post(createRestletRepresentation(clientCall));
				break;
			case DELETE:
				representation = clientResource.delete();
				break;
			default:
				throw new IllegalArgumentException("Cannot handle client call with method: " 
						+ clientCall.getMethod());
			}
			int statusCode = clientResource.getStatus().getCode();
			System.out.println("status: " + statusCode); //TODO REMOVE
			byte[] payload = representation != null ? getPayload(representation) : null;
			System.out.println("payload: " + Arrays.toString(payload)); //TODO REMOVE
			return new ClientResponse(statusCode, payload);
		} catch(ResourceException originalException) {
			Status originalStatus = clientResource.getStatus();
			String entityAstext = clientResource.getResponse().getEntityAsText().trim();
			String originalDescription = originalStatus.getDescription().trim();
			String description = originalDescription.isEmpty() ? 
					entityAstext : originalDescription + ": " + entityAstext; 
			Status newStatus = new Status(originalStatus.getCode(), originalException,
					originalStatus.getName(), description, clientCall.getUri());
			throw new ResourceException(newStatus, originalException);
		} finally {
			clientResource.release();
		}
	}
	
	private Representation createRestletRepresentation(ClientCall clientCall) {
		ByteArrayInputStream input = new ByteArrayInputStream(clientCall.getPayload());
		return new InputRepresentation(input);
	}
	
	private byte[] getPayload(Representation representation) {
		System.out.println("REPRESENTATION: " + representation); //TODO REMOVE
		long size = representation.getSize();
		if(size == Representation.UNKNOWN_SIZE || size > 0) {
			try(InputStream stream = representation.getStream()) {
				return ByteStreams.toByteArray(stream);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return new byte[0];
		}
	}
}
