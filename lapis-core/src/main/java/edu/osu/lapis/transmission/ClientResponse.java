package edu.osu.lapis.transmission;

public class ClientResponse {
	final private int statusCode;
	final private byte[] messageBody;
	
	public ClientResponse(int statusCode, byte[] messageBody) {
		this.statusCode = statusCode;
		this.messageBody = messageBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public byte[] getMessageBody() {
		return messageBody;
	}

}
