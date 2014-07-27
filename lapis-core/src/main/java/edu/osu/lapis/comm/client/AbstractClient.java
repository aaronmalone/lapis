package edu.osu.lapis.comm.client;


import edu.osu.lapis.util.LapisRestletUtils;

public abstract class AbstractClient implements Client {
	@Override
	public byte[] doCall(Method method, String... uriParts) {
		String uri = LapisRestletUtils.buildUri(uriParts);
		return doCall(method, uri);
	}

	@Override
	public byte[] doCall(Method method, byte[] messageBody, String... uriParts) {
		String uri = LapisRestletUtils.buildUri(uriParts);
		return doCall(method, messageBody, uri);
	}

}
