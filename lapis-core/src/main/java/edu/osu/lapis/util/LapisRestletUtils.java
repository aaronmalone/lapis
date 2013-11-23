package edu.osu.lapis.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.restlet.Message;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

import com.google.common.io.ByteStreams;

public class LapisRestletUtils {
	
	public static String buildUri(String ... parts) {
		StringBuilder sb = new StringBuilder();
		for(String part : parts) {
			sb.append(trimSlashes(part));
			sb.append('/');
		}
		return sb.substring(0, sb.length()-1); //remove trailing slash
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
	
	public static InputStream getMessageEntityAsStream(Message message) {
		Representation entity = message.getEntity();
		try {
			return entity.getStream();
		} catch (IOException e) {
			throw new RuntimeException("Error retrieving stream from message.", e);
		}
	}
	
	public static byte[] getMessageEntityAsBytes(Message message) {
		InputStream stream = null;
		try {
			stream = getMessageEntityAsStream(message);
			return ByteStreams.toByteArray(stream);
		} catch (IOException e) {
			throw new RuntimeException("Error retrieving data from message.", e);
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new RuntimeException("Error closing stream.", e);
				}
			}
		}
	}
}
