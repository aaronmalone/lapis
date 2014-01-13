package edu.osu.lapis.transmission;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ClientResponse {
	final private int statusCode;
	final private byte[] payload;
	
	public ClientResponse(int statusCode, byte[] payload) {
		this.statusCode = statusCode;
		this.payload = payload;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public byte[] getPayload() {
		return payload;
	}
	
	public InputStream getStream() {
		return new ByteArrayInputStream(payload);
	}
}
