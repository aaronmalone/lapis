package edu.osu.lapis.transmission;

@SuppressWarnings("serial")
public class LapisClientException extends RuntimeException {
	public LapisClientException(String message) {
		super(message);
	}
	
	public LapisClientException(String message, Throwable cause) {
		super(getMessage(message, cause), cause);
	}
	
	private static String getMessage(String message, Throwable cause) {
		if(cause instanceof LapisClientException) {
			String trimmed = message.trim();
			String separator = trimmed.endsWith(".") ? " " : " :: ";
			return trimmed + separator + cause.getMessage();
		} else {
			return message;
		}
	}
}