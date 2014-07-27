package edu.osu.lapis.client;


import edu.osu.lapis.util.ClientUtils;

public abstract class AbstractClient implements Client {
	@Override
	public byte[] doCall(ClientMethod method, String... uriParts) {
		String uri = ClientUtils.buildUri(uriParts);
		return doCall(method, uri);
	}

	@Override
	public byte[] doCall(ClientMethod method, byte[] messageBody, String... uriParts) {
		String uri = ClientUtils.buildUri(uriParts);
		return doCall(method, messageBody, uri);
	}

}
