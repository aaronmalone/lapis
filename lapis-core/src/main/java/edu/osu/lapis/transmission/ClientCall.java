package edu.osu.lapis.transmission;

public class ClientCall {
	public static enum RestMethod {
		PUT, GET, POST, DELETE
	}
	
	final private RestMethod method;
	final private String uri;
	final private byte[] payload;
	
	public ClientCall(RestMethod method, String uri) {
		this(method, uri, (byte[])null);
	}
	
	public ClientCall(RestMethod method, String uri, byte[] payload) {
		this.method = method;
		this.uri = uri;
		this.payload = payload;
	}
	
	public ClientCall(RestMethod method, String uri, String payload) {
		this(method,uri,payload.getBytes());
	}

	public RestMethod getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public byte[] getPayload() {
		return payload;
	}
}
