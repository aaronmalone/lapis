package edu.osu.lapis.comm.client;


public interface Client {

	public byte[] doCall(Method method, String... uriParts);

	public byte[] doCall(Method method, String uri);

	public byte[] doCall(Method method, byte[] messageBody, String... uriParts);

	public byte[] doCall(Method method, byte[] messageBody, String uri);
}
