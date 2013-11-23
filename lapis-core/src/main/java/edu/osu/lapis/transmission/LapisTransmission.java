package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

public class LapisTransmission {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final String CONNECTION_REFUSED = "Connection refused";
	
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
			byte[] payload = representation != null ? getPayload(representation) : null;
			return new ClientResponse(statusCode, payload);
		} catch(ResourceException originalException) {
			throw handleException(clientCall, clientResource, originalException);
		} finally {
			clientResource.release();
		}
	}
	
	private ResourceException handleException(ClientCall clientCall, ClientResource clientResource, ResourceException originalException) {
		Status originalStatus = clientResource.getStatus();
		String originalDescription = originalStatus.getDescription().trim();
		log.error("Handling exception '{}' from client call '{}'.", originalException, clientCall);
		if(originalDescription.startsWith(CONNECTION_REFUSED)) {
			throw new RuntimeException(CONNECTION_REFUSED + ": remote node may be down.", originalException);
		} else {
			final String entityText = getEntityText(clientResource.getResponse());
			String newDescription = getNewDescription(originalDescription, entityText);
			Status newStatus = getNewStatus(originalStatus, originalException, newDescription, clientCall.getUri());			
			return new ResourceException(newStatus, originalException);			
		}
	}
	
	private String getEntityText(Response response) {
		Representation rep = response.getEntity();
		try {
			if(rep != null) {
				return rep.getText();
			} else {
				return "";
			}
		} catch (IOException e) {
			log.error("Error while getting text from Restlet representation.", e);
			return "";
		}
	}
	
	private String getNewDescription(String originalDescription, String entityText) {
		if(originalDescription.isEmpty()) {
			return entityText;
		} else if(entityText.isEmpty()) {
			return originalDescription;
		} else {
			return originalDescription + ": " + entityText;
		}
	}
	
	private Status getNewStatus(Status originalStatus, Throwable originalException, String description, String uri) {
		return new Status(originalStatus.getCode(), originalException, originalStatus.getName(), description, uri);
	}
	
	private Representation createRestletRepresentation(ClientCall clientCall) {
		ByteArrayInputStream input = new ByteArrayInputStream(clientCall.getPayload());
		return new InputRepresentation(input);
	}
	
	private byte[] getPayload(Representation representation) {
		long size = representation.getSize();
		if(size == Representation.UNKNOWN_SIZE || size > 0) {
			InputStream stream = null;
			try {
				stream = representation.getStream();
				return ByteStreams.toByteArray(stream);
			} catch(IOException e) {
				throw new RuntimeException(e);
			} finally {
				if(stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} else {
			return new byte[0];
		}
	}
}
