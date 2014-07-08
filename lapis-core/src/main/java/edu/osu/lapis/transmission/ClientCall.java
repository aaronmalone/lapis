package edu.osu.lapis.transmission;

public class ClientCall {
	public static enum RestMethod {
		PUT, GET, POST, DELETE
	}
	
	final private RestMethod method;
	final private String uri;
	final private byte[] messageBody;
	
	public ClientCall(RestMethod method, String uri) {
		this(method, uri, (byte[])null);
	}
	
	public ClientCall(RestMethod method, String uri, byte[] messageBody) {
		this.method = method;
		this.uri = uri;
		this.messageBody = messageBody;
	}
	
	public ClientCall(RestMethod method, String uri, String messageBody) {
		this(method,uri, messageBody.getBytes());
	}

	public RestMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public byte[] getMessageBody() {
		return messageBody;
	}

	@Override
	public String toString() {
		return "ClientCall(" + method + ", " + uri + ")";
	}
}
