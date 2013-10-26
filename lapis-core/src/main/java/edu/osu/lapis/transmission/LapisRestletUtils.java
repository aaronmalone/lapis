package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;

public class LapisRestletUtils {
	
	//TODO HOW DO WE USE THIS METHOD?
	public static LapisNode getLapisNodeFromRequestBody(Request request, LapisSerialization lapisSerialization) {
		try (InputStream stream = request.getEntity().getStream()) {
			return lapisSerialization.deserializeLapisNode(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String buildUri(String ... parts) {
		StringBuilder sb = new StringBuilder();
		for(String part : parts) {
			sb.append(trimSlashes(part));
			sb.append('/');
		}
		return sb.substring(0, sb.length()-1); //remove trailing slash //TODO TEST THIS
	}
	
	private static String trimSlashes(String input) {
		String s = StringUtils.removeStart(input, "/");
		return StringUtils.removeEnd(s, "/");
	}
	
	public static InputRepresentation createRepresentation(byte[] serializedData) {
		ByteArrayInputStream stream = new ByteArrayInputStream(serializedData);
		return new InputRepresentation(stream);
	}
	
	public static InputRepresentation createRepresentation(byte[] serializedData, MediaType mediaType) {
		ByteArrayInputStream stream = new ByteArrayInputStream(serializedData);
		return new InputRepresentation(stream, mediaType);
	}
	
	public static InputStream callGetAndReturnStream(ClientResource clientResource) {
		try {
			return clientResource.get().getStream();
		} catch (ResourceException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
