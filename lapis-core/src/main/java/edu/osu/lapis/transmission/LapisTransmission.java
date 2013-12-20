package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.time.StopWatch;
import org.restlet.Response;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.common.io.ByteStreams;

import edu.osu.lapis.Logger;
import edu.osu.lapis.transmission.ClientCall.RestMethod;

public class LapisTransmission {
	
	private final Logger logger = Logger.getLogger(getClass());
	
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
	
	private LapisClientException handleException(ClientCall clientCall, ClientResource clientResource, ResourceException originalException) {
		logger.debug("Encountered exception '%s' executing client call '%s'.", originalException, clientCall);
		final String originalDescription = clientResource.getStatus().getDescription().trim();
		final String entityText = getEntityText(clientResource.getResponse());
		final String newMessage = entityText.trim().isEmpty() ? originalDescription : entityText;
		return new LapisClientException(newMessage, originalException);
	}
	
	private String getEntityText(Response response) {
		Representation rep = response.getEntity();
		try {
			if(rep != null) {
				String text = rep.getText();
				logger.trace("Call that resulted in exception had this text in its response representation: %s", text);
				return text;
			} else {
				logger.trace("Call that resulted in exception has null response representation.");
				return "";
			}
		} catch (IOException e) {
			logger.error(e, "Error while getting text from Restlet representation.");
			throw new RuntimeException(e);
		}
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
