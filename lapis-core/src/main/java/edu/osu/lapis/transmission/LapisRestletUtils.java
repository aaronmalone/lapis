package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.restlet.Message;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import edu.osu.lapis.network.LapisNode;
import edu.osu.lapis.serialization.LapisSerialization;

public class LapisRestletUtils {
	
	//TODO HOW DO WE USE THIS METHOD?
	//look at how we might use on responses
	
	public static LapisNode getLapisNodeFromMessageBody(Message message, LapisSerialization lapisSerialization) {
		try (InputStream stream = getStreamAndPutBackInEntity(message)) {
			//TODO CLEANUP, maybe setting lapis node from message body in attribute
			if(stream == null) {
				return null;
			} else {
				stream.mark(Integer.MAX_VALUE);
				if(lapisSerialization == null) System.out.println("it's null!");//TODO REMVOE
				LapisNode node = lapisSerialization.deserializeLapisNode(stream);
				stream.reset();
				return node;
			}
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
	
	//TODO MOVE CREATE_REPRESENTATION METHODS TO ANOTHER UTILITY
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
	
	/*
	 * InputRepresentation nullifies the stream when getStream() is called, 
	 * so we use this method to immediately set the stream again.
	 */
	private static InputStream getStreamAndPutBackInEntity(Message message) throws IOException {
		Representation entity = message.getEntity();
		InputStream stream = entity.getStream();
		if(stream != null) {
			if(entity instanceof InputRepresentation) {
				((InputRepresentation)entity).setStream(stream);
			}
		}
		return stream;
		
	}
}
