package edu.osu.lapis.exception;

@SuppressWarnings("serial")
public class LapisClientExceptionWithStatusCode extends LapisClientException {

	private final int statusCode;

	public LapisClientExceptionWithStatusCode(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public LapisClientExceptionWithStatusCode(String message, Throwable cause, int statusCode) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public LapisClientExceptionWithStatusCode(Throwable cause, int statusCode) {
		super(cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
