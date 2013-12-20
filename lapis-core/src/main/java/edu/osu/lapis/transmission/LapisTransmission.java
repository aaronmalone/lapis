package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.time.StopWatch;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.common.io.ByteStreams;

import edu.osu.lapis.Logger;
import edu.osu.lapis.transmission.ClientCall.RestMethod;

public class LapisTransmission {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	private final String CONNECTION_REFUSED = "Connection refused";
	
	public byte[] executeClientCallReturnBytes(ClientCall clientCall) {
		ClientResponse response = executeClientCall(clientCall);
		return response.getPayload();
	}
	
	public ClientResponse executeClientCall(ClientCall clientCall) {
		String uri = clientCall.getUri();
		RestMethod method = clientCall.getMethod();
		ClientResource clientResource = new ClientResource(uri);		
		final Representation representation;
		logger.trace("About to execute %s on %s", method, uri);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			switch(method) {
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
				throw new IllegalArgumentException("Cannot handle client call with method: " + method);
			}
			stopWatch.stop();
			logger.trace("Took %d millis to execute %s on %s", stopWatch.getTime(), method, uri);
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
		logger.error("Handling exception '%s' from client call '%s'.", originalException, clientCall);
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
			logger.error(e, "Error while getting text from Restlet representation.");
			throw new RuntimeException(e);
		}
	}
	
	private String getNewDescription(String originalDescription, String entityText) {
		if(originalDescription.isEmpty()) {
			return entityText;
		} else if(entityText.isEmpty() || entityText.length() > 250) {
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
